"use server";

import { revalidatePath } from "next/cache";
import { redirect } from "next/navigation";
import { apiFetch, apiUpload, type ApiResult } from "@/lib/api";
import { ENDPOINTS, extractErrorMessage } from "@/lib/api-contract";
import { MAX_PROPOSAL_PHOTO_BYTES, MAX_PROPOSAL_PHOTOS } from "@/lib/proposal-limits";
import { clearSessionToken } from "@/lib/session";

export type ProposalFormState =
  | { error: string }
  | { success: true }
  | null;

const MAX_MESSAGE = 1000;

function parseValue(text: string): number | null | undefined {
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
    case 409:
    case 422:
      return serverMessage ?? "Verifique os dados da proposta.";
    case 404:
      return "Este anúncio não está mais disponível.";
    default:
      return "Algo deu errado no servidor. Tente novamente em instantes.";
  }
}

/** POST /anuncios/{id}/propostas: usado com `sendProposal.bind(null, adId)`. */
export async function sendProposal(
  adId: string,
  _previous: ProposalFormState,
  formData: FormData
): Promise<ProposalFormState> {
  const valor = parseValue(String(formData.get("valor") ?? "").trim());
  const mensagem = String(formData.get("mensagem") ?? "").trim();

  if (valor === undefined) {
    return { error: "Valor inválido. Use só números, com até 2 casas decimais." };
  }
  if (!mensagem) return { error: "Escreva uma mensagem para o anunciante." };
  if (mensagem.length > MAX_MESSAGE) {
    return { error: `A mensagem pode ter no máximo ${MAX_MESSAGE} caracteres.` };
  }

  const fotos = formData
    .getAll("fotos")
    .filter((foto): foto is File => foto instanceof File && foto.size > 0);
  if (fotos.length > MAX_PROPOSAL_PHOTOS) {
    return { error: `Anexe no máximo ${MAX_PROPOSAL_PHOTOS} fotos.` };
  }
  if (fotos.some((foto) => foto.size > MAX_PROPOSAL_PHOTO_BYTES)) {
    return { error: "Cada foto pode ter no máximo 5 MB." };
  }

  let result: ApiResult;
  try {
    if (fotos.length === 0) {
      result = await apiFetch(ENDPOINTS.proposals(adId), {
        method: "POST",
        auth: true,
        body: { valor, mensagem },
      });
    } else {
      // Com fotos, a mesma rota recebe multipart: proposta e fotos são gravadas juntas.
      const form = new FormData();
      if (valor !== null) form.set("valor", valor.toFixed(2));
      form.set("mensagem", mensagem);
      fotos.forEach((foto, index) => form.append("fotos", foto, foto.name || `foto-${index + 1}.jpg`));
      result = await apiUpload(ENDPOINTS.proposals(adId), form);
    }
  } catch {
    return { error: "Não foi possível conectar ao servidor. Tente novamente." };
  }

  if (result.status === 401) {
    await clearSessionToken();
    redirect("/login");
  }

  if (!result.ok) return { error: messageFor(result) };

  revalidatePath("/historico");
  return { success: true };
}

export type ProposalAction = "aceitar" | "recusar" | "cancelar";

/** PATCH /propostas/{id}/aceitar | recusar | cancelar. */
export async function respondProposal(
  proposalId: string,
  action: ProposalAction
): Promise<{ ok: true } | { ok: false; error: string }> {
  let result: ApiResult;
  try {
    result = await apiFetch(
      `/propostas/${encodeURIComponent(proposalId)}/${action}`,
      { method: "PATCH", auth: true }
    );
  } catch {
    return {
      ok: false,
      error: "Não foi possível conectar ao servidor. Tente novamente.",
    };
  }

  if (result.status === 401) {
    await clearSessionToken();
    redirect("/login");
  }

  if (!result.ok) {
    const serverMessage = extractErrorMessage(result.data);
    return {
      ok: false,
      error:
        result.status === 404
          ? "Esta proposta não está mais disponível."
          : serverMessage ?? "Algo deu errado no servidor. Tente novamente.",
    };
  }

  // Aceitar um pedido o pausa: Home, Histórico e detalhes mudam.
  revalidatePath("/home");
  revalidatePath("/historico");
  revalidatePath("/servicos/[id]", "page");
  revalidatePath("/profissionais/[id]", "page");
  return { ok: true };
}
