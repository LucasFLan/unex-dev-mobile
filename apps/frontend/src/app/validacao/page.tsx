import { Suspense } from "react";
import Link from "next/link";
import { AlertTriangle, BadgeCheck, Clock3, ShieldCheck } from "lucide-react";
import { BackButton } from "@/components/BackButton";
import { RelativeTime } from "@/components/RelativeTime";
import { ValidationForm } from "@/components/ValidationForm";
import { backHrefFrom, FROM_PARAM } from "@/lib/navigation";
import { getMinhaValidacao } from "@/lib/validation";

type SearchParams = Promise<Record<string, string | string[] | undefined>>;

async function ValidacaoContent({ searchParams }: { searchParams: SearchParams }) {
  const backHref = backHrefFrom((await searchParams)[FROM_PARAM], "/menu");
  const validation = await getMinhaValidacao();

  return (
    <main className="min-h-dvh bg-fundo pb-10 text-slate-800">
      <div className="mx-auto w-full max-w-lg lg:pt-6">
        <header className="sticky top-0 z-20 flex items-center gap-1 bg-fundo px-2 pb-2 pt-3">
          <BackButton href={backHref} />
          <h1 className="text-[17px] font-bold tracking-tight">Validar conta</h1>
        </header>

        <div className="space-y-4 px-3.5">
          {validation.situacao === "APROVADO" && (
            <section className="flex flex-col items-center rounded-xl border border-emerald-200 bg-emerald-50 px-4 py-6 text-center">
              <BadgeCheck size={36} className="text-emerald-600" aria-hidden="true" />
              <h2 className="mt-2 text-sm font-bold text-emerald-800">Conta validada</h2>
              <p className="mt-1 text-xs text-emerald-700">
                Você já pode publicar anúncios e enviar propostas.
              </p>
              <Link
                href="/anunciar"
                className="mt-4 h-10 rounded-lg bg-blue-600 px-5 text-xs font-semibold leading-10 text-white"
              >
                Criar anúncio
              </Link>
            </section>
          )}

          {validation.situacao === "EM_ANALISE" && (
            <section className="flex flex-col items-center rounded-xl border border-amber-200 bg-amber-50 px-4 py-6 text-center">
              <Clock3 size={34} className="text-amber-600" aria-hidden="true" />
              <h2 className="mt-2 text-sm font-bold text-amber-800">Documentos em análise</h2>
              <p className="mt-1 text-xs leading-5 text-amber-700">
                Recebemos sua foto do documento e a selfie
                {validation.enviadoEm && (
                  <>
                    {" "}
                    (<RelativeTime isoDate={validation.enviadoEm} />)
                  </>
                )}
                . Você vai receber uma notificação quando a análise terminar.
              </p>
            </section>
          )}

          {validation.podeEnviar && (
            <>
              {validation.situacao === "REPROVADO" ? (
                <section className="rounded-xl border border-red-200 bg-red-50 p-3">
                  <p className="flex items-center gap-1.5 text-xs font-semibold text-red-700">
                    <AlertTriangle size={14} aria-hidden="true" />
                    Seus documentos não foram aprovados
                  </p>
                  {validation.motivoReprovacao && (
                    <p className="mt-1 text-[11px] leading-4 text-red-700">
                      Motivo: {validation.motivoReprovacao}
                    </p>
                  )}
                  <p className="mt-1 text-[11px] text-red-600">
                    Corrija o que foi apontado e envie de novo.
                  </p>
                </section>
              ) : (
                <section className="rounded-xl border border-blue-200 bg-blue-50 p-3">
                  <p className="flex items-center gap-1.5 text-xs font-semibold text-blue-800">
                    <ShieldCheck size={14} aria-hidden="true" />
                    Por que validar?
                  </p>
                  <p className="mt-1 text-[11px] leading-4 text-blue-700">
                    Para manter o ChamaAí seguro, só contas validadas publicam anúncios e enviam
                    propostas. Envie uma foto do seu documento e uma selfie segurando ele: a equipe
                    confere com os dados do seu cadastro.
                  </p>
                </section>
              )}

              <ValidationForm resubmission={validation.situacao === "REPROVADO"} />
            </>
          )}
        </div>
      </div>
    </main>
  );
}

export default function ValidacaoPage({ searchParams }: { searchParams: SearchParams }) {
  return (
    <Suspense fallback={<main className="min-h-dvh bg-fundo" />}>
      <ValidacaoContent searchParams={searchParams} />
    </Suspense>
  );
}
