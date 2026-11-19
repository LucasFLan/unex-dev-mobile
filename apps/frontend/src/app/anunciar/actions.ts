"use server";

import { revalidatePath } from "next/cache";
import { redirect } from "next/navigation";
import { apiFetch, type ApiResult } from "@/lib/api";
import { AD_FIELDS, ENDPOINTS, extractErrorMessage } from "@/lib/api-contract";
import { UFS } from "@/lib/estados";
import { clearSessionToken } from "@/lib/session";

export type AdFormState = { error: string } | null;

const AD_TYPES = ["OFERTA", "PEDIDO"];

const field = (formData: FormData, key: string) =>
  String(formData.get(key) ?? "").trim();

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
      return "Você não tem permissão para criar anúncios.";
    default:
      return "Algo deu errado no servidor. Tente novamente em instantes.";
  }
}

export async function createAd(
  _previous: AdFormState,
  formData: FormData
): Promise<AdFormState> {
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

  let result: ApiResult;
  try {
    result = await apiFetch(ENDPOINTS.ads, {
      method: "POST",
      auth: true,
      body: {
        [AD_FIELDS.type]: tipo,
        [AD_FIELDS.categoryId]: categoriaId,
        [AD_FIELDS.title]: titulo,
        [AD_FIELDS.description]: descricao,
        [AD_FIELDS.price]: preco, // null = "a combinar"
        [AD_FIELDS.city]: cidade,
        [AD_FIELDS.state]: uf,
      },
    });
  } catch {
    return {
      error: "Não foi possível conectar ao servidor. Tente novamente.",
    };
  }

  if (result.status === 401) {
    await clearSessionToken();
    redirect("/login");
  }

  if (!result.ok) return { error: messageFor(result) };

  revalidatePath("/home");
  revalidatePath("/historico");
  redirect("/historico");
}