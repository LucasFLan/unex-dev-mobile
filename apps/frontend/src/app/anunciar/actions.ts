"use server";

import { revalidatePath } from "next/cache";
import { redirect } from "next/navigation";
import { apiFetch, apiUpload, type ApiResult } from "@/lib/api";
import { AD_FIELDS, ENDPOINTS, extractErrorMessage } from "@/lib/api-contract";
import { parseAnuncio, type AnuncioItem } from "@/lib/ads";
import { UFS } from "@/lib/estados";
import { backHrefFrom } from "@/lib/navigation";
import {
  ACCEPTED_PHOTO_TYPES,
  MAX_AD_PHOTOS,
  MAX_PHOTO_BYTES,
  PHOTO_NOTICE_PARAM,
} from "@/lib/photos";
import { clearSessionToken } from "@/lib/session";

export type AdFormState = { error: string } | null;

export type AdActionResult =
  | { ok: true; ad: AnuncioItem | null }
  | { ok: false; error: string };

const AD_TYPES = ["OFERTA", "PEDIDO"];

const CONNECTION_ERROR =
  "Não foi possível conectar ao servidor. Tente novamente.";

const field = (formData: FormData, key: string) =>
  String(formData.get(key) ?? "").trim();

const adPath = (id: string) => `${ENDPOINTS.ads}/${encodeURIComponent(id)}`;

function parsePrice(text: string): number | null | undefined {
  if (!text) return null;

  const normalized = text.replace(",", ".");
  if (!/^\d+(\.\d{1,2})?$/.test(normalized)) return undefined;

  const value = Number(normalized);
  return value > 0 ? value : undefined;
}

function messageFor(result: ApiResult) {
  const serverMessage = extractErrorMessage(result.data);

  switch (result.status) {
    case 400:
    case 422:
      return serverMessage ?? "Verifique os dados do anúncio.";
    case 403:
      return serverMessage ?? "Você não tem permissão para alterar este anúncio.";
    case 404:
      return "Anúncio não encontrado.";
    default:
      return "Algo deu errado no servidor. Tente novamente em instantes.";
  }
}

/** Valida o formulário e monta o corpo de POST/PUT /anuncios. */
function readAdForm(
  formData: FormData
): { body: Record<string, unknown> } | { error: string } {
  const tipo = field(formData, "tipo");
  const categoriaId = field(formData, "categoriaId");
  const titulo = field(formData, "titulo");
  const descricao = field(formData, "descricao");
  const cidade = field(formData, "cidade");
  const uf = field(formData, "uf");
  const preco = parsePrice(field(formData, "preco"));

  if (!AD_TYPES.includes(tipo)) {
    return { error: "Escolha se você procura ou oferece um serviço." };
  }
  if (!categoriaId) return { error: "Escolha uma categoria." };
  if (titulo.length < 3 || titulo.length > 120) {
    return { error: "O título deve ter entre 3 e 120 caracteres." };
  }
  if (!descricao) return { error: "Descreva o serviço." };
  if (descricao.length > 5000) {
    return { error: "A descrição pode ter no máximo 5000 caracteres." };
  }
  if (preco === undefined) {
    return {
      error: "Preço inválido. Use só números, com até 2 casas decimais.",
    };
  }
  if (!cidade || cidade.length > 100) {
    return { error: "Informe a cidade (até 100 caracteres)." };
  }
  if (!UFS.includes(uf)) return { error: "Escolha o estado." };

  return {
    body: {
      [AD_FIELDS.type]: tipo,
      [AD_FIELDS.categoryId]: categoriaId,
      [AD_FIELDS.title]: titulo,
      [AD_FIELDS.description]: descricao,
      [AD_FIELDS.price]: preco, // null = "a combinar"
      [AD_FIELDS.city]: cidade,
      [AD_FIELDS.state]: uf,
    },
  };
}

/** Fotos novas do formulário (já reduzidas no navegador). */
function readPhotos(formData: FormData): { photos: File[] } | { error: string } {
  const photos = formData
    .getAll("fotos")
    .filter((value): value is File => value instanceof File && value.size > 0);

  if (photos.length > MAX_AD_PHOTOS) {
    return { error: `O anúncio pode ter até ${MAX_AD_PHOTOS} fotos.` };
  }
  for (const photo of photos) {
    if (photo.size > MAX_PHOTO_BYTES) return { error: "Cada foto pode ter no máximo 5 MB." };
    if (photo.type && !ACCEPTED_PHOTO_TYPES.includes(photo.type)) {
      return { error: "Envie as fotos em JPG, PNG ou WEBP." };
    }
  }
  return { photos };
}

