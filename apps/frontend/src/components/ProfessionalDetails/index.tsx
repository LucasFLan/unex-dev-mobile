"use client";

import { useState } from "react";
import { Share2, MapPin, CalendarDays, UserRound } from "lucide-react";
import { AdOwnerActions } from "@/components/AdOwnerActions";
import { BackButton } from "@/components/BackButton";
import { ValidationBanner } from "@/components/ValidationBanner";
import { ProposalSheet } from "@/components/ProposalSheet";
import { AdPhotoGallery } from "@/components/AdPhotoGallery";
import { AdPhotoManager } from "@/components/AdPhotoManager";
import Button from "@/components/Button";
import { RelativeTime } from "@/components/RelativeTime";
import { MyProposalStatus } from "@/components/MyProposalStatus";
import { ReceivedProposals } from "@/components/ReceivedProposals";
import type { AnuncioItem } from "@/lib/ads";
import type { ProposalItem } from "@/lib/proposals";
import { getCategoryMeta } from "@/lib/categories";
import { AdAuthor, AdLocation } from "@/components/AdLocation";
import { formatPrice } from "@/lib/format";

type ProfessionalDetailsProps = {
  ad: AnuncioItem;
  /** O usuário logado é o autor: mostra as ações de gerenciar. */
  isOwner: boolean;
  /** Pedidos de orçamento recebidos (só preenchido para o autor). */
  proposals: ProposalItem[];
  /** Proposta que o usuário logado já enviou para este anúncio. */
  myProposal: ProposalItem | null;
  /** Tela-pai (de onde o usuário abriu o anúncio). */
  backHref: string;
  /** Conta aprovada? Senão, o botão leva para a validação. */
  canPropose: boolean;
};

