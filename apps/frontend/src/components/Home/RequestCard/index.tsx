import Link from "next/link";
import { Clock3, MapPin } from "lucide-react";
import { RelativeTime } from "@/components/RelativeTime";
import { type ServiceRequest } from "@/lib/marketplace";
import { formatPrice } from "@/lib/format";
import { formatDistance } from "@/lib/geo";
import { withFrom } from "@/lib/navigation";

export function RequestCard({
  request,
  from,
  className = "w-60",
}: {
  request: ServiceRequest;
  /** Tela de onde o detalhe foi aberto, para o voltar (padrão: Home). */
  from?: string;
  className?: string;
}) {
  const href = `/servicos/${request.id}`;

  return (
    <article className={`relative flex ${className} flex-col rounded-xl border border-slate-200 bg-white p-2.5 shadow-sm`}>
      <div className="flex items-center justify-between gap-1">
        <span className="truncate rounded bg-amber-50 px-1.5 py-1 text-[10px] font-medium text-amber-800">
          {request.category}
        </span>

        <span className="flex shrink-0 items-center gap-1 text-[10px] text-slate-500">
          <Clock3 size={10} aria-hidden="true" />
          <RelativeTime isoDate={request.createdAt} />
        </span>
      </div>

      <h3 className="mt-2 text-[11px] font-semibold leading-4">
        <Link
          href={from ? withFrom(href, from) : href}
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
            {request.distanceKm !== null ? `${formatDistance(request.distanceKm)} · ` : ""}
            {request.location}
          </span>
        </span>

        <strong className="shrink-0 text-[11px]">
          {formatPrice(request.price)}
        </strong>
      </div>

      <span className="relative mt-2 rounded-lg bg-blue-50 py-2 text-center text-[11px] font-semibold text-blue-600 transition hover:bg-blue-100">
        Ver pedido
      </span>
    </article>
  );
}