/** POST /anuncios/{id}/fotos (multipart, campo "fotos" repetido). Devolve a mensagem de erro, se houver. */
async function uploadPhotos(id: string, photos: File[]): Promise<string | null> {
  if (photos.length === 0) return null;

  const form = new FormData();
  photos.forEach((photo, index) => form.append("fotos", photo, photo.name || `foto-${index + 1}.jpg`));

  let result: ApiResult;
  try {
    result = await apiUpload(`${adPath(id)}/fotos`, form);
  } catch {
    return CONNECTION_ERROR;
  }
  await handleUnauthorized(result);
  if (result.ok) return null;
  return result.status === 413
    ? "As fotos são grandes demais (máximo 5 MB cada)."
    : messageFor(result);
}

async function call(
  path: string,
  method: "POST" | "PUT" | "PATCH" | "DELETE",
  body?: unknown
): Promise<ApiResult | null> {
  try {
    return await apiFetch(path, { method, auth: true, body });
  } catch {
    return null;
  }
}

/** Sessão expirada: limpa o cookie e manda para o login. */
async function handleUnauthorized(result: ApiResult) {
  if (result.status === 401) {
    await clearSessionToken();
    redirect("/login");
  }
}

function revalidateAd(id?: string) {
  revalidatePath("/home");
  revalidatePath("/historico");
  revalidatePath("/perfil");
  if (id) {
    revalidatePath(`/servicos/${id}`);
    revalidatePath(`/profissionais/${id}`);
  }
}

export async function createAd(
  _previous: AdFormState,
  formData: FormData
): Promise<AdFormState> {
  const parsed = readAdForm(formData);
  if ("error" in parsed) return parsed;
  const photos = readPhotos(formData);
  if ("error" in photos) return photos;

  const result = await call(ENDPOINTS.ads, "POST", parsed.body);
  if (!result) return { error: CONNECTION_ERROR };

  await handleUnauthorized(result);
  if (!result.ok) return { error: messageFor(result) };

  const created = parseAnuncio(result.data);
  revalidateAd(created?.id);

  // O anúncio já existe: se as fotos falharem, abre a edição para tentar de novo em vez de perder o anúncio.
  if (created && (await uploadPhotos(created.id, photos.photos))) {
    redirect(`/anunciar/${encodeURIComponent(created.id)}/editar?${PHOTO_NOTICE_PARAM}=1`);
  }
  redirect("/historico");
}

/** PUT /anuncios/{id}: usado com `updateAd.bind(null, id)` no formulário. */
export async function updateAd(
  id: string,
  _previous: AdFormState,
  formData: FormData
): Promise<AdFormState> {
  const parsed = readAdForm(formData);
  if ("error" in parsed) return parsed;
  const photos = readPhotos(formData);
  if ("error" in photos) return photos;

  const result = await call(adPath(id), "PUT", parsed.body);
  if (!result) return { error: CONNECTION_ERROR };

  await handleUnauthorized(result);
  if (!result.ok) return { error: messageFor(result) };

  revalidateAd(id);

  // Remove antes de enviar, para as novas caberem no limite de fotos. Foto já removida (404) não é erro.
  for (const photoId of formData.getAll("removerFoto").map(String)) {
    const removed = await call(`${adPath(id)}/fotos/${encodeURIComponent(photoId)}`, "DELETE");
    if (!removed) return { error: `Os dados foram salvos, mas ${CONNECTION_ERROR.toLowerCase()}` };
    await handleUnauthorized(removed);
    if (!removed.ok && removed.status !== 404) {
      return { error: `Os dados foram salvos, mas uma foto não foi removida: ${messageFor(removed)}` };
    }
  }

  const photoError = await uploadPhotos(id, photos.photos);
  if (photoError) {
    return { error: `Os dados foram salvos, mas as fotos novas não foram enviadas: ${photoError}` };
  }
  // Volta para onde a edição foi aberta (Histórico ou detalhe do anúncio).
  redirect(backHrefFrom(field(formData, "voltar"), "/historico"));
}

/** PATCH /anuncios/{id}/ativar ou /desativar. */
export async function setAdActive(
  id: string,
  active: boolean
): Promise<AdActionResult> {
  const result = await call(
    `${adPath(id)}/${active ? "ativar" : "desativar"}`,
    "PATCH"
  );
  if (!result) return { ok: false, error: CONNECTION_ERROR };

  await handleUnauthorized(result);
  if (!result.ok) return { ok: false, error: messageFor(result) };

  revalidateAd(id);
  return { ok: true, ad: parseAnuncio(result.data) };
}

/** DELETE /anuncios/{id} (definitivo). */
export async function deleteAd(id: string): Promise<AdActionResult> {
  const result = await call(adPath(id), "DELETE");
  if (!result) return { ok: false, error: CONNECTION_ERROR };

  await handleUnauthorized(result);
  if (!result.ok) return { ok: false, error: messageFor(result) };

  revalidateAd(id);
  return { ok: true, ad: null };
}
