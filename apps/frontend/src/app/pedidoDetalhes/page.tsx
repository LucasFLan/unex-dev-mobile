"use client";

import { useRouter } from "next/navigation";
import { useState } from "react";

import {
  ArrowLeft,
  Share2,
  Clock3,
  MapPin,
  Zap,
  BadgeCheck,
  Star,
  ChevronRight,
  LockKeyhole
} from "lucide-react";
import Button from "@/components/Button";
import Image from "next/image";
import Chuveiro from "@/../public/chuveiro.jpg";

export default function ServiceDetails() {
  const router = useRouter();

  const [showProposal, setShowProposal] = useState(false);

  function handleShare() {
    if (navigator.share) {
      navigator.share({
        title: "Pedido de serviço - ChamaAí",
        text: "Confira este pedido de serviço!",
        url: window.location.href,
      });
    } else {
      navigator.clipboard.writeText(window.location.href);
      alert("Link copiado!");
    }
  }

  return (
    <main className="min-h-screen bg-white text-slate-900">

      <div className="mx-auto min-h-screen w-full max-w-md pb-24">

        <header className="flex h-15.5 items-center justify-between px-4">

          <button
            type="button"
            onClick={() => router.back()}
            aria-label="Voltar"
            className="flex h-8 w-8 items-center justify-center rounded-lg border border-slate-200"
          >
            <ArrowLeft size={17} />
          </button>

          <h1 className="text-[11px] font-semibold">
            Anúncio de serviço
          </h1>

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
              Eletricista
            </span>

            <span className="rounded-md bg-red-100 px-2 py-1 text-[8px] font-medium text-red-600">
              Urgente
            </span>

          </div>

          <h2 className="mt-2 text-[14px] font-bold leading-4.5 tracking-tight">
            Instalar 3 tomadas e um chuveiro
          </h2>

          <div className="mt-1.5 flex flex-wrap items-center gap-1 text-[9px] text-slate-500">

            <Clock3 size={11} />

            <span>Publicado há 15 min</span>

            <span>·</span>

            <span>Boa Vista, 3 km</span>

          </div>

          <div className="mt-3 flex items-center justify-between rounded-xl border border-blue-200 bg-blue-50 px-3 py-2.5">

            <div>
              <p className="text-[8px] font-medium text-blue-600">
                Orçamento do cliente
              </p>

              <p className="text-[16px] font-bold">
                R$ 150
              </p>
            </div>

            <div className="text-right">
              <p className="text-[8px] text-blue-600">
                Prazo
              </p>

              <p className="flex items-center gap-1 text-[10px] font-semibold text-red-500">
                <Zap size={12} />
                Urgente
              </p>
            </div>

          </div>

          <section className="mt-4">

            <h3 className="text-[11px] font-bold">
              Descrição
            </h3>

            <p className="mt-1.5 text-[9px] leading-3.5 text-slate-600">
              Preciso de um eletricista para instalar 3
              tomadas novas na cozinha (bancada da pia) e
              trocar o chuveiro do banheiro, que queimou.
              Já comprei o chuveiro novo (220V). A fiação
              está aparente em um dos pontos. Disponível
              para receber o profissional hoje à tarde ou
              amanhã pela manhã.
            </p>

          </section>

          <section className="mt-4">

            <h3 className="text-[11px] font-bold">
              Fotos do cliente
            </h3>

            <div className="mt-2 grid grid-cols-3 gap-1.5">

              <Image src={Chuveiro} alt="Chuveiro Imagem" className="flex aspect-square items-center justify-center rounded-xl"/>
              <Image src={Chuveiro} alt="Chuveiro Imagem" className="flex aspect-square items-center justify-center rounded-xl"/>
              <Image src={Chuveiro} alt="Chuveiro Imagem" className="flex aspect-square items-center justify-center rounded-xl"/>

            </div>

          </section>

          <section className="mt-3">

            <button
              type="button"
              className="flex w-full items-center gap-2 rounded-xl border border-slate-200 px-2.5 py-2"
            >

              <div className="flex h-7 w-7 shrink-0 items-center justify-center rounded-full bg-blue-100 text-[9px] font-semibold text-blue-600">
                ML
              </div>

              <div className="min-w-0 flex-1 text-left">

                <div className="flex items-center gap-1">
                  <span className="text-[9px] font-semibold">
                    Mariana Lopes
                  </span>

                  <BadgeCheck
                    size={11}
                    className="text-blue-600"
                  />
                </div>

                <div className="mt-0.5 flex items-center gap-1">
                  <Star
                    size={10}
                    className="fill-amber-400 text-amber-400"
                  />

                  <span className="text-[9px] font-semibold">
                    4.8
                  </span>
                </div>

                <p className="text-[8px] text-slate-500">
                  Cliente desde 2024 · 5 pedidos publicados
                </p>

              </div>

              <ChevronRight
                size={15}
                className="shrink-0 text-slate-400"
              />

            </button>

          </section>

          <section className="mt-4">

            <h3 className="text-[11px] font-bold">
              Localização aproximada
            </h3>

            <div className="relative mt-2 h-[85px] overflow-hidden rounded-xl bg-[#EDF2F7]">

              <div className="absolute inset-0">

                <div className="absolute -left-3 top-[34px] h-[5px] w-[120%] rotate-[8deg] bg-white" />

                <div className="absolute -left-3 top-[65px] h-[5px] w-[120%] rotate-[8deg] bg-white" />

                <div className="absolute left-[38%] -top-5 h-[140px] w-[5px] rotate-[-12deg] bg-white" />

                <div className="absolute left-[70%] -top-5 h-[140px] w-[5px] rotate-[7deg] bg-white" />

                <div className="absolute left-[20%] -top-5 h-[140px] w-[5px] rotate-[-12deg] bg-white" />

              </div>

              <div className="absolute left-1/2 top-1/2 flex h-[68px] w-[68px] -translate-x-1/2 -translate-y-1/2 items-center justify-center rounded-full border border-blue-300 bg-blue-200/50">

                <MapPin
                  size={15}
                  fill="#2563EB"
                  className="text-white"
                />

              </div>

            </div>

            <div className="mt-2 flex items-start gap-1 text-[8px] leading-3 text-slate-500">

              <LockKeyhole
                size={11}
                className="mt-0.5 shrink-0"
              />

              <p>
                O endereço exato é liberado quando a proposta é aceita.
              </p>

            </div>

          </section>

        </section>

      </div>

      <footer className="fixed inset-x-0 bottom-0 z-20 border-t border-slate-100 bg-white px-5 pb-[max(20px,env(safe-area-inset-bottom))] pt-2">

        <div className="mx-auto max-w-md">

          <button
            type="button"
            onClick={() => setShowProposal(true)}
            className="h-9.75 w-full rounded-lg bg-blue-600 text-[10px] font-semibold text-white shadow-sm transition hover:bg-blue-700 active:scale-[0.99]"
          >
            Enviar proposta
          </button>

        </div>

      </footer>

      {showProposal && (
        <div className="fixed inset-0 z-30 flex items-end justify-center bg-black/40">

          <div className="w-full max-w-md rounded-t-2xl bg-white p-5">

            <div className="mb-4 flex items-center justify-between">

              <h3 className="text-sm font-bold">
                Enviar proposta
              </h3>

              <button
                type="button"
                onClick={() => setShowProposal(false)}
                className="text-slate-500"
                aria-label="Fechar"
              >
                ✕
              </button>

            </div>

            <p className="text-xs text-slate-500">
              Informe o valor que deseja cobrar pelo serviço.
            </p>

            <label className="mt-4 block text-xs font-medium">
              Valor da proposta
            </label>

            <input
              type="number"
              placeholder="R$ 0,00"
              min="0"
              className="mt-2 h-11 w-full rounded-lg border border-slate-200 px-3 text-sm outline-none focus:border-blue-500"
            />

            <label className="mt-4 block text-xs font-medium">
              Mensagem para o cliente
            </label>

            <textarea
              placeholder="Descreva como pretende realizar o serviço..."
              rows={3}
              className="mt-2 w-full resize-none rounded-lg border border-slate-200 p-3 text-xs outline-none focus:border-blue-500"
            />

            <Button text="Confirmar proposta" type="button" onClick={() => {
                setShowProposal(false);
                alert("Proposta enviada!");
              }}/>

          </div>

        </div>
      )}

    </main>
  );
}
