import { Suspense } from "react";
import type { Metadata } from "next";
import { ListingContent } from "@/components/ListingContent";
import { getAnuncios } from "@/lib/ads";
import { getCategories } from "@/lib/categories-api";

export const metadata: Metadata = { title: "Pedidos de serviço" };

async function ServicosLoader() {
  const [anuncios, categories] = await Promise.all([getAnuncios(), getCategories()]);
  return <ListingContent kind="servicos" anuncios={anuncios} categories={categories} />;
}

export default function ServicosPage() {
  return (
    <Suspense fallback={<main className="min-h-dvh bg-white" />}>
      <ServicosLoader />
    </Suspense>
  );
}
