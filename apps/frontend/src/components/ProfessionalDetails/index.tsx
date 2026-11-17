"use client";

import {
  Share2,
  MapPin,
  CalendarDays,
  UserRound,
  BadgeCheck,
  ChevronRight,
  Star,
} from "lucide-react";
import { BackButton } from "@/components/BackButton";
import Button from "@/components/Button";
import { CATEGORY_META } from "@/lib/categories";
import { initials, type ProfessionalDetail } from "@/lib/marketplace";
import {formatPrice, formatDistance, formatRating} from "@/lib/format";

export function ProfessionalDetails({
  professional,
}: {
  professional: ProfessionalDetail;
}) {
  const { icon: Icon, bg, text } = CATEGORY_META[professional.category];

  async function handleShare() {
    const url = window.location.href;

    try {
      if (navigator.share) {
        await navigator.share({
          title: professional.service,
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

  function handleRequest() {
    // TODO(API): abrir o fluxo de orçamento (ex.: /profissionais/[id]/contratar).
    alert("Solicitação de orçamento selecionada!");
  }

  return (
    <main className="min-h-screen bg-white text-slate-900 ">
      <div className="mx-auto min-h-screen w-full max-w-md pb-24">
        <section className={`relative h-45.5 ${bg}`}>
          <BackButton
            iconSize={17}
            className="absolute left-3 top-10 z-10 flex h-7 w-7 items-center justify-center rounded-full bg-white shadow-sm"
          />

          <div className="absolute right-3 top-10 z-10 flex gap-2">
            <button
              type="button"
              onClick={handleShare}
              aria-label="Compartilhar"
              className="flex h-7 w-7 items-center justify-center rounded-full bg-white shadow-sm"
            >
              <Share2 size={16} />
            </button>
          </div>

          <div className="flex h-full items-center justify-center pt-5">
            <Icon
              size={43}
              strokeWidth={1.3}
              className={text}
              aria-hidden="true"
            />
          </div>
        </section>

        <section className="relative -mt-3 rounded-t-2xl bg-white px-4 pt-3">
          <div className="flex items-center gap-1 text-[9px] font-medium text-blue-600">
            <UserRound size={11} />
            Anúncio de Profissional
          </div>

          <div className="mt-2 flex gap-1.5">
            <span className="rounded-md bg-amber-50 px-2 py-1 text-[8px] font-medium text-amber-800">
              {professional.category}
            </span>

            <span className="rounded-md bg-blue-50 px-2 py-1 text-[8px] font-medium text-blue-700">
              Oferece serviço
            </span>
          </div>

          <h1 className="mt-2 text-[14px] font-bold leading-4.5 tracking-tight">
            {professional.service}
          </h1>

          <div className="mt-2 flex flex-wrap items-center gap-x-2 gap-y-1 text-[9px] text-slate-500">
            <span className="flex items-center gap-1">
              <MapPin size={11} />
              {professional.neighborhood},{" "}
              {formatDistance(professional.distanceKm)}
            </span>

            <span className="text-slate-300">|</span>

            <span className="flex items-center gap-1">
              <CalendarDays size={11} />
              {professional.availability}
            </span>
          </div>

          <div className="mt-3 flex items-baseline gap-1">
            <span className="text-[9px] text-slate-500">A partir de</span>
          </div>

          <div className="flex items-baseline gap-1">
            <strong className="text-[17px] font-bold">
              {formatPrice(professional.priceFrom)}
            </strong>

            {professional.priceUnit && (
              <span className="text-[9px] text-slate-500">
                / {professional.priceUnit}
              </span>
            )}
          </div>

          <div className="mt-2 flex items-center gap-1.5 text-[9px] text-slate-500">
            <span className="flex h-4 w-4 items-center justify-center rounded-full bg-blue-100 text-[7px] font-semibold text-blue-600">
              {initials(professional.name)}
            </span>

            <span>Anunciado por</span>

            <strong className="text-slate-800">{professional.name}</strong>

            {professional.verified && (
              <BadgeCheck size={11} className="text-blue-600" />
            )}
          </div>

          <section className="mt-4">
            <h2 className="text-[11px] font-bold">Sobre o serviço</h2>

            <p className="mt-1.5 text-[10px] leading-3.75 text-slate-600">
              {professional.description}
            </p>
          </section>

          <section className="mt-4">
            <h2 className="text-[11px] font-bold">Região de atendimento</h2>

            <div
              className="relative mt-2 h-22.25 overflow-hidden rounded-xl bg-[#EDF2F7]"
              aria-hidden="true"
            >
              <div className="absolute inset-0">
                <div className="absolute -left-4 top-8.75 h-1.25 w-[120%] rotate-[8deg] bg-white" />
                <div className="absolute -left-4 top-16.75 h-1.25 w-[120%] rotate-[8deg] bg-white" />
                <div className="absolute left-[38%] -top-5 h-37.5 w-1.25 rotate-12 bg-white" />
                <div className="absolute left-[70%] -top-5 h-37.5 w-1.25 rotate-[7deg] bg-white" />
                <div className="absolute left-[20%] -top-5 h-37.5 w-1.25 rotate-12 bg-white" />
              </div>

              <div className="absolute left-1/2 top-1/2 flex h-18 w-18 -translate-x-1/2 -translate-y-1/2 items-center justify-center rounded-full border border-blue-300 bg-blue-200/50">
                <MapPin size={16} fill="#2563EB" className="text-white" />
              </div>
            </div>

            <div className="mt-2 flex items-center gap-1 text-[9px] text-slate-500">
              <MapPin size={11} />
              {professional.neighborhood} e bairros próximos · até{" "}
              {professional.serviceRadiusKm} km
            </div>

            <div className="mt-3 h-1 w-full rounded-full bg-slate-100" />
          </section>

          <section className="mt-3">
            <div className="flex items-center gap-1 text-[8px] font-medium uppercase tracking-wide text-blue-600">
              <UserRound size={11} />
              Quem presta o serviço
            </div>

            <h2 className="mt-1 text-[11px] font-bold">
              Sobre o profissional
            </h2>

            <button
              type="button"
              className="cursor-pointer mt-2 flex w-full items-center gap-2 rounded-xl border border-slate-200 p-2.5 text-left transition hover:bg-slate-50"
            >
              <div className="flex h-8 w-8 shrink-0 items-center justify-center rounded-full bg-blue-100 text-[10px] font-semibold text-blue-600">
                {initials(professional.name)}
              </div>

              <div className="min-w-0 flex-1">
                <p className="text-[9px] font-semibold">{professional.name}</p>

                <div className="flex items-center gap-1">
                  <Star size={10} className="fill-amber-400 text-amber-400" />

                  <span className="text-[9px] font-medium">
                    {formatRating(professional.rating)}
                  </span>
                </div>

                <p className="text-[8px] leading-3 text-slate-500">
                  Membro desde {professional.memberSince.slice(0, 4)}
                  {professional.verified && " · Identidade verificada"}
                </p>
              </div>

              <ChevronRight size={14} className="shrink-0 text-slate-400" />
            </button>
          </section>
        </section>
      </div>

      <footer className="fixed inset-x-0 bottom-0 z-20 border-t border-slate-100 bg-white px-4 pb-[max(20px,env(safe-area-inset-bottom))] pt-2">
        <div className="mx-auto max-w-md">
          <Button
            text="Solicitar orçamento"
            type="button"
            onClick={handleRequest}
          />
        </div>
      </footer>
    </main>
  );
}