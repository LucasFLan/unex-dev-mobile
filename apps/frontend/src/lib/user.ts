import { cache } from "react";
import { redirect } from "next/navigation";
import { apiFetch } from "@/lib/api";
import { ENDPOINTS, FIELDS } from "@/lib/api-contract";

export type CurrentUser = {
  id: string;
  name: string;
  email: string;
  phone: string;
  cpf: string;
  birthDate: string;
  verified: boolean;
  role: "USUARIO" | "ADMIN";
  /** Só APROVADO publica anúncios e envia propostas. */
  validation: AccountValidation;
  /** `fotoAtualizadaEm` do perfil: versão da foto, ou `null` sem foto. */
  photoVersion: string | null;
};

export type AccountValidation = "PENDENTE" | "APROVADO" | "REPROVADO";

const text = (value: unknown) => (typeof value === "string" ? value : "");

function parseUser(data: unknown): CurrentUser | null {
  if (typeof data !== "object" || data === null) return null;

  const body = data as Record<string, unknown>;
  const name = text(body[FIELDS.name]);
  const email = text(body[FIELDS.email]);

  if (!name || !email) return null;

  return {
    id: text(body.id),
    name,
    email,
    phone: text(body[FIELDS.phone]),
    cpf: text(body[FIELDS.cpf]),
    birthDate: text(body[FIELDS.birthDate]),
    verified: body.verificado === true,
    role: body.papel === "ADMIN" ? "ADMIN" : "USUARIO",
    validation:
      body.statusValidacao === "APROVADO" || body.statusValidacao === "REPROVADO"
        ? body.statusValidacao
        : "PENDENTE",
    photoVersion: text(body.fotoAtualizadaEm) || null,
  };
}

export const getCurrentUser = cache(async (): Promise<CurrentUser> => {
  let result;

  try {
    result = await apiFetch(ENDPOINTS.me, { auth: true });
  } catch {
    throw new Error("Não foi possível conectar ao servidor.");
  }

  if (result.status === 401 || result.status === 403) {
    redirect("/api/logout");
  }

  if (!result.ok) {
    throw new Error(`Falha ao carregar o usuário (status ${result.status}).`);
  }

  const user = parseUser(result.data);
  if (!user) {
    throw new Error("Resposta inesperada do servidor ao carregar o usuário.");
  }

  return user;
});