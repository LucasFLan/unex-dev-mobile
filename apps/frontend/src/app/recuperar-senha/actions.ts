"use server";

import { apiFetch, type ApiResult } from "@/lib/api";
import { extractErrorMessage } from "@/lib/api-contract";
import { PASSWORD_RULE_MESSAGE, isValidPassword, onlyDigits } from "@/lib/validators";

export type RecoveryStep = "email" | "codigo" | "senha" | "concluido";

export type RecoveryState = {
  step: RecoveryStep;
  email: string;
  /** Código já conferido, guardado para a etapa da nova senha. */
  codigo?: string;
  error?: string;
  info?: string;
};

const ENDPOINTS = {
  request: "/auth/recuperar-senha",
  verify: "/auth/recuperar-senha/validar",
  reset: "/auth/recuperar-senha/redefinir",
};

const CONNECTION_ERROR = "Não foi possível conectar ao servidor. Tente novamente.";

const field = (formData: FormData, key: string) =>
  String(formData.get(key) ?? "").trim();

async function post(path: string, body: Record<string, string>): Promise<ApiResult | null> {
  try {
    return await apiFetch(path, { method: "POST", body });
  } catch {
    return null;
  }
}

const errorFor = (result: ApiResult) =>
  extractErrorMessage(result.data) ?? "Algo deu errado no servidor. Tente novamente em instantes.";

/** Uma action para as três etapas: o campo "acao" do formulário diz o que fazer. */
export async function recoverPassword(
  previous: RecoveryState,
  formData: FormData
): Promise<RecoveryState> {
  const acao = field(formData, "acao");

  if (acao === "voltar") return { step: "email", email: previous.email };

  if (acao === "solicitar" || acao === "reenviar") {
    const email = field(formData, "email") || previous.email;
    if (!email) return { ...previous, error: "Informe o e-mail da sua conta." };

    const result = await post(ENDPOINTS.request, { email });
    if (!result) return { ...previous, email, error: CONNECTION_ERROR };
    if (!result.ok) return { ...previous, email, error: errorFor(result) };

    return {
      step: "codigo",
      email,
      info:
        acao === "reenviar"
          ? "Se já passou 1 minuto desde o último envio, mandamos um novo código."
          : undefined,
    };
  }

  if (acao === "validar") {
    const codigo = onlyDigits(field(formData, "codigo"));
    if (codigo.length !== 6) {
      return { ...previous, error: "Digite os 6 números do código." };
    }

    const result = await post(ENDPOINTS.verify, { email: previous.email, codigo });
    if (!result) return { ...previous, error: CONNECTION_ERROR };
    if (!result.ok) return { ...previous, error: errorFor(result) };

    return { step: "senha", email: previous.email, codigo };
  }

  if (acao === "redefinir") {
    const novaSenha = String(formData.get("novaSenha") ?? "");
    const confirmar = String(formData.get("confirmar") ?? "");

    if (!novaSenha || !confirmar) return { ...previous, error: "Preencha os dois campos." };
    if (!isValidPassword(novaSenha)) return { ...previous, error: PASSWORD_RULE_MESSAGE };
    if (novaSenha !== confirmar) {
      return { ...previous, error: "A confirmação não confere com a nova senha." };
    }

    const result = await post(ENDPOINTS.reset, {
      email: previous.email,
      codigo: previous.codigo ?? "",
      novaSenha,
    });
    if (!result) return { ...previous, error: CONNECTION_ERROR };
    if (!result.ok) {
      // Código expirou entre as etapas: volta para pedir outro.
      const message = errorFor(result);
      return /código/i.test(message)
        ? { step: "codigo", email: previous.email, error: message }
        : { ...previous, error: message };
    }

    return { step: "concluido", email: previous.email };
  }

  return previous;
}
