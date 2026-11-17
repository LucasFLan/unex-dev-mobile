export const SESSION_COOKIE = "token";

export const ENDPOINTS = {
  login: "/auth/login",
  register: "/usuarios",
} as const;

export const FIELDS = {
  name: "nome",
  birthDate: "dataNascimento",
  cpf: "cpf",
  email: "email",
  password: "senha",
  phone: "telefone",
} as const;

export function extractToken(data: unknown): string | null {
  if (typeof data !== "object" || data === null) return null;

  const { accessToken } = data as Record<string, unknown>;

  return typeof accessToken === "string" && accessToken.length > 0
    ? accessToken
    : null;
}

export function extractErrorMessage(data: unknown): string | null {
  if (typeof data !== "object" || data === null) return null;

  const body = data as Record<string, unknown>;
  const message = body.message ?? body.detail;

  return typeof message === "string" && message.length > 0 ? message : null;
}