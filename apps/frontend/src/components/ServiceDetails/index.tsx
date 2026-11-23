"use client";

import { useState } from "react";
import { Share2, Clock3 } from "lucide-react";
import { AdOwnerActions } from "@/components/AdOwnerActions";
import { BackButton } from "@/components/BackButton";
import { ValidationBanner } from "@/components/ValidationBanner";
import { ProposalSheet } from "@/components/ProposalSheet";
import { AdPhotoGallery } from "@/components/AdPhotoGallery";
import { AdPhotoManager } from "@/components/AdPhotoManager";
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

  /** Botão de proposta (ou o aviso de validação) de quem não é o autor e ainda não enviou proposta. */
  const proposeAction = canPropose ? (
    <button
      type="button"
      onClick={() => setShowProposal(true)}
      className="h-9.75 w-full rounded-lg bg-blue-600 text-[10px] font-semibold text-white shadow-sm transition hover:bg-blue-700 active:scale-[0.99] lg:h-12 lg:text-sm"
    >
      Enviar proposta
    </button>
  ) : (
    <ValidationBanner validation="PENDENTE" from={`/servicos/${ad.id}`} />
  );

  /*
   * Celular/tablet: coluna única, como antes. Desktop (lg+): duas colunas — fotos, descrição, propostas e mapa à
   * esquerda; à direita um painel que acompanha a rolagem com título, orçamento, autor e as ações.
   */
  return (
    <main className="min-h-screen bg-white text-slate-900 md:bg-fundo md:px-6 md:py-10 lg:px-8 lg:py-8">
      <div className="mx-auto min-h-screen w-full max-w-md bg-white pb-24 md:min-h-0 md:overflow-hidden md:rounded-t-2xl md:border md:border-b-0 md:border-slate-200 md:pb-6 lg:grid lg:max-w-6xl lg:grid-cols-[minmax(0,1fr)_23rem] lg:items-start lg:gap-x-8 lg:overflow-visible lg:rounded-none lg:border-0 lg:bg-transparent lg:pb-0">
        <header className="flex h-15.5 items-center justify-between px-4 lg:col-span-2 lg:mb-4 lg:h-auto lg:px-0">
          <BackButton
            href={backHref}
            iconSize={17}
            className="flex h-8 w-8 items-center justify-center rounded-lg border border-slate-200 lg:h-10 lg:w-10 lg:bg-white"
          />

          <h1 className="text-[11px] font-semibold lg:text-sm lg:text-slate-500">Anúncio de serviço</h1>

          <button
            type="button"
            onClick={handleShare}
            aria-label="Compartilhar"
            className="flex h-8 w-8 items-center justify-center rounded-lg border border-slate-200 lg:h-10 lg:w-10 lg:bg-white"
          >
            <Share2 size={16} />
          </button>
        </header>

        <aside className="px-5 lg:sticky lg:top-8 lg:col-start-2 lg:row-start-2 lg:rounded-2xl lg:border lg:border-slate-200 lg:bg-white lg:p-6 lg:shadow-sm">
          <div className="flex items-center gap-1.5">
            <span className="rounded-md bg-amber-100 px-2 py-1 text-[8px] font-medium text-amber-800 lg:px-2.5 lg:text-xs">
              {ad.categoria.nome}
            </span>

            <span className="rounded-md bg-blue-50 px-2 py-1 text-[8px] font-medium text-blue-700 lg:px-2.5 lg:text-xs">
              {ad.tipo === "PEDIDO" ? "Procura profissional" : "Oferece serviço"}
            </span>

            {!ad.ativo && (
              <span className="rounded-md bg-slate-100 px-2 py-1 text-[8px] font-medium text-slate-600 lg:px-2.5 lg:text-xs">
                Pausado
              </span>
            )}
          </div>

          <h2 className="mt-2 text-[14px] font-bold leading-4.5 tracking-tight lg:mt-3 lg:text-2xl lg:leading-8">
            {ad.titulo}
          </h2>

          <div className="mt-1.5 flex flex-wrap items-center gap-1 text-[9px] text-slate-500 lg:mt-2 lg:gap-1.5 lg:text-sm">
            <Clock3 size={11} className="lg:size-4" />

            <span>
              Publicado <RelativeTime isoDate={ad.criadoEm} />
            </span>

            <span>·</span>

            <span>{ad.bairro ? `${ad.bairro} · ${location}` : location}</span>
          </div>

          <div className="mt-3 flex items-center justify-between rounded-xl border border-blue-200 bg-blue-50 px-3 py-2.5 lg:mt-5 lg:px-4 lg:py-4">
            <div>
              <p className="text-[8px] font-medium text-blue-600 lg:text-xs">
                Orçamento do cliente
              </p>

              <p className="text-[16px] font-bold lg:text-2xl">{formatPrice(ad.preco)}</p>
            </div>

            <div className="text-right">
              <p className="text-[8px] text-blue-600 lg:text-xs">Prazo</p>
              <p className="text-[10px] font-semibold text-slate-700 lg:text-sm">
                A combinar
              </p>
            </div>
          </div>

          <AdAuthor ad={ad} isOwner={isOwner} from={`/servicos/${ad.id}`} />

          {/* Desktop: as ações ficam no painel; no celular, no rodapé fixo. */}
          <div className="hidden lg:mt-6 lg:block lg:border-t lg:border-slate-100 lg:pt-6">
            {isOwner ? (
              <AdOwnerActions ad={ad} backHref={backHref} variant="panel" />
            ) : myProposal ? (
              <MyProposalStatus proposal={myProposal} variant="panel" onNewRequest={canPropose ? () => setShowProposal(true) : undefined} newRequestLabel="Enviar nova proposta" />
            ) : (
              proposeAction
            )}
          </div>
        </aside>

        <div className="px-5 lg:col-start-1 lg:row-start-2 lg:rounded-2xl lg:border lg:border-slate-200 lg:bg-white lg:p-6 lg:shadow-sm">
          {ad.fotos.length > 0 && (
            <section className="relative mt-4 h-48 overflow-hidden rounded-xl bg-slate-100 md:h-56 lg:mt-0 lg:mb-8 lg:h-[26rem]" aria-label="Fotos">
              <AdPhotoGallery adId={ad.id} photos={ad.fotos} title={ad.titulo} />
            </section>
          )}

          <section className="mt-4 lg:mt-0">
            <h3 className="text-[11px] font-bold lg:text-base">Descrição</h3>

            <p className="mt-1.5 whitespace-pre-line text-[9px] leading-3.5 text-slate-600 lg:mt-2 lg:text-sm lg:leading-6">
              {ad.descricao}
            </p>
          </section>

          {isOwner && <AdPhotoManager ad={ad} />}

          {isOwner && (
            <ReceivedProposals proposals={proposals} adType={ad.tipo} />
          )}

          <div className="lg:mt-8">
            <AdLocation ad={ad} isOwner={isOwner} />
          </div>
        </div>
      </div>

      {isOwner ? (
        <AdOwnerActions ad={ad} backHref={backHref} />
      ) : myProposal ? (
        <MyProposalStatus proposal={myProposal} onNewRequest={canPropose ? () => setShowProposal(true) : undefined} newRequestLabel="Enviar nova proposta" />
      ) : (
        <footer className="fixed inset-x-0 bottom-0 z-20 border-t border-slate-100 bg-white px-5 pb-[max(20px,env(safe-area-inset-bottom))] pt-2 md:sticky md:mx-auto md:max-w-md md:rounded-b-2xl md:border md:border-slate-200 md:shadow-sm lg:hidden">
          <div className="mx-auto max-w-md">{proposeAction}</div>
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
