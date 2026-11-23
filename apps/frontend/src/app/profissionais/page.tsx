import { Suspense } from "react";
import type { Metadata } from "next";
import { ListingContent } from "@/components/ListingContent";
import { getAnuncios } from "@/lib/ads";
import { getCategories } from "@/lib/categories-api";

export const metadata: Metadata = { title: "Profissionais" };

async function ProfissionaisLoader() {
  const [anuncios, categories] = await Promise.all([getAnuncios(), getCategories()]);
  return <ListingContent kind="profissionais" anuncios={anuncios} categories={categories} />;
}

export default function ProfissionaisPage() {
  return (
    <Suspense fallback={<main className="min-h-dvh bg-white" />}>
      <ProfissionaisLoader />
    </Suspense>
  );
}
