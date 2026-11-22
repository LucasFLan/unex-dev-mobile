import { Suspense } from "react";
import { notFound, redirect } from "next/navigation";
import { getAnuncio } from "@/lib/ads";
import { backHrefFrom, FROM_PARAM, withFrom } from "@/lib/navigation";

type SearchParams = Promise<Record<string, string | string[] | undefined>>;

/** Link genérico de anúncio (usado nas notificações): manda para o detalhe certo pelo tipo. */
async function AnuncioRedirect({
  params,
  searchParams,
}: {
  params: Promise<{ id: string }>;
  searchParams: SearchParams;
}): Promise<never> {
  const { id } = await params;
  const from = backHrefFrom((await searchParams)[FROM_PARAM], "/home");
  const ad = await getAnuncio(id);

  if (!ad) notFound();

  const detail = ad.tipo === "OFERTA" ? `/profissionais/${ad.id}` : `/servicos/${ad.id}`;
  redirect(from === "/home" ? detail : withFrom(detail, from));
}

export default function AnuncioPage({
  params,
  searchParams,
}: {
  params: Promise<{ id: string }>;
  searchParams: SearchParams;
}) {
  return (
    <Suspense fallback={<main className="min-h-dvh bg-white" />}>
      <AnuncioRedirect params={params} searchParams={searchParams} />
    </Suspense>
  );
}
