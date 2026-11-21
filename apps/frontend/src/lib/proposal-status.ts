import type { ProposalStatus } from "@/lib/proposals";

/** Rótulos e cores dos status (sem dependências de servidor: usado em componentes de cliente). */
export const PROPOSAL_STATUS_LABEL: Record<ProposalStatus, string> = {
  PENDENTE: "Aguardando resposta",
  ACEITA: "Aceita",
  RECUSADA: "Recusada",
  NAO_SELECIONADA: "Não selecionada",
  CANCELADA: "Cancelada",
};

export const PROPOSAL_STATUS_STYLE: Record<ProposalStatus, string> = {
  PENDENTE: "bg-amber-50 text-amber-700",
  ACEITA: "bg-emerald-50 text-emerald-700",
  RECUSADA: "bg-red-50 text-red-600",
  NAO_SELECIONADA: "bg-slate-100 text-slate-600",
  CANCELADA: "bg-slate-100 text-slate-500",
};
