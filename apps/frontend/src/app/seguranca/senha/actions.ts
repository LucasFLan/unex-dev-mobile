"use server";

import { redirect } from "next/navigation";
import { apiFetch, type ApiResult } from "@/lib/api";
import {
  ENDPOINTS,
  PASSWORD_FIELDS,
  extractErrorMessage,
  extractFieldErrors,
} from "@/lib/api-contract";
import { clearSessionToken } from "@/lib/session";
import { PASSWORD_RULE_MESSAGE, isValidPassword } from "@/lib/validators";

export type PasswordFormState = { error: string } | { success: true } | null;

const FIELD_LABELS: Record<string, string> = {
  [PASSWORD_FIELDS.current]: "Senha atual",
  [PASSWORD_FIELDS.next]: "Nova senha",
};

function messageFor(result: ApiResult) {
  const serverMessage = extractErrorMessage(result.data);

  switch (result.status) {
    case 400:
    case 422: {
      const fieldErrors = extractFieldErrors(result.data);

      if (fieldErrors.length > 0) {
        return fieldErrors
          .map(({ field, message }) =>
            field && Object.hasOwn(FIELD_LABELS, field)
              ? `${FIELD_LABELS[field]}: ${message}`
              : message
          )
          .join(" ");
      }

      return serverMessage ?? "Verifique os dados informados.";
    }
    case 401:
    case 403:
      return serverMessage ?? "A senha atual está incorreta.";
    default:
      return "Algo deu errado no servidor. Tente novamente em instantes.";
  }
}

export async function changePassword(
  _previous: PasswordFormState,
  formData: FormData
): Promise<PasswordFormState> {
  const senhaAtual = String(formData.get("senhaAtual") ?? "");
  const novaSenha = String(formData.get("novaSenha") ?? "");
  const confirmar = String(formData.get("confirmar") ?? "");

  if (!senhaAtual || !novaSenha || !confirmar) {
    return { error: "Preencha todos os campos." };
  }
  if (!isValidPassword(novaSenha)) return { error: PASSWORD_RULE_MESSAGE };
  if (novaSenha !== confirmar) {
    return { error: "A confirmação não confere com a nova senha." };
  }
  if (novaSenha === senhaAtual) {
    return { error: "A nova senha deve ser diferente da atual." };
  }

  let result: ApiResult;
  try {
    result = await apiFetch(ENDPOINTS.changePassword, {
      method: "PATCH",
      auth: true,
      body: {
        [PASSWORD_FIELDS.current]: senhaAtual,
        [PASSWORD_FIELDS.next]: novaSenha,
      },
    });
  } catch {
    return {
      error: "Não foi possível conectar ao servidor. Tente novamente.",
    };
  }

  if (result.status === 401) {
    const message = extractErrorMessage(result.data);

    if (!message || !/senha/i.test(message)) {
      await clearSessionToken();
      redirect("/login");
    }
  }

  if (!result.ok) {
    if (process.env.NODE_ENV !== "production") {
      console.error(
        "[changePassword] resposta da API:",
        result.status,
        JSON.stringify(result.data)
      );
    }

    return { error: messageFor(result) };
  }

  return { success: true };
}