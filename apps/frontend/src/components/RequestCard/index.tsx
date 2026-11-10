import Link from "next/link";
import { Clock3, MapPin } from "lucide-react";
import {
  type ServiceRequest,
} from "@/lib/marketplace";
import {formatPrice} from "@/lib/format";
import {formatDistance, formatRelativeTime} from "@/lib/format";

export function RequestCard({ request }: { request: ServiceRequest }) {
  return (
    <article className="relative flex w-60 flex-col rounded-xl border border-slate-200 bg-white p-2.5 shadow-sm">
      <div className="flex items-center justify-between gap-1">
        <div className="flex items-center gap-1">
          <span className="rounded bg-amber-50 px-1.5 py-1 text-[10px] font-medium text-amber-800">
            {request.category}
          </span>

          {request.urgent && (
            <span className="rounded bg-red-50 px-1.5 py-1 text-[10px] font-medium text-red-600">
              Urgente
            </span>
          )}
        </div>

        <span
          className="flex shrink-0 items-center gap-1 text-[10px] text-slate-500"
          suppressHydrationWarning
        >
          <Clock3 size={10} aria-hidden="true" />
          {formatRelativeTime(request.createdAt)}
        </span>
      </div>

      <h3 className="mt-2 text-[11px] font-semibold leading-4">
        <Link
          href={`/servicos/${request.id}`}
          className="after:absolute after:inset-0 focus-visible:outline-2 focus-visible:outline-blue-600"
        >
          {request.title}
        </Link>
      </h3>

      <p className="mt-1 line-clamp-3 min-h-10.5 text-[10px] leading-3.5 text-slate-500">
        {request.description}
      </p>

      <div className="mt-2 flex items-center justify-between gap-2 border-t border-slate-100 pt-2">
        <span className="flex min-w-0 items-center gap-1 text-[10px] text-slate-500">
          <MapPin size={11} className="shrink-0" aria-hidden="true" />
          <span className="truncate">
            {request.neighborhood} · {formatDistance(request.distanceKm)}
          </span>
        </span>

        <strong className="shrink-0 text-[11px]">
          {formatPrice(request.price)}
        </strong>
      </div>

      <Link
        href={`/servicos/${request.id}/proposta`}
        className="relative z-10 mt-2 rounded-lg bg-blue-50 py-2 text-center text-[11px] font-semibold text-blue-600 transition hover:bg-blue-100"
      >
        Enviar proposta
      </Link>
    </article>
  );
}