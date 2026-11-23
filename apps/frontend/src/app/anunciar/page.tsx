import { Suspense } from "react";
import { AdForm } from "@/components/AdForm";
import { ValidationRequired } from "@/components/ValidationRequired";
import { getAnuncio, type AnuncioItem } from "@/lib/ads";
import { getCategories } from "@/lib/categories-api";
import { backHrefFrom, FROM_PARAM } from "@/lib/navigation";
import { getCurrentUser } from "@/lib/user";

type SearchParams = Promise<Record<string, string | string[] | undefined>>;

/** Parâmetro com o id do anúncio a duplicar: /anunciar?copiar={id}. */
const COPY_PARAM = "copiar";

/** Só o autor pode duplicar: de outra pessoa (ou inexistente), abre o formulário vazio. */
async function loadTemplate(id: string | string[] | undefined): Promise<AnuncioItem | undefined> {
  const value = Array.isArray(id) ? id[0] : id;
  if (!value) return undefined;

  const [ad, user] = await Promise.all([getAnuncio(value), getCurrentUser()]);
  return ad && ad.autorId === user.id ? ad : undefined;
}

async function AnunciarContent({ searchParams }: { searchParams: SearchParams }) {
  const params = await searchParams;
  const user = await getCurrentUser();
  if (user.validation !== "APROVADO") {
    return <ValidationRequired validation={user.validation} action="publicar anúncios" from="/anunciar" />;
  }
  const [categories, template] = await Promise.all([
    getCategories(),
    loadTemplate(params[COPY_PARAM]),
  ]);
  const backHref = backHrefFrom(params[FROM_PARAM], template ? "/historico" : "/home");

  return <AdForm categories={categories} template={template} backHref={backHref} />;
}

export default function AnunciarPage({ searchParams }: { searchParams: SearchParams }) {
  return (
    <Suspense fallback={<main className="min-h-dvh bg-fundo" />}>
      <AnunciarContent searchParams={searchParams} />
    </Suspense>
  );
}
