import { cache } from "react";
import { redirect } from "next/navigation";
import { apiFetch } from "@/lib/api";

export type ProposalStatus =
  | "PENDENTE"
  | "ACEITA"
  | "RECUSADA"
  | "NAO_SELECIONADA"
  | "CANCELADA";

export type ProposalPerson = {
  id: string;
  nome: string;
  /** Só vem depois que a proposta é aceita. */
  telefone: string | null;
};

export type ProposalItem = {
  id: string;
  anuncio: { id: string; titulo: string; tipo: "OFERTA" | "PEDIDO" } | null;
  remetente: ProposalPerson;
  destinatario: ProposalPerson;
  /** `null` = "a combinar". */
  valor: number | null;
  mensagem: string;
  status: ProposalStatus;
  criadoEm: string;
  respondidoEm: string | null;
  /** Ids das fotos anexadas, na ordem do envio (ver /api/propostas/[id]/fotos/[fotoId]). */
  fotos: string[];
};

const ENDPOINTS = {
  received: "/usuarios/me/propostas/recebidas",
  sent: "/usuarios/me/propostas/enviadas",
};
const MAX_PAGE_SIZE = 50;
const STATUSES: ProposalStatus[] = [
  "PENDENTE",
  "ACEITA",
  "RECUSADA",
  "NAO_SELECIONADA",
  "CANCELADA",
];

const text = (value: unknown) => (typeof value === "string" ? value : "");
const record = (value: unknown) =>
  typeof value === "object" && value !== null
    ? (value as Record<string, unknown>)
    : null;

function person(value: unknown): ProposalPerson {
  const b = record(value) ?? {};
  return {
    id: text(b.id),
    nome: text(b.nome) || "Usuário",
    telefone: text(b.telefone) || null,
  };
}

function parseProposal(item: unknown): ProposalItem | null {
  const b = record(item);
  const id = text(b?.id);
  if (!b || !id) return null;

  const anuncio = record(b.anuncio);
  const status = text(b.status) as ProposalStatus;

  return {
    id,
    anuncio: anuncio
      ? {
          id: text(anuncio.id),
          titulo: text(anuncio.titulo),
          tipo: text(anuncio.tipo) === "OFERTA" ? "OFERTA" : "PEDIDO",
        }
      : null,
    remetente: person(b.remetente),
    destinatario: person(b.destinatario),
    valor: typeof b.valor === "number" ? b.valor : null,
    mensagem: text(b.mensagem),
    status: STATUSES.includes(status) ? status : "PENDENTE",
    criadoEm: text(b.criadoEm),
    respondidoEm: text(b.respondidoEm) || null,
    fotos: Array.isArray(b.fotos) ? b.fotos.filter((foto): foto is string => typeof foto === "string") : [],
  };
}

async function load(path: string): Promise<ProposalItem[]> {
  let result;
  try {
    result = await apiFetch(`${path}?tamanho=${MAX_PAGE_SIZE}`, { auth: true });
  } catch {
    throw new Error("Não foi possível conectar ao servidor.");
  }

  if (result.status === 401 || result.status === 403) redirect("/api/logout");
  if (!result.ok) {
    throw new Error(`Falha ao carregar as propostas (status ${result.status}).`);
  }

  const itens = record(result.data)?.itens;
  return (Array.isArray(itens) ? itens : [])
    .map(parseProposal)
    .filter((item): item is ProposalItem => item !== null);
}

/** GET /usuarios/me/propostas/recebidas: as 50 mais recentes. */
export const getPropostasRecebidas = cache(() => load(ENDPOINTS.received));

/** GET /usuarios/me/propostas/enviadas: as 50 mais recentes. */
export const getPropostasEnviadas = cache(() => load(ENDPOINTS.sent));
