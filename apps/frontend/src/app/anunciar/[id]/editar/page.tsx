import { Suspense } from "react";
import { notFound } from "next/navigation";
import { AdForm } from "@/components/AdForm";
import { getAnuncio } from "@/lib/ads";
import { getCategories } from "@/lib/categories-api";
import { backHrefFrom, FROM_PARAM } from "@/lib/navigation";
import { PHOTO_NOTICE_PARAM } from "@/lib/photos";
import { getCurrentUser } from "@/lib/user";

type SearchParams = Promise<Record<string, string | string[] | undefined>>;

async function EditarAnuncioContent({
  params,
  searchParams,
}: {
  params: Promise<{ id: string }>;
  searchParams: SearchParams;
}) {
  const { id } = await params;
  const query = await searchParams;
  const backHref = backHrefFrom(query[FROM_PARAM], "/historico");
  const notice = query[PHOTO_NOTICE_PARAM]
    ? "Seu anúncio foi publicado, mas as fotos não foram enviadas. Adicione as fotos de novo e salve."
    : undefined;
  const [ad, categories, user] = await Promise.all([
    getAnuncio(id),
    getCategories(),
    getCurrentUser(),
  ]);

  // Só o autor pode editar: para os outros, o anúncio "não existe".
  if (!ad || ad.autorId !== user.id) notFound();

  return <AdForm categories={categories} ad={ad} backHref={backHref} notice={notice} />;
}

export default function EditarAnuncioPage({
  params,
  searchParams,
}: {
  params: Promise<{ id: string }>;
  searchParams: SearchParams;
}) {
  return (
    <Suspense fallback={<main className="min-h-dvh bg-fundo" />}>
      <EditarAnuncioContent params={params} searchParams={searchParams} />
    </Suspense>
  );
}
