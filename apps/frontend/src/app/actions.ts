"use server";

import { redirect } from "next/navigation";
import { apiFetch, type ApiResult } from "@/lib/api";
import {
  ENDPOINTS,
  FIELDS,
  extractErrorMessage,
  extractToken,
} from "@/lib/api-contract";
import { clearSessionToken, setSessionToken } from "@/lib/session";
import { isPastDate, onlyDigits, toIsoDate } from "@/lib/validators";

export type AuthState = {
  error: string;
  values?: {
    nome?: string;
    dataNascimento?: string;
    cpf?: string;
    email?: string;
    telefone?: string;
  };
} | null;

const CONNECTION_ERROR =
  "Não foi possível conectar ao servidor. Tente novamente.";

const field = (formData: FormData, key: string) =>
  String(formData.get(key) ?? "").trim();

async function post(path: string, body: Record<string, string>) {
  try {
    return await apiFetch(path, { method: "POST", body });
  } catch {
    return null;
  }
}

function messageFor(result: ApiResult, kind: "login" | "register") {
  const serverMessage = extractErrorMessage(result.data);

  switch (result.status) {
    case 401:
    case 403:
      return kind === "login"
        ? "E-mail ou senha inválidos."
        : "Não foi possível concluir o cadastro.";
    case 409:
      return "Já existe uma conta com este e-mail ou CPF.";
    case 400:
    case 422:
      return serverMessage ?? "Verifique os dados informados.";
    default:
      return "Algo deu errado no servidor. Tente novamente em instantes.";
  }
}

export async function login(
  _previous: AuthState,
  formData: FormData
): Promise<AuthState> {
  const email = field(formData, "email");
  const senha = String(formData.get("senha") ?? "");
  const values = { email };

  if (!email || !senha) {
    return { error: "Informe e-mail e senha.", values };
  }

  const result = await post(ENDPOINTS.login, {
    [FIELDS.email]: email,
    [FIELDS.password]: senha,
  });

  if (!result) return { error: CONNECTION_ERROR, values };
  if (!result.ok) return { error: messageFor(result, "login"), values };

  const token = extractToken(result.data);
  if (!token) {
    return { error: "O servidor não devolveu o token de acesso.", values };
  }

  await setSessionToken(token);
  redirect("/home"); 
}

export async function register(
  _previous: AuthState,
  formData: FormData
): Promise<AuthState> {
  const nome = field(formData, "nome");
  const dataNascimento = toIsoDate(field(formData, "dataNascimento"));
  const cpfDigitado = field(formData, "cpf");
  const email = field(formData, "email");
  const telefoneDigitado = field(formData, "telefone");
  const senha = String(formData.get("senha") ?? "");
  const confirmar = String(formData.get("confirmar") ?? "");

  const values = {
    nome,
    dataNascimento,
    cpf: cpfDigitado,
    email,
    telefone: telefoneDigitado,
  };

  const cpf = onlyDigits(cpfDigitado);
  const telefone = onlyDigits(telefoneDigitado);

  if (!nome || !dataNascimento || !cpf || !email || !telefone || !senha) {
    return { error: "Preencha todos os campos.", values };
  }
  if (cpf.length !== 11) {
    return { error: "CPF inválido. Informe os 11 números.", values };
  }
  if (telefone.length < 10 || telefone.length > 11) {
    return { error: "Telefone inválido. Informe o DDD e o número.", values };
  }
  if (!isPastDate(dataNascimento)) {
    return { error: "Data de nascimento inválida.", values };
  }
  if (senha.length < 8 || !/[A-Za-z]/.test(senha) || !/\d/.test(senha)) {
    return {
      error: "A senha deve ter no mínimo 8 caracteres, com letras e números.",
      values,
    };
  }
  if (formData.has("confirmar") && senha !== confirmar) {
    return { error: "As senhas não conferem.", values };
  }
  if (!formData.get("termos")) {
    return {
      error: "Aceite os Termos de Uso e a Política de Privacidade.",
      values,
    };
  }

  const registered = await post(ENDPOINTS.register, {
    [FIELDS.name]: nome,
    [FIELDS.birthDate]: dataNascimento,
    [FIELDS.cpf]: cpf,
    [FIELDS.email]: email,
    [FIELDS.password]: senha,
    [FIELDS.phone]: telefone,
  });

  if (!registered) return { error: CONNECTION_ERROR, values };
  if (!registered.ok) {
    return { error: messageFor(registered, "register"), values };
  }

  let token = extractToken(registered.data);

  if (!token) {
    const logged = await post(ENDPOINTS.login, {
      [FIELDS.email]: email,
      [FIELDS.password]: senha,
    });
    token = logged?.ok ? extractToken(logged.data) : null;
  }

  if (!token) redirect("/login");

  await setSessionToken(token);
  redirect("/home");
}

export async function logout() {
  await clearSessionToken();
  redirect("/login");
}