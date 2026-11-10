import Link from "next/link";
import { BadgeCheck, Star } from "lucide-react";
import { CATEGORY_META } from "@/lib/categories";
import {
  initials,
  type Professional,
} from "@/lib/marketplace";
import {formatPrice} from "@/lib/format";
import {formatDistance, formatRating} from "@/lib/format";


export function ProfessionalCard({
  professional,
}: {
  professional: Professional;
}) {
  const { icon: Icon, bg, text } = CATEGORY_META[professional.category];

  return (
    <Link
      href={`/profissionais/${professional.id}`}
      className="block w-47.5 overflow-hidden rounded-xl border border-slate-200 bg-white shadow-sm focus-visible:outline-2 focus-visible:outline-blue-600"
    >
      <div
        className={`relative flex h-21.5 items-center justify-center ${bg}`}
      >
        <span className="absolute left-2 top-2 rounded-md bg-white/90 px-2 py-1 text-[10px] font-medium text-amber-800">
          {professional.category}
        </span>

        <Icon
          size={29}
          strokeWidth={1.7}
          className={text}
          aria-hidden="true"
        />
      </div>

      <div className="p-2">
        <h3 className="min-h-8 text-[11px] font-semibold leading-3.5">
          {professional.service}
        </h3>

        <div className="mt-1.5 flex items-center gap-1 text-[10px] text-slate-600">
          <span
            className="flex h-5 w-5 shrink-0 items-center justify-center rounded-full bg-slate-100 text-[10px] font-medium"
            aria-hidden="true"
          >
            {initials(professional.name)}
          </span>

          <span className="truncate">{professional.name}</span>
          <BadgeCheck
            size={11}
            className="shrink-0 text-blue-600"
            aria-label="Profissional verificado"
          />
        </div>

        <div className="mt-1.5 flex items-center gap-1 text-[10px] text-slate-500">
          <Star
            size={11}
            className="fill-amber-400 text-amber-400"
            aria-hidden="true"
          />
          <span className="font-semibold text-slate-800">
            {formatRating(professional.rating)}
          </span>
          <span>({professional.reviewsCount}) ·</span>
          <span className="truncate">
            {professional.neighborhood} ·{" "}
            {formatDistance(professional.distanceKm)}
          </span>
        </div>

        <p className="mt-1.5 text-[10px] text-slate-500">
          A partir de{" "}
          <strong className="text-xs text-slate-900">
            {formatPrice(professional.priceFrom)}
          </strong>
        </p>
      </div>
    </Link>
  );
}