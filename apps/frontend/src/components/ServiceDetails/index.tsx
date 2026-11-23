"use client";

import { useState } from "react";
import { Share2, Clock3 } from "lucide-react";
import { AdOwnerActions } from "@/components/AdOwnerActions";
import { BackButton } from "@/components/BackButton";
import { ValidationBanner } from "@/components/ValidationBanner";
import { ProposalSheet } from "@/components/ProposalSheet";
import { RelativeTime } from "@/components/RelativeTime";
import { MyProposalStatus } from "@/components/MyProposalStatus";
import { ReceivedProposals } from "@/components/ReceivedProposals";
import { AdAuthor, AdLocation } from "@/components/AdLocation";
import type { AnuncioItem } from "@/lib/ads";
import type { ProposalItem } from "@/lib/proposals";
import { formatPrice } from "@/lib/format";

type ServiceDetailsProps = {
  ad: AnuncioItem;
  /** O usuário logado é o autor: mostra as ações de gerenciar. */
  isOwner: boolean;
  /** Propostas recebidas (só preenchido para o autor). */
  proposals: ProposalItem[];
  /** Proposta que o usuário logado já enviou para este anúncio. */
  myProposal: ProposalItem | null;
  /** Tela-pai (de onde o usuário abriu o anúncio). */
  backHref: string;
  /** Conta aprovada? Senão, o botão leva para a validação. */
  canPropose: boolean;
};

export function ServiceDetails({
  ad,
  isOwner,
  proposals,
  myProposal,
  backHref,
  canPropose,
}: ServiceDetailsProps) {
  const [showProposal, setShowProposal] = useState(false);
  const location = `${ad.cidade}, ${ad.uf}`;

  async function handleShare() {
    const url = window.location.href;

    try {
      if (navigator.share) {
        await navigator.share({
          title: "Pedido de serviço - ChamaAí",
          text: ad.titulo,
          url,
        });
      } else {
        await navigator.clipboard.writeText(url);
        alert("Link copiado!");
      }
    } catch {
      // O usuário cancelou o compartilhamento: não é um erro.
    }
  }

  return (
    <main className="min-h-screen bg-white text-slate-900 md:bg-fundo md:px-6 md:py-10">
      <div className="mx-auto min-h-screen w-full max-w-md bg-white pb-24 md:min-h-0 md:overflow-hidden md:rounded-t-2xl md:border md:border-b-0 md:border-slate-200 md:pb-6">
        <header className="flex h-15.5 items-center justify-between px-4">
          <BackButton
            href={backHref}
            iconSize={17}
            className="flex h-8 w-8 items-center justify-center rounded-lg border border-slate-200"
          />

          <h1 className="text-[11px] font-semibold">Anúncio de serviço</h1>

          <button
            type="button"
            onClick={handleShare}
            aria-label="Compartilhar"
            className="flex h-8 w-8 items-center justify-center rounded-lg border border-slate-200"
          >
            <Share2 size={16} />
          </button>
        </header>

        <section className="px-5">
          <div className="flex items-center gap-1.5">
            <span className="rounded-md bg-amber-100 px-2 py-1 text-[8px] font-medium text-amber-800">
              {ad.categoria.nome}
            </span>

            <span className="rounded-md bg-blue-50 px-2 py-1 text-[8px] font-medium text-blue-700">
              {ad.tipo === "PEDIDO" ? "Procura profissional" : "Oferece serviço"}
            </span>

            {!ad.ativo && (
              <span className="rounded-md bg-slate-100 px-2 py-1 text-[8px] font-medium text-slate-600">
                Pausado
              </span>
            )}
          </div>

          <h2 className="mt-2 text-[14px] font-bold leading-4.5 tracking-tight">
            {ad.titulo}
          </h2>

          <div className="mt-1.5 flex flex-wrap items-center gap-1 text-[9px] text-slate-500">
            <Clock3 size={11} />

            <span>
              Publicado <RelativeTime isoDate={ad.criadoEm} />
            </span>

            <span>·</span>

            <span>{ad.bairro ? `${ad.bairro} · ${location}` : location}</span>
          </div>

          <div className="mt-3 flex items-center justify-between rounded-xl border border-blue-200 bg-blue-50 px-3 py-2.5">
            <div>
              <p className="text-[8px] font-medium text-blue-600">
                Orçamento do cliente
              </p>

              <p className="text-[16px] font-bold">{formatPrice(ad.preco)}</p>
            </div>

            <div className="text-right">
              <p className="text-[8px] text-blue-600">Prazo</p>
              <p className="text-[10px] font-semibold text-slate-700">
                A combinar
              </p>
            </div>
          </div>

          <AdAuthor ad={ad} isOwner={isOwner} from={`/servicos/${ad.id}`} />

          <section className="mt-4">
            <h3 className="text-[11px] font-bold">Descrição</h3>

            <p className="mt-1.5 text-[9px] leading-3.5 text-slate-600">
              {ad.descricao}
            </p>
          </section>

          {isOwner && (
            <ReceivedProposals proposals={proposals} adType={ad.tipo} />
          )}

          <AdLocation ad={ad} isOwner={isOwner} />
        </section>
      </div>

      {isOwner ? (
        <AdOwnerActions ad={ad} backHref={backHref} />
      ) : myProposal ? (
        <MyProposalStatus proposal={myProposal} />
      ) : (
      <footer className="fixed inset-x-0 bottom-0 z-20 border-t border-slate-100 bg-white px-5 pb-[max(20px,env(safe-area-inset-bottom))] pt-2 md:sticky md:mx-auto md:max-w-md md:rounded-b-2xl md:border md:border-slate-200 md:shadow-sm">
        <div className="mx-auto max-w-md">
          {canPropose ? (
            <button
              type="button"
              onClick={() => setShowProposal(true)}
              className="h-9.75 w-full rounded-lg bg-blue-600 text-[10px] font-semibold text-white shadow-sm transition hover:bg-blue-700 active:scale-[0.99]"
            >
              Enviar proposta
            </button>
          ) : (
            <ValidationBanner validation="PENDENTE" from={`/servicos/${ad.id}`} />
          )}
        </div>
      </footer>
      )}

      {showProposal && !isOwner && (
        <ProposalSheet
          adId={ad.id}
          title="Enviar proposta"
          description="Diga quanto cobra e como pretende fazer o serviço."
          valueLabel="Valor da proposta"
          messagePlaceholder="Descreva como pretende realizar o serviço, prazo e se leva o material..."
          onClose={() => setShowProposal(false)}
        />
      )}
    </main>
  );
}