"use client";

import { useState, useTransition } from "react";
import { useRouter } from "next/navigation";
import { CheckCircle2, MapPin, Star } from "lucide-react";
import { respondProposal } from "@/app/propostas/actions";
import { ReviewSheet } from "@/components/ReviewSheet";
import { Stars } from "@/components/Stars";
import type { ProposalAddress, ProposalItem } from "@/lib/proposals";

function addressLine(address: ProposalAddress) {
  const street = [address.logradouro, address.numero].filter(Boolean).join(", ");
  return [street, address.complemento, address.bairro, `${address.cidade}/${address.uf}`]
    .filter(Boolean)
    .join(" · ");
}

/** Endereço do serviço, liberado depois do aceite, com atalho para abrir no mapa. */
export function ServiceAddress({ address }: { address: ProposalAddress }) {
  const query = [address.logradouro, address.numero, address.bairro, address.cidade, address.uf, address.cep]
    .filter(Boolean)
    .join(", ");
  return (
    <div className="flex items-start gap-1.5 text-[10px] text-slate-600">
      <MapPin size={12} className="mt-0.5 shrink-0 text-slate-400" aria-hidden="true" />
      <p className="min-w-0 flex-1">
        <span className="font-medium text-slate-700">Endereço: </span>
        {addressLine(address)}
        {address.cep && (
          <>
            {" · "}
            <a
              href={`https://www.openstreetmap.org/search?query=${encodeURIComponent(query)}`}
              target="_blank"
              rel="noopener noreferrer"
              className="font-medium text-blue-600"
            >
              Abrir no mapa
            </a>
          </>
        )}
      </p>
    </div>
  );
}

/**
 * Depois do aceite: quem contratou marca o serviço como concluído e avalia; o profissional acompanha.
 * `isClient`: o usuário logado é quem contratou (autor do PEDIDO, ou quem pediu orçamento numa OFERTA).
 */
export function ServiceProgress({
  proposal,
  isClient,
  professionalName,
}: {
  proposal: ProposalItem;
  isClient: boolean;
  professionalName: string;
}) {
  const router = useRouter();
  const [pending, startTransition] = useTransition();
  const [error, setError] = useState("");
  const [reviewing, setReviewing] = useState(false);

  if (proposal.status !== "ACEITA" && proposal.status !== "CONCLUIDA") return null;

  function complete() {
    if (!window.confirm(`Confirmar que ${professionalName} concluiu o serviço?`)) return;
    setError("");
    startTransition(async () => {
      const result = await respondProposal(proposal.id, "concluir");
      if (!result.ok) {
        setError(result.error);
        return;
      }
      router.refresh();
      setReviewing(true);
    });
  }

  const review = proposal.avaliacao;

  return (
    <div className="mt-2 space-y-2">
      {error && (
        <p role="alert" className="rounded-lg bg-red-50 px-3 py-2 text-[10px] text-red-600">
          {error}
        </p>
      )}

      {proposal.status === "ACEITA" &&
        (isClient ? (
          <button
            type="button"
            onClick={complete}
            disabled={pending}
            className="flex h-9 w-full items-center justify-center gap-1.5 rounded-lg bg-emerald-600 text-[11px] font-semibold text-white disabled:opacity-60"
          >
            <CheckCircle2 size={14} aria-hidden="true" />
            {pending ? "Enviando..." : "Marcar serviço como concluído"}
          </button>
        ) : (
          <p className="text-[10px] text-slate-500">
            Quando o cliente confirmar que o serviço foi feito, ele poderá te avaliar.
          </p>
        ))}

      {proposal.status === "CONCLUIDA" && !review && (
        isClient ? (
          <button
            type="button"
            onClick={() => setReviewing(true)}
            className="flex h-9 w-full items-center justify-center gap-1.5 rounded-lg bg-amber-500 text-[11px] font-semibold text-white"
          >
            <Star size={14} aria-hidden="true" className="fill-white" />
            Avaliar {professionalName}
          </button>
        ) : (
          <p className="text-[10px] text-slate-500">Serviço concluído. Aguardando a avaliação do cliente.</p>
        )
      )}

      {review && (
        <div className="rounded-lg bg-amber-50/60 px-2.5 py-2">
          <div className="flex items-center gap-1.5">
            <Stars rating={review.nota} size={11} />
            <span className="text-[10px] font-medium text-slate-600">
              {isClient ? "Sua avaliação" : "Avaliação do cliente"}
            </span>
          </div>
          {review.comentario && (
            <p className="mt-1 text-[10px] leading-3.5 text-slate-600">&ldquo;{review.comentario}&rdquo;</p>
          )}
        </div>
      )}

      {reviewing && (
        <ReviewSheet
          proposalId={proposal.id}
          professionalName={professionalName}
          onClose={() => setReviewing(false)}
          onDone={() => {
            setReviewing(false);
            router.refresh();
          }}
        />
      )}
    </div>
  );
}
