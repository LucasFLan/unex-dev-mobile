import type { Metadata } from "next";
import { Suspense } from "react";
import { ShieldCheck } from "lucide-react";
import { ComingSoon } from "@/components/ComingSoon";
import { backHrefFrom, FROM_PARAM } from "@/lib/navigation";

export const metadata: Metadata = { title: "Política de privacidade" };

type SearchParams = Promise<Record<string, string | string[] | undefined>>;

/** Pública: abre do cadastro (?de=/register) e do Menu (?de=/menu); o "Voltar" leva de volta para lá. */
async function Content({ searchParams }: { searchParams: SearchParams }) {
  const backHref = backHrefFrom((await searchParams)[FROM_PARAM], "/menu");

  return (
    <ComingSoon
      title="Política de privacidade"
      description="A política de privacidade está sendo finalizada e vai explicar como seus dados são guardados e usados."
      meanwhile="seus documentos da validação só são vistos pela equipe de administração, e seu endereço só por quem você aceitar."
      icon={ShieldCheck}
      tone="bg-emerald-100 text-emerald-500"
      backHref={backHref}
      backLabel="Voltar"
      withNav={false}
    />
  );
}

export default function Page({ searchParams }: { searchParams: SearchParams }) {
  return (
    <Suspense fallback={<main className="min-h-dvh bg-fundo" />}>
      <Content searchParams={searchParams} />
    </Suspense>
  );
}
