import { Suspense } from "react";
import { notFound, redirect } from "next/navigation";
import { getAnuncio } from "@/lib/ads";

/** Link genérico de anúncio (usado nas notificações): manda para o detalhe certo pelo tipo. */
async function AnuncioRedirect({
  params,
}: {
  params: Promise<{ id: string }>;
}): Promise<never> {
  const { id } = await params;
  const ad = await getAnuncio(id);

  if (!ad) notFound();

  redirect(ad.tipo === "OFERTA" ? `/profissionais/${ad.id}` : `/servicos/${ad.id}`);
}

export default function AnuncioPage({
  params,
}: {
  params: Promise<{ id: string }>;
}) {
  return (
    <Suspense fallback={<main className="min-h-dvh bg-white" />}>
      <AnuncioRedirect params={params} />
    </Suspense>
  );
}
