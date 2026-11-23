"use client";

import { useState, useTransition } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { ChevronRight, Send } from "lucide-react";
import { respondProposal } from "@/app/propostas/actions";
import { ProposalContact } from "@/components/ProposalContact";
import { ProposalPhotos } from "@/components/ProposalPhotos";
import { ProposalStatusBadge } from "@/components/ProposalStatusBadge";
import { RelativeTime } from "@/components/RelativeTime";
import { formatPrice } from "@/lib/format";
import { withFrom } from "@/lib/navigation";
import type { ProposalItem } from "@/lib/proposals";

/** Propostas que o usuário enviou, com o andamento de cada uma. */
export function SentProposals({ proposals }: { proposals: ProposalItem[] }) {
  const router = useRouter();
  const [pending, startTransition] = useTransition();
  const [error, setError] = useState("");

  const cancel = (proposal: ProposalItem) => {
    if (!window.confirm("Cancelar esta proposta? Não será possível enviar outra para o mesmo anúncio.")) {
      return;
    }
    startTransition(async () => {
      setError("");
      const result = await respondProposal(proposal.id, "cancelar");
      if (!result.ok) setError(result.error);
      else router.refresh();
    });
  };

  if (proposals.length === 0) {
    return (
      <div className="rounded-xl border border-slate-200 bg-white px-4 py-10 text-center">
        <Send size={25} className="mx-auto mb-2 text-slate-300" aria-hidden="true" />
        <p className="text-xs font-semibold text-slate-600">
          Você ainda não enviou propostas
        </p>
        <p className="mt-1 text-[11px] text-slate-500">
          Abra um pedido ou uma oferta na Início para enviar a sua.
        </p>
      </div>
    );
  }

  return (
    <div className="space-y-2">
      {error && (
        <p
          role="alert"
          className="rounded-lg bg-red-50 px-3 py-2 text-[11px] text-red-600"
        >
          {error}
        </p>
      )}

      {proposals.map((proposal) => {
        const ad = proposal.anuncio;
        // Pedido respondido fica pausado e não abre mais para quem enviou.
        const detail = !ad
          ? null
          : ad.tipo === "OFERTA"
            ? `/profissionais/${ad.id}`
            : proposal.status === "PENDENTE"
              ? `/servicos/${ad.id}`
              : null;
        const href = detail && withFrom(detail, "/historico?aba=propostas");

        return (
          <article
            key={proposal.id}
            className="rounded-xl border border-slate-200 bg-white p-2.5 shadow-sm"
          >
            <div className="flex items-start gap-2">
              <div className="min-w-0 flex-1">
                <div className="mb-1 flex flex-wrap items-center gap-1">
                  <span className="rounded bg-purple-50 px-1.5 py-0.5 text-[10px] font-semibold text-purple-600">
                    {ad?.tipo === "OFERTA" ? "Orçamento" : "Proposta"}
                  </span>
                  <ProposalStatusBadge status={proposal.status} />
                </div>

                <h3 className="text-xs font-semibold leading-snug text-slate-800">
                  {ad?.titulo ?? "Anúncio removido"}
                </h3>
                <p className="mt-0.5 text-[10px] text-slate-500">
                  Para {proposal.destinatario.nome} ·{" "}
                  <RelativeTime isoDate={proposal.criadoEm} />
                </p>
                <p className="mt-0.5 text-[11px] font-semibold text-slate-800">
                  {formatPrice(proposal.valor)}
                </p>
              </div>

              {href && (
                <Link
                  href={href}
                  aria-label="Ver anúncio"
                  className="-mr-2 -mt-2 flex h-10 w-10 shrink-0 items-center justify-center text-slate-500"
                >
                  <ChevronRight size={17} aria-hidden="true" />
                </Link>
              )}
            </div>

            <ProposalPhotos proposalId={proposal.id} photos={proposal.fotos} />

            {proposal.status === "ACEITA" && (
              <div className="mt-2 border-t border-slate-100 pt-2">
                <ProposalContact person={proposal.destinatario} />
              </div>
            )}

            {proposal.status === "PENDENTE" && (
              <div className="mt-2 flex justify-end">
                <button
                  type="button"
                  onClick={() => cancel(proposal)}
                  disabled={pending}
                  className="h-8 rounded-md px-3 text-[10px] font-medium text-red-500 active:bg-red-50 disabled:opacity-50"
                >
                  Cancelar proposta
                </button>
              </div>
            )}
          </article>
        );
      })}
    </div>
  );
}
