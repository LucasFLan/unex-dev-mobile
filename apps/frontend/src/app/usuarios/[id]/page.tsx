import { Suspense } from "react";
import type { Metadata } from "next";
import { notFound } from "next/navigation";
import { Star } from "lucide-react";
import { Avatar } from "@/components/Avatar";
import { BackButton } from "@/components/BackButton";
import { ReviewCard } from "@/components/ReviewCard";
import { Stars } from "@/components/Stars";
import { isUuid } from "@/lib/avatar";
import { backHrefFrom, FROM_PARAM } from "@/lib/navigation";
import { formatRating, getUserReviews } from "@/lib/reviews";
import { getCurrentUser } from "@/lib/user";

export const metadata: Metadata = { title: "Avaliações" };

type Props = {
  params: Promise<{ id: string }>;
  searchParams: Promise<Record<string, string | string[] | undefined>>;
};

async function PerfilPublico({ params, searchParams }: Props) {
  const { id } = await params;
  if (!isUuid(id)) notFound();
  const backHref = backHrefFrom((await searchParams)[FROM_PARAM], "/home");
  const [reviews, me] = await Promise.all([getUserReviews(id), getCurrentUser()]);
  if (!reviews) notFound();

  const mine = me.id === reviews.userId;

  return (
    <main className="min-h-dvh bg-fundo pb-10 text-slate-800">
      <div className="mx-auto w-full max-w-lg">
        <header className="flex items-center gap-1 px-2 pt-3">
          <BackButton href={backHref} />
          <h1 className="text-[17px] font-bold tracking-tight">{mine ? "Minhas avaliações" : "Avaliações"}</h1>
        </header>

        <section className="mx-3.5 mt-2 flex flex-col items-center rounded-xl border border-slate-200 bg-white px-4 py-5 text-center shadow-sm">
          <Avatar userId={reviews.userId} name={reviews.nome} version={reviews.fotoAtualizadaEm} className="h-20 w-20 text-2xl" />
          <h2 className="mt-3 text-base font-bold">{reviews.nome}</h2>

          {reviews.media !== null && reviews.total > 0 ? (
            <div className="mt-2 flex flex-col items-center">
              <p className="text-3xl font-bold leading-none">{formatRating(reviews.media)}</p>
              <div className="mt-1.5">
                <Stars rating={reviews.media} size={16} />
              </div>
              <p className="mt-1 text-xs text-slate-500">
                {reviews.total} {reviews.total === 1 ? "avaliação" : "avaliações"}
              </p>
            </div>
          ) : (
            <p className="mt-2 flex items-center gap-1 text-xs text-slate-500">
              <Star size={13} aria-hidden="true" />
              {mine ? "Você ainda não recebeu avaliações." : "Ainda não recebeu avaliações."}
            </p>
          )}
        </section>

        {reviews.itens.length > 0 && (
          <section className="mt-4 px-3.5" aria-labelledby="titulo-avaliacoes">
            <h2 id="titulo-avaliacoes" className="mb-2 text-xs font-bold">
              O que dizem os clientes
            </h2>
            <ul className="space-y-2">
              {reviews.itens.map((review) => (
                <li key={review.id}>
                  <ReviewCard review={review} />
                </li>
              ))}
            </ul>
          </section>
        )}
      </div>
    </main>
  );
}

export default function PerfilPublicoPage(props: Props) {
  return (
    <Suspense fallback={<main className="min-h-dvh bg-fundo" />}>
      <PerfilPublico {...props} />
    </Suspense>
  );
}
