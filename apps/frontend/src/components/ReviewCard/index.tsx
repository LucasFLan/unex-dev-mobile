import { Avatar } from "@/components/Avatar";
import { Stars } from "@/components/Stars";
import { formatShortDate } from "@/lib/format";
import type { Review } from "@/lib/reviews";

export function ReviewCard({ review }: { review: Review }) {
  const author = review.autor?.nome || "Usuário removido";

  return (
    <article className="rounded-xl border border-slate-200 bg-white p-3 shadow-sm">
      <div className="flex items-center gap-2">
        <Avatar
          userId={review.autor?.id}
          name={author}
          version={review.autor ? (review.autor.fotoAtualizadaEm ?? undefined) : null}
          className="h-8 w-8 text-[10px]"
        />

        <div className="min-w-0 flex-1">
          <p className="truncate text-xs font-semibold">{author}</p>
          <p className="text-[10px] text-slate-500">{formatShortDate(review.criadoEm)}</p>
        </div>

        {review.servicoCategoria && (
          <span className="max-w-[40%] shrink-0 truncate rounded bg-amber-50 px-1.5 py-1 text-[10px] font-medium text-amber-800">
            {review.servicoCategoria}
          </span>
        )}
      </div>

      <div className="mt-2 flex items-center gap-1.5">
        <Stars rating={review.nota} />
        <span className="truncate text-[10px] text-slate-500">{review.servicoTitulo}</span>
      </div>

      {review.comentario && (
        <p className="mt-1.5 whitespace-pre-line text-[11px] leading-4 text-slate-600">{review.comentario}</p>
      )}
    </article>
  );
}
