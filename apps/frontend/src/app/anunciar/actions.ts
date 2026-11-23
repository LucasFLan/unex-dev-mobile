"use server";

import { revalidatePath } from "next/cache";
import { redirect } from "next/navigation";
import { apiFetch, type ApiResult } from "@/lib/api";
import { AD_FIELDS, ENDPOINTS, extractErrorMessage } from "@/lib/api-contract";
import { parseAnuncio, type AnuncioItem } from "@/lib/ads";
import { UFS } from "@/lib/estados";
import { backHrefFrom } from "@/lib/navigation";
import { clearSessionToken } from "@/lib/session";

export type AdFormState = { error: string } | null;

export type CepResult =
  | {
      ok: true;
      cep: string;
      logradouro: string | null;
      bairro: string | null;
      cidade: string | null;
      uf: string | null;
      /** Ponto aproximado (~1 km) para mostrar a região. */
      latitude: number | null;
      longitude: number | null;
    }
  | { ok: false; error: string };

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
  const cep = field(formData, "cep").replace(/\D/g, "");
  const raio = field(formData, "raioAtendimentoKm");

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
  if (cep.length !== 8) return { error: "Informe o CEP (8 dígitos)." };
  if (raio && !/^\d{1,3}$/.test(raio)) return { error: "Escolha até onde você atende." };

  return {
    body: {
      [AD_FIELDS.type]: tipo,
      [AD_FIELDS.categoryId]: categoriaId,
      [AD_FIELDS.title]: titulo,
      [AD_FIELDS.description]: descricao,
      [AD_FIELDS.price]: preco, // null = "a combinar"
      [AD_FIELDS.city]: cidade,
      [AD_FIELDS.state]: uf,
      cep,
      bairro: field(formData, "bairro") || null,
      logradouro: field(formData, "logradouro") || null,
      numero: field(formData, "numero") || null,
      complemento: field(formData, "complemento") || null,
      // Só vale para OFERTA; vazio = "toda a cidade".
      raioAtendimentoKm: tipo === "OFERTA" && raio ? Number(raio) : null,
    },
  };
}

/** GET /cep/{cep}: preenche o endereço do formulário. */
export async function lookupCep(cep: string): Promise<CepResult> {
  const digits = cep.replace(/\D/g, "");
  if (digits.length !== 8) return { ok: false, error: "O CEP tem 8 dígitos." };

  let result: ApiResult;
  try {
    result = await apiFetch(ENDPOINTS.cep(digits), { auth: true });
  } catch {
    return { ok: false, error: CONNECTION_ERROR };
  }
  if (result.status === 401) await handleUnauthorized(result);
  if (result.status === 404) return { ok: false, error: "CEP não encontrado. Confira os números." };
  if (!result.ok) {
    return { ok: false, error: "A busca de CEP está fora do ar. Preencha bairro e cidade à mão." };
  }

  const b = (result.data ?? {}) as Record<string, unknown>;
  const str = (value: unknown) => (typeof value === "string" && value ? value : null);
  const num = (value: unknown) => (typeof value === "number" ? value : null);
  return {
    ok: true,
    cep: digits,
    logradouro: str(b.logradouro),
    bairro: str(b.bairro),
    cidade: str(b.cidade),
    uf: str(b.uf),
    latitude: num(b.latitude),
    longitude: num(b.longitude),
  };
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

  const result = await call(ENDPOINTS.ads, "POST", parsed.body);
  if (!result) return { error: CONNECTION_ERROR };

  await handleUnauthorized(result);
  if (!result.ok) return { error: messageFor(result) };

  revalidateAd();
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

  const result = await call(adPath(id), "PUT", parsed.body);
  if (!result) return { error: CONNECTION_ERROR };

  await handleUnauthorized(result);
  if (!result.ok) return { error: messageFor(result) };

  revalidateAd(id);
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
