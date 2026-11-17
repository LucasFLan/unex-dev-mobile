import { Star } from "lucide-react";
import { initials } from "@/lib/marketplace";
import { formatShortDate, type Review } from "@/lib/perfil";

export function ReviewCard({ review }: { review: Review }) {
  return (
    <article className="rounded-xl border border-slate-200 bg-white p-3 shadow-sm">
      <div className="flex items-center gap-2">
        <span
          className="flex h-8 w-8 shrink-0 items-center justify-center rounded-full bg-slate-100 text-[10px] font-semibold text-slate-600"
          aria-hidden="true"
        >
          {initials(review.authorName)}
        </span>

        <div className="min-w-0 flex-1">
          <p className="truncate text-xs font-semibold">{review.authorName}</p>
          <p className="text-[10px] text-slate-500">
            {formatShortDate(review.createdAt)}
          </p>
        </div>

        <span className="shrink-0 rounded bg-amber-50 px-1.5 py-1 text-[10px] font-medium text-amber-800">
          {review.category}
        </span>
      </div>

      <div
        className="mt-2 flex gap-0.5"
        role="img"
        aria-label={`Nota ${review.rating} de 5`}
      >
        {[1, 2, 3, 4, 5].map((star) => (
          <Star
            key={star}
            size={12}
            aria-hidden="true"
            className={
              star <= Math.round(review.rating)
                ? "fill-amber-400 text-amber-400"
                : "fill-slate-200 text-slate-200"
            }
          />
        ))}
      </div>

      <p className="mt-1.5 text-[11px] leading-4 text-slate-600">
        {review.comment}
      </p>
    </article>
  );
}