"use client";

import { useState, useTransition } from "react";
import { useRouter } from "next/navigation";
import { Check, X } from "lucide-react";
import { respondProposal } from "@/app/propostas/actions";
import { ProposalContact } from "@/components/ProposalContact";
import { ProposalStatusBadge } from "@/components/ProposalStatusBadge";
import { RelativeTime } from "@/components/RelativeTime";
import { formatPrice } from "@/lib/format";
import { Avatar } from "@/components/Avatar";
import { ProposalPhotos } from "@/components/ProposalPhotos";
import { ServiceProgress } from "@/components/ServiceProgress";
import type { ProposalItem } from "@/lib/proposals";

type ReceivedProposalsProps = {
  proposals: ProposalItem[];
  /** PEDIDO: aceitar uma pausa o pedido e não seleciona as outras. */
  adType: "OFERTA" | "PEDIDO";
};

/** Propostas recebidas em um anúncio, vistas pelo autor no detalhe, com aceitar e recusar. */
export function ReceivedProposals({ proposals, adType }: ReceivedProposalsProps) {
  const router = useRouter();
  const [pending, startTransition] = useTransition();
  const [busyId, setBusyId] = useState<string | null>(null);
  const [error, setError] = useState("");

  const respond = (proposal: ProposalItem, action: "aceitar" | "recusar") => {
    const question =
      action === "aceitar"
        ? adType === "PEDIDO"
          ? `Aceitar a proposta de ${proposal.remetente.nome}? O pedido será pausado e as outras propostas serão marcadas como não selecionadas.`
          : `Aceitar o pedido de orçamento de ${proposal.remetente.nome}? Os contatos de vocês serão liberados.`
        : `Recusar a proposta de ${proposal.remetente.nome}?`;
    if (!window.confirm(question)) return;

    setBusyId(proposal.id);
    setError("");
    startTransition(async () => {
      const result = await respondProposal(proposal.id, action);
      setBusyId(null);
      if (!result.ok) setError(result.error);
      else router.refresh();
    });
  };

  const pendingCount = proposals.filter((p) => p.status === "PENDENTE").length;

  return (
    <section className="mt-4 lg:mt-8">
      <h3 className="text-[11px] font-bold lg:text-base">
        Propostas recebidas ({proposals.length})
        {pendingCount > 0 && (
          <span className="ml-1 font-normal text-amber-700">
            · {pendingCount} aguardando você
          </span>
        )}
      </h3>

      {error && (
        <p
          role="alert"
          className="mt-2 rounded-lg bg-red-50 px-3 py-2 text-[10px] text-red-600 lg:text-sm"
        >
          {error}
        </p>
      )}

      {proposals.length === 0 ? (
        <p className="mt-2 rounded-xl border border-dashed border-slate-200 px-3 py-4 text-center text-[10px] text-slate-500 lg:py-8 lg:text-sm">
          Ninguém enviou proposta ainda.
        </p>
      ) : (
        <ul className="mt-2 space-y-2 lg:mt-3 lg:grid lg:grid-cols-2 lg:gap-3 lg:space-y-0">
          {proposals.map((proposal) => {
            const busy = pending && busyId === proposal.id;

            return (
              <li
                key={proposal.id}
                className={`rounded-xl border p-2.5 ${
                  proposal.status === "ACEITA"
                    ? "border-emerald-200 bg-emerald-50/40"
                    : proposal.status === "CONCLUIDA"
                    ? "border-blue-200 bg-blue-50/30"
                    : "border-slate-200"
                }`}
              >
                <div className="flex items-center gap-2">
                  <Avatar
                    userId={proposal.remetente.id}
                    name={proposal.remetente.nome}
                    className="h-8 w-8 text-[10px] lg:h-10 lg:w-10 lg:text-xs"
                  />

                  <div className="min-w-0 flex-1">
                    <div className="flex items-center gap-1">
                      <p className="truncate text-[10px] font-semibold lg:text-sm">
                        {proposal.remetente.nome}
                      </p>
                      <ProposalStatusBadge status={proposal.status} />
                    </div>
                    <p className="text-[9px] text-slate-500 lg:text-xs">
                      <RelativeTime isoDate={proposal.criadoEm} />
                    </p>
                  </div>

                  <strong className="shrink-0 text-[11px] lg:text-sm">
                    {formatPrice(proposal.valor)}
                  </strong>
                </div>

                <p className="mt-2 whitespace-pre-line text-[10px] leading-3.5 text-slate-600 lg:text-sm lg:leading-5">
                  {proposal.mensagem}
                </p>

                <ProposalPhotos proposalId={proposal.id} photos={proposal.fotos} />

                {proposal.status === "PENDENTE" && (
                  <div className="mt-2 grid grid-cols-2 gap-1.5">
                    <button
                      type="button"
                      onClick={() => respond(proposal, "recusar")}
                      disabled={pending}
                      className="flex h-8 items-center justify-center gap-1 rounded-md border border-slate-200 text-[10px] font-medium text-slate-700 disabled:opacity-50 lg:h-9 lg:text-sm"
                    >
                      <X size={12} aria-hidden="true" />
                      Recusar
                    </button>
                    <button
                      type="button"
                      onClick={() => respond(proposal, "aceitar")}
                      disabled={pending}
                      className="flex h-8 items-center justify-center gap-1 rounded-md bg-blue-600 text-[10px] font-semibold text-white disabled:opacity-50 lg:h-9 lg:text-sm"
                    >
                      <Check size={12} aria-hidden="true" />
                      {busy ? "Enviando..." : "Aceitar"}
                    </button>
                  </div>
                )}

                {(proposal.status === "ACEITA" || proposal.status === "CONCLUIDA") && (
                  <div className="mt-2 border-t border-emerald-100 pt-2">
                    <ProposalContact person={proposal.remetente} />
                    {/* Num pedido, quem contratou é o autor (você); numa oferta, é quem pediu o orçamento. */}
                    <ServiceProgress
                      proposal={proposal}
                      isClient={adType === "PEDIDO"}
                      professionalName={proposal.remetente.nome}
                    />
                  </div>
                )}
              </li>
            );
          })}
        </ul>
      )}
    </section>
  );
}
