"use client";

import { useState, useTransition } from "react";
import { useRouter } from "next/navigation";
import { respondProposal } from "@/app/propostas/actions";
import { ProposalContact } from "@/components/ProposalContact";
import { ProposalStatusBadge } from "@/components/ProposalStatusBadge";
import { formatPrice } from "@/lib/format";
import type { ProposalItem } from "@/lib/proposals";

/** Celular e tablet: rodapé fixo. Desktop (lg+): bloco dentro do painel lateral do detalhe. */
const WRAPPER = {
  footer:
    "fixed inset-x-0 bottom-0 z-20 border-t border-slate-100 bg-white px-4 pb-[max(20px,env(safe-area-inset-bottom))] pt-2.5 md:sticky md:mx-auto md:max-w-md md:rounded-b-2xl md:border md:border-slate-200 md:shadow-sm lg:hidden",
  panel: "hidden lg:block lg:rounded-xl lg:border lg:border-slate-200 lg:p-4",
} as const;

/** Situação da proposta que o usuário já enviou a este anúncio (rodapé no celular, painel no desktop). */
export function MyProposalStatus({
  proposal,
  variant = "footer",
}: {
  proposal: ProposalItem;
  variant?: keyof typeof WRAPPER;
}) {
  const router = useRouter();
  const [pending, startTransition] = useTransition();
  const [error, setError] = useState("");

  const cancel = () => {
    if (!window.confirm("Cancelar sua proposta? Não será possível enviar outra para este anúncio.")) {
      return;
    }
    startTransition(async () => {
      setError("");
      const result = await respondProposal(proposal.id, "cancelar");
      if (!result.ok) setError(result.error);
      else router.refresh();
    });
  };

  return (
    <footer className={WRAPPER[variant]}>
      <div className="mx-auto max-w-md lg:max-w-none">
        <div className="flex items-center justify-between gap-2">
          <p className="text-[11px] font-semibold lg:text-sm">
            Sua proposta · {formatPrice(proposal.valor)}
          </p>
          <ProposalStatusBadge status={proposal.status} />
        </div>

        {error && (
          <p
            role="alert"
            className="mt-2 rounded-lg bg-red-50 px-3 py-2 text-[10px] text-red-600"
          >
            {error}
          </p>
        )}

        {proposal.status === "ACEITA" && (
          <div className="mt-2">
            <p className="mb-1.5 text-[10px] text-emerald-700 lg:text-sm">
              Proposta aceita! Combine os detalhes direto com o anunciante:
            </p>
            <ProposalContact person={proposal.destinatario} />
          </div>
        )}

        {proposal.status === "PENDENTE" && (
          <div className="mt-2 flex items-center justify-between gap-2">
            <p className="text-[10px] text-slate-500 lg:text-xs">
              Você será notificado quando o anunciante responder.
            </p>
            <button
              type="button"
              onClick={cancel}
              disabled={pending}
              className="h-8 shrink-0 rounded-md border border-red-200 px-3 text-[10px] font-medium text-red-600 disabled:opacity-50 lg:h-9 lg:text-sm"
            >
              {pending ? "Cancelando..." : "Cancelar"}
            </button>
          </div>
        )}

        {(proposal.status === "RECUSADA" ||
          proposal.status === "NAO_SELECIONADA") && (
          <p className="mt-2 text-[10px] text-slate-500 lg:text-sm">
            O anunciante seguiu com outra opção. Que tal ver outros anúncios?
          </p>
        )}
      </div>
    </footer>
  );
}
