import type { Metadata } from "next";
import { Suspense } from "react";
import { FileText } from "lucide-react";
import { ComingSoon } from "@/components/ComingSoon";
import { backHrefFrom, FROM_PARAM } from "@/lib/navigation";

export const metadata: Metadata = { title: "Termos de uso" };

type SearchParams = Promise<Record<string, string | string[] | undefined>>;

/** Pública: abre do cadastro (?de=/register) e do Menu (?de=/menu); o "Voltar" leva de volta para lá. */
async function Content({ searchParams }: { searchParams: SearchParams }) {
  const backHref = backHrefFrom((await searchParams)[FROM_PARAM], "/menu");

  return (
    <ComingSoon
      title="Termos de uso"
      description="Os termos de uso do ChamaAí estão sendo finalizados e vão aparecer aqui."
      icon={FileText}
      tone="bg-slate-100 text-slate-500"
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
