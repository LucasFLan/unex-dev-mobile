"use server";

import { revalidatePath } from "next/cache";
import { redirect } from "next/navigation";
import { apiFetch, type ApiResult } from "@/lib/api";
import { extractErrorMessage } from "@/lib/api-contract";
import { clearSessionToken } from "@/lib/session";
import { VALIDATION_ENDPOINTS } from "@/lib/validation";

export type ReviewState = { error: string } | null;

async function decide(id: string, acao: "aprovar" | "reprovar", motivo?: string): Promise<ReviewState> {
  let result: ApiResult;
  try {
    result = await apiFetch(`${VALIDATION_ENDPOINTS.submission(id)}/${acao}`, {
      method: "PATCH",
      auth: true,
      body: acao === "reprovar" ? { motivo } : undefined,
    });
  } catch {
    return { error: "Não foi possível conectar ao servidor. Tente novamente." };
  }

  if (result.status === 401) {
    await clearSessionToken();
    redirect("/login");
  }
  if (!result.ok) {
    return {
      error:
        result.status === 403
          ? "Só administradores podem analisar documentos."
          : extractErrorMessage(result.data) ?? "Algo deu errado no servidor. Tente novamente.",
    };
  }

  revalidatePath("/admin/validacoes");
  redirect("/admin/validacoes");
}

/** PATCH /admin/validacoes/{id}/aprovar — usado com `approveValidation.bind(null, id)`. */
export async function approveValidation(id: string): Promise<ReviewState> {
  return decide(id, "aprovar");
}

/** PATCH /admin/validacoes/{id}/reprovar com o motivo do formulário. */
export async function rejectValidation(
  id: string,
  _previous: ReviewState,
  formData: FormData
): Promise<ReviewState> {
  const motivo = String(formData.get("motivo") ?? "").trim();
  if (motivo.length < 5) {
    return { error: "Explique o motivo (pelo menos 5 caracteres): o usuário vai ver essa mensagem." };
  }
  return decide(id, "reprovar", motivo);
}
