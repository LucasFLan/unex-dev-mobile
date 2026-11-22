import { Suspense } from "react";
import { HistoricoContent } from "@/components/HistoricoContent";
import { getMeusAnuncios } from "@/lib/ads";
import { getPropostasEnviadas } from "@/lib/proposals";

async function HistoricoLoader({
  searchParams,
}: {
  searchParams: Promise<{ aba?: string }>;
}) {
  const { aba } = await searchParams;
  const [anuncios, sentProposals] = await Promise.all([
    getMeusAnuncios(),
    getPropostasEnviadas(),
  ]);

  return (
    <HistoricoContent
      anuncios={anuncios}
      sentProposals={sentProposals}
      initialTab={aba === "propostas" ? "propostas" : "anuncios"}
    />
  );
}

export default function HistoricoPage({
  searchParams,
}: {
  searchParams: Promise<{ aba?: string }>;
}) {
  return (
    <Suspense fallback={<main className="min-h-dvh bg-fundo" />}>
      <HistoricoLoader searchParams={searchParams} />
    </Suspense>
  );
}
