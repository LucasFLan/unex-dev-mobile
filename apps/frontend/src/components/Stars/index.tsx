import { Star } from "lucide-react";
import { formatRating } from "@/lib/rating";

/** Cinco estrelas, preenchidas até a nota (arredondada). */
export function Stars({ rating, size = 12 }: { rating: number; size?: number }) {
  const filled = Math.round(rating);
  return (
    <span className="inline-flex gap-0.5" role="img" aria-label={`Nota ${formatRating(rating)} de 5`}>
      {[1, 2, 3, 4, 5].map((star) => (
        <Star
          key={star}
          size={size}
          aria-hidden="true"
          className={star <= filled ? "fill-amber-400 text-amber-400" : "fill-slate-200 text-slate-200"}
        />
      ))}
    </span>
  );
}

/** "★ 4,8 (12)" ou "Novo" para quem ainda não tem avaliações. */
export function RatingBadge({
  media,
  total,
  className = "text-[10px]",
}: {
  media: number | null;
  total: number;
  className?: string;
}) {
  if (media === null || total === 0) {
    return <span className={`font-medium text-slate-400 ${className}`}>Novo por aqui</span>;
  }
  return (
    <span
      className={`inline-flex items-center gap-0.5 font-semibold text-slate-700 ${className}`}
      aria-label={`Nota ${formatRating(media)} de 5, ${total} ${total === 1 ? "avaliação" : "avaliações"}`}
    >
      <Star size={11} aria-hidden="true" className="fill-amber-400 text-amber-400" />
      {formatRating(media)}
      <span className="font-normal text-slate-400">({total})</span>
    </span>
  );
}
