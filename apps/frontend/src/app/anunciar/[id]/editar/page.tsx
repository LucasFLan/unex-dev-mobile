import { Suspense } from "react";
import { notFound } from "next/navigation";
import { AdForm } from "@/components/AdForm";
import { getAnuncio } from "@/lib/ads";
import { getCategories } from "@/lib/categories-api";
import { backHrefFrom, FROM_PARAM } from "@/lib/navigation";
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
  const backHref = backHrefFrom((await searchParams)[FROM_PARAM], "/historico");
  const [ad, categories, user] = await Promise.all([
    getAnuncio(id),
    getCategories(),
    getCurrentUser(),
  ]);

  // Só o autor pode editar: para os outros, o anúncio "não existe".
  if (!ad || ad.autorId !== user.id) notFound();

  return <AdForm categories={categories} ad={ad} backHref={backHref} />;
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