export function ProfessionalDetails({
  ad,
  isOwner,
  proposals,
  myProposal,
  backHref,
  canPropose,
}: ProfessionalDetailsProps) {
  const { icon: Icon, bg, text } = getCategoryMeta(ad.categoria.slug);
  const location = `${ad.cidade}, ${ad.uf}`;
  const [showRequest, setShowRequest] = useState(false);

  async function handleShare() {
    const url = window.location.href;

    try {
      if (navigator.share) {
        await navigator.share({
          title: ad.titulo,
          text: "Confira este profissional no ChamaAí!",
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


  /** Botão de pedir orçamento (ou o aviso de validação) de quem não é o autor e ainda não pediu. */
  const requestAction = canPropose ? (
    <Button
      text="Solicitar orçamento"
      type="button"
      onClick={() => setShowRequest(true)}
    />
  ) : (
    <ValidationBanner validation="PENDENTE" from={`/profissionais/${ad.id}`} />
  );

  /*
   * Celular/tablet: coluna única, como antes. Desktop (lg+): duas colunas — fotos, descrição, pedidos e mapa à
   * esquerda; à direita um painel que acompanha a rolagem com título, preço, profissional e as ações.
   */
  return (
    <main className="min-h-screen bg-white text-slate-900 md:bg-fundo md:px-6 md:py-10 lg:px-8 lg:py-8">
      <div className="mx-auto min-h-screen w-full max-w-md bg-white pb-24 md:min-h-0 md:overflow-hidden md:rounded-t-2xl md:border md:border-b-0 md:border-slate-200 md:pb-6 lg:grid lg:max-w-6xl lg:grid-cols-[minmax(0,1fr)_23rem] lg:items-start lg:gap-x-8 lg:gap-y-6 lg:overflow-visible lg:rounded-none lg:border-0 lg:bg-transparent lg:pb-0">
        <section className={`relative h-45.5 ${bg} ${ad.fotos.length > 0 ? "md:h-64" : ""} lg:col-start-1 lg:row-start-1 lg:h-[26rem] lg:overflow-hidden lg:rounded-2xl`}>
          <BackButton
            href={backHref}
            iconSize={17}
            className="absolute left-3 top-10 z-10 flex h-7 w-7 items-center justify-center rounded-full bg-white shadow-sm lg:left-4 lg:top-4 lg:h-10 lg:w-10"
          />

          <div className="absolute right-3 top-10 z-10 flex gap-2 lg:right-4 lg:top-4">
            <button
              type="button"
              onClick={handleShare}
              aria-label="Compartilhar"
              className="flex h-7 w-7 items-center justify-center rounded-full bg-white shadow-sm lg:h-10 lg:w-10"
            >
              <Share2 size={16} />
            </button>
          </div>

          {ad.fotos.length > 0 ? (
            <AdPhotoGallery adId={ad.id} photos={ad.fotos} title={ad.titulo} />
          ) : (
            <div className="flex h-full items-center justify-center pt-5 lg:pt-0">
              <Icon
                size={43}
                strokeWidth={1.3}
                className={`${text} lg:size-20`}
                aria-hidden="true"
              />
            </div>
          )}
        </section>

        <aside className="relative -mt-3 rounded-t-2xl bg-white px-4 pt-3 lg:sticky lg:top-8 lg:col-start-2 lg:row-span-2 lg:row-start-1 lg:mt-0 lg:rounded-2xl lg:border lg:border-slate-200 lg:p-6 lg:shadow-sm">
          <div className="flex items-center gap-1 text-[9px] font-medium text-blue-600 lg:gap-1.5 lg:text-xs">
            <UserRound size={11} className="lg:size-3.5" />
            Anúncio de Profissional
          </div>

          <div className="mt-2 flex gap-1.5 lg:mt-3">
            <span className="rounded-md bg-amber-50 px-2 py-1 text-[8px] font-medium text-amber-800 lg:px-2.5 lg:text-xs">
              {ad.categoria.nome}
            </span>

            <span className="rounded-md bg-blue-50 px-2 py-1 text-[8px] font-medium text-blue-700 lg:px-2.5 lg:text-xs">
              {ad.tipo === "OFERTA" ? "Oferece serviço" : "Procura profissional"}
            </span>

            {!ad.ativo && (
              <span className="rounded-md bg-slate-100 px-2 py-1 text-[8px] font-medium text-slate-600 lg:px-2.5 lg:text-xs">
                Pausado
              </span>
            )}
          </div>

          <h1 className="mt-2 text-[14px] font-bold leading-4.5 tracking-tight lg:mt-3 lg:text-2xl lg:leading-8">
            {ad.titulo}
          </h1>

          <div className="mt-2 flex flex-wrap items-center gap-x-2 gap-y-1 text-[9px] text-slate-500 lg:text-sm">
            <span className="flex items-center gap-1">
              <MapPin size={11} className="lg:size-4" />
              {location}
            </span>

            <span className="text-slate-300">|</span>

            <span className="flex items-center gap-1">
              <CalendarDays size={11} className="lg:size-4" />
              Publicado <RelativeTime isoDate={ad.criadoEm} />
            </span>
          </div>

          <div className="lg:mt-5 lg:rounded-xl lg:bg-slate-50 lg:px-4 lg:py-3">
            {ad.preco !== null && (
              <div className="mt-3 flex items-baseline gap-1 lg:mt-0">
                <span className="text-[9px] text-slate-500 lg:text-xs">A partir de</span>
              </div>
            )}

            <div className={`flex items-baseline gap-1 ${ad.preco === null ? "mt-3 lg:mt-0" : ""}`}>
              <strong className="text-[17px] font-bold lg:text-2xl">
                {formatPrice(ad.preco)}
              </strong>
            </div>
          </div>

          <AdAuthor ad={ad} isOwner={isOwner} from={`/profissionais/${ad.id}`} />

          {/* Desktop: as ações ficam no painel; no celular, no rodapé fixo. */}
          <div className="hidden lg:mt-6 lg:block lg:border-t lg:border-slate-100 lg:pt-6">
            {isOwner ? (
              <AdOwnerActions ad={ad} backHref={backHref} variant="panel" />
            ) : myProposal ? (
              <MyProposalStatus proposal={myProposal} variant="panel" />
            ) : (
              requestAction
            )}
          </div>
        </aside>

        <div className="bg-white px-4 lg:col-start-1 lg:row-start-2 lg:rounded-2xl lg:border lg:border-slate-200 lg:p-6 lg:shadow-sm">
          <section className="mt-4 lg:mt-0">
            <h2 className="text-[11px] font-bold lg:text-base">Sobre o serviço</h2>

            <p className="mt-1.5 whitespace-pre-line text-[10px] leading-3.75 text-slate-600 lg:mt-2 lg:text-sm lg:leading-6">
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
        <MyProposalStatus proposal={myProposal} />
      ) : (
        <footer className="fixed inset-x-0 bottom-0 z-20 border-t border-slate-100 bg-white px-4 pb-[max(20px,env(safe-area-inset-bottom))] pt-2 md:sticky md:mx-auto md:max-w-md md:rounded-b-2xl md:border md:border-slate-200 md:shadow-sm lg:hidden">
          <div className="mx-auto max-w-md">{requestAction}</div>
        </footer>
      )}

      {showRequest && !isOwner && (
        <ProposalSheet
          adId={ad.id}
          title="Solicitar orçamento"
          description="Conte o que você precisa. O profissional vai receber seu pedido."
          valueLabel="Quanto pretende pagar"
          messagePlaceholder="Descreva o serviço, o local e quando você precisa..."
          onClose={() => setShowRequest(false)}
        />
      )}
    </main>
  );
}
