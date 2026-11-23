import { cache } from "react";
import { redirect } from "next/navigation";
import { apiFetch, type ApiResult } from "@/lib/api";

export type ValidationSituation = "NAO_ENVIADO" | "EM_ANALISE" | "APROVADO" | "REPROVADO";

export type MyValidation = {
  situacao: ValidationSituation;
  podeEnviar: boolean;
  enviadoEm: string | null;
  analisadoEm: string | null;
  motivoReprovacao: string | null;
};

export type ReviewStatus = "PENDENTE" | "APROVADO" | "REPROVADO";

export type ValidationSubmission = {
  id: string;
  usuario: {
    id: string;
    nome: string;
    email: string;
    cpf: string;
    dataNascimento: string;
    telefone: string | null;
  } | null;
  status: ReviewStatus;
  enviadoEm: string;
  analisadoEm: string | null;
  motivoReprovacao: string | null;
};

export const VALIDATION_ENDPOINTS = {
  mine: "/usuarios/me/validacao",
  queue: "/admin/validacoes",
  submission: (id: string) => `/admin/validacoes/${encodeURIComponent(id)}`,
  file: (id: string, tipo: "DOCUMENTO" | "SELFIE") =>
    `/admin/validacoes/${encodeURIComponent(id)}/arquivos/${tipo}`,
};

const SITUATIONS: ValidationSituation[] = ["NAO_ENVIADO", "EM_ANALISE", "APROVADO", "REPROVADO"];
const STATUSES: ReviewStatus[] = ["PENDENTE", "APROVADO", "REPROVADO"];

const text = (value: unknown) => (typeof value === "string" ? value : "");
const optional = (value: unknown) => (typeof value === "string" && value ? value : null);
const record = (value: unknown) =>
  typeof value === "object" && value !== null ? (value as Record<string, unknown>) : null;

async function request(path: string): Promise<ApiResult> {
  let result: ApiResult;
  try {
    result = await apiFetch(path, { auth: true });
  } catch {
    throw new Error("Não foi possível conectar ao servidor.");
  }
  if (result.status === 401) redirect("/api/logout");
  return result;
}

/** GET /usuarios/me/validacao. */
export const getMinhaValidacao = cache(async (): Promise<MyValidation> => {
  const result = await request(VALIDATION_ENDPOINTS.mine);
  if (!result.ok) {
    throw new Error(`Falha ao carregar a validação (status ${result.status}).`);
  }

  const b = record(result.data) ?? {};
  const situacao = text(b.situacao) as ValidationSituation;
  return {
    situacao: SITUATIONS.includes(situacao) ? situacao : "NAO_ENVIADO",
    podeEnviar: b.podeEnviar === true,
    enviadoEm: optional(b.enviadoEm),
    analisadoEm: optional(b.analisadoEm),
    motivoReprovacao: optional(b.motivoReprovacao),
  };
});

export function parseSubmission(value: unknown): ValidationSubmission | null {
  const b = record(value);
  const id = text(b?.id);
  if (!b || !id) return null;

  const u = record(b.usuario);
  const status = text(b.status) as ReviewStatus;
  return {
    id,
    usuario: u
      ? {
          id: text(u.id),
          nome: text(u.nome),
          email: text(u.email),
          cpf: text(u.cpf),
          dataNascimento: text(u.dataNascimento),
          telefone: optional(u.telefone),
        }
      : null,
    status: STATUSES.includes(status) ? status : "PENDENTE",
    enviadoEm: text(b.enviadoEm),
    analisadoEm: optional(b.analisadoEm),
    motivoReprovacao: optional(b.motivoReprovacao),
  };
}

/** GET /admin/validacoes?status=... (só ADMIN). Devolve null se o usuário não for admin. */
export async function getFilaValidacao(
  status: ReviewStatus
): Promise<{ itens: ValidationSubmission[]; total: number } | null> {
  const result = await request(`${VALIDATION_ENDPOINTS.queue}?status=${status}&tamanho=50`);
  if (result.status === 403) return null;
  if (!result.ok) {
    throw new Error(`Falha ao carregar a fila de validação (status ${result.status}).`);
  }

  const b = record(result.data) ?? {};
  const itens = Array.isArray(b.itens) ? b.itens : [];
  return {
    itens: itens.map(parseSubmission).filter((item): item is ValidationSubmission => item !== null),
    total: typeof b.totalItens === "number" ? b.totalItens : itens.length,
  };
}

/** GET /admin/validacoes/{id}. null = não encontrado ou sem permissão. */
export async function getEnvioValidacao(id: string): Promise<ValidationSubmission | null> {
  const result = await request(VALIDATION_ENDPOINTS.submission(id));
  if (result.status === 403 || result.status === 404 || result.status === 400) return null;
  if (!result.ok) {
    throw new Error(`Falha ao carregar o envio (status ${result.status}).`);
  }
  return parseSubmission(result.data);
}
