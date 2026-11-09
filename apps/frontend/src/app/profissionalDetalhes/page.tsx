"use client";

import { useRouter } from "next/navigation";

import {
  ArrowLeft,
  Share2,
  Zap,
  MapPin,
  CalendarDays,
  UserRound,
  BadgeCheck,
  ChevronRight,
  Star
} from "lucide-react";
import Button from "@/components/Button";

export default function ProfessionalDetails() {
  const router = useRouter();

  function handleShare() {
    if (navigator.share) {
      navigator.share({
        title: "Eletricista residencial",
        text: "Confira este profissional no ChamaAí!",
        url: window.location.href,
      });
    } else {
      navigator.clipboard.writeText(window.location.href);
      alert("Link copiado!");
    }
  }

  function handleRequest() {
    alert("Solicitação de orçamento selecionada!");
  }

  return (
    <main className="min-h-screen bg-white text-slate-900 ">

      <div className="mx-auto min-h-screen w-full max-w-md pb-24">

        <section className="relative h-45.5 bg-[#FFF2C5]">

          <button
            onClick={() => router.back()}
            aria-label="Voltar"
            className="absolute left-3 top-10 z-10 flex h-7 w-7 items-center justify-center rounded-full bg-white shadow-sm"
          >
            <ArrowLeft size={17} />
          </button>

          <div className="absolute right-3 top-10 z-10 flex gap-2">

            <button
              onClick={handleShare}
              aria-label="Compartilhar"
              className="flex h-7 w-7 items-center justify-center rounded-full bg-white shadow-sm"
            >
              <Share2 size={16} />
            </button>

          </div>

          <div className="flex h-full items-center justify-center pt-5">
            <Zap
              size={43}
              strokeWidth={1.3}
              className="text-amber-600"
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
              Eletricista
            </span>

            <span className="rounded-md bg-blue-50 px-2 py-1 text-[8px] font-medium text-blue-700">
              Oferece serviço
            </span>
          </div>

          <h1 className="mt-2 text-[14px] font-bold leading-4.5 tracking-tight">
            Eletricista residencial e instalações
          </h1>

          <div className="mt-2 flex flex-wrap items-center gap-x-2 gap-y-1 text-[9px] text-slate-500">

            <span className="flex items-center gap-1">
              <MapPin size={11} />
              Centro, 2 km
            </span>

            <span className="text-slate-300">|</span>

            <span className="flex items-center gap-1">
              <CalendarDays size={11} />
              Disponível hoje
            </span>

          </div>

          <div className="mt-3 flex items-baseline gap-1">
            <span className="text-[9px] text-slate-500">
              A partir de
            </span>
          </div>

          <div className="flex items-baseline gap-1">
            <strong className="text-[17px] font-bold">
              R$ 80
            </strong>

            <span className="text-[9px] text-slate-500">
              / visita
            </span>
          </div>

          <div className="mt-2 flex items-center gap-1.5 text-[9px] text-slate-500">

            <span className="flex h-4 w-4 items-center justify-center rounded-full bg-blue-100 text-[7px] font-semibold text-blue-600">
              CM
            </span>

            <span>Anunciado por</span>

            <strong className="text-slate-800">
              Carlos Mendes
            </strong>

            <BadgeCheck size={11} className="text-blue-600" />

          </div>

          <section className="mt-4">

            <h2 className="text-[11px] font-bold">
              Sobre o serviço
            </h2>

            <p className="mt-1.5 text-[10px] leading-3.75 text-slate-600">
              Instalação, troca e reparo de tomadas,
              interruptores, chuveiros e luminárias em
              casas e apartamentos. O atendimento inclui
              avaliação no local, orçamento sem compromisso
              e garantia de 90 dias. O material pode ser
              incluso ou fornecido por você.
            </p>

          </section>

          <section className="mt-4">

            <h2 className="text-[11px] font-bold">
              Região de atendimento
            </h2>

            <div className="relative mt-2 h-22.25 overflow-hidden rounded-xl bg-[#EDF2F7]">

              <div className="absolute inset-0">

                <div className="absolute -left-4 top-8.75 h-1.25 w-[120%] rotate-[8deg] bg-white" />

                <div className="absolute -left-4 top-16.75 h-1.25 w-[120%] rotate-[8deg] bg-white" />

                <div className="absolute left-[38%] -top-5 h-37.5 w-1.25 rotate-12 bg-white" />

                <div className="absolute left-[70%] -top-5 h-37.5 w-1.25 rotate-[7deg] bg-white" />

                <div className="absolute left-[20%] -top-5 h-37.5 w-1.25 rotate-12 bg-white" />

              </div>

              <div className="absolute left-1/2 top-1/2 flex h-18 w-18 -translate-x-1/2 -translate-y-1/2 items-center justify-center rounded-full border border-blue-300 bg-blue-200/50">

                <MapPin
                  size={16}
                  fill="#2563EB"
                  className="text-white"
                />

              </div>

            </div>

            <div className="mt-2 flex items-center gap-1 text-[9px] text-slate-500">
              <MapPin size={11} />
              Centro e bairros próximos · até 10 km
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
                CM
              </div>

              <div className="min-w-0 flex-1">

                <p className="text-[9px] font-semibold">
                  Carlos M
                </p>

                <div className="flex items-center gap-1">
                  <Star
                    size={10}
                    className="fill-amber-400 text-amber-400"
                  />

                  <span className="text-[9px] font-medium">
                    4.9
                  </span>
                </div>

                <p className="text-[8px] leading-3 text-slate-500">
                  Membro desde 2021 · Identidade verificada
                </p>

              </div>

              <ChevronRight
                size={14}
                className="shrink-0 text-slate-400"
              />

            </button>

          </section>

        </section>

      </div>

      <footer className="fixed inset-x-0 bottom-0 z-20 border-t border-slate-100 bg-white px-4 pb-[max(20px,env(safe-area-inset-bottom))] pt-2">

        <div className="mx-auto max-w-md">

          <Button text="Solicitar orçamento" type="button" onClick={handleRequest}/>

        </div>

      </footer>

    </main>
  );
}
