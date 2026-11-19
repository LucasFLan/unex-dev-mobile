import { Suspense } from "react";
import { AdForm } from "@/components/AdForm";
import { getCategories } from "@/lib/categories-api";

async function AnunciarContent() {
  const categories = await getCategories();

  return <AdForm categories={categories} />;
}

export default function AnunciarPage() {
  return (
    <Suspense fallback={<main className="min-h-dvh bg-fundo" />}>
      <AnunciarContent />
    </Suspense>
  );
}