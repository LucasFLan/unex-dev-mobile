import { Suspense } from "react";
import { notFound } from "next/navigation";
import { AdForm } from "@/components/AdForm";
import { getAnuncio } from "@/lib/ads";
import { getCategories } from "@/lib/categories-api";
import { getCurrentUser } from "@/lib/user";

async function EditarAnuncioContent({
  params,
}: {
  params: Promise<{ id: string }>;
}) {
  const { id } = await params;
  const [ad, categories, user] = await Promise.all([
    getAnuncio(id),
    getCategories(),
    getCurrentUser(),
  ]);

  // Só o autor pode editar: para os outros, o anúncio "não existe".
  if (!ad || ad.autorId !== user.id) notFound();

  return <AdForm categories={categories} ad={ad} />;
}

export default function EditarAnuncioPage({
  params,
}: {
  params: Promise<{ id: string }>;
}) {
  return (
    <Suspense fallback={<main className="min-h-dvh bg-fundo" />}>
      <EditarAnuncioContent params={params} />
    </Suspense>
  );
}
