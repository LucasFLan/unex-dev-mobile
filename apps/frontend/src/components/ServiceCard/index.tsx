import Link from "next/link";
import { ChevronRight } from "lucide-react";
import { getCategoryMeta } from "@/lib/categories";
import { duplicateAdHref } from "@/lib/ad-links";
import { withFrom } from "@/lib/navigation";
import type { Service, ServiceStatus, ServiceType } from "@/lib/services";

const TYPE_STYLE: Record<ServiceType, string> = {
  Procuro: "bg-amber-50 text-amber-700",
  Ofereço: "bg-blue-50 text-blue-600",
};

const STATUS_STYLE: Record<ServiceStatus, { text: string; dot: string }> = {
  Ativo: { text: "text-emerald-600", dot: "bg-emerald-500" },
  Pausado: { text: "text-slate-500", dot: "bg-slate-400" },
};

type ServiceCardProps = {
  service: Service;
  /** Desabilita as ações enquanto uma chamada à API está em andamento. */
  busy?: boolean;
  onTogglePause: (id: string) => void;
  onDelete: (id: string) => void;
};

export function ServiceCard({
  service,
  busy = false,
  onTogglePause,
  onDelete,
}: ServiceCardProps) {
  const { icon: Icon, bg, text } = getCategoryMeta(service.categorySlug);
  const status = STATUS_STYLE[service.status];
  const isPaused = service.status === "Pausado";

  return (
    <article className="rounded-xl border border-slate-200 bg-white p-2.5 shadow-sm">
      <div className="flex gap-2.5">
        <div
          className={`flex h-9 w-9 shrink-0 items-center justify-center rounded-lg ${bg} ${text}`}
        >
          <Icon size={19} strokeWidth={2.2} aria-hidden="true" />
        </div>

        <div className="min-w-0 flex-1">
          <div className="mb-1 flex flex-wrap items-center gap-1">
            <span
              className={`rounded px-1.5 py-0.5 text-[10px] font-semibold ${TYPE_STYLE[service.type]}`}
            >
              {service.type}
            </span>

            <span
              className={`flex items-center gap-1 text-[10px] font-medium ${status.text}`}
            >
              <span
                className={`h-1.5 w-1.5 rounded-full ${status.dot}`}
                aria-hidden="true"
              />
              {service.status}
            </span>
          </div>

          <h3 className="text-xs font-semibold leading-snug text-slate-800">
            {service.title}
          </h3>

          <p className="mt-1 text-[10px] text-slate-500">
            {service.description}
          </p>

          <p className="mt-0.5 text-[11px] font-semibold text-slate-800">
            {service.price}
          </p>
        </div>

        <Link
          href={service.href}
          className="-mr-2 -mt-2 flex h-10 w-10 shrink-0 items-center justify-center text-slate-500"
          aria-label={`Ver anúncio ${service.title}`}
        >
          <ChevronRight size={17} aria-hidden="true" />
        </Link>
      </div>

      <div className="mt-2 flex items-center gap-1.5">
        <Link
          href={withFrom(`/anunciar/${service.id}/editar`, "/historico")}
          className="flex h-8 items-center rounded-md border border-slate-200 px-3 text-[10px] font-medium text-slate-700 active:bg-slate-100"
        >
          Editar
        </Link>

        <button
          type="button"
          onClick={() => onTogglePause(service.id)}
          disabled={busy}
          className="h-8 rounded-md border border-slate-200 px-3 text-[10px] font-medium text-slate-700 active:bg-slate-100 disabled:opacity-50"
        >
          {isPaused ? "Retomar" : "Pausar"}
        </button>

        <Link
          href={duplicateAdHref(service.id, "/historico")}
          className="flex h-8 items-center rounded-md border border-slate-200 px-3 text-[10px] font-medium text-slate-700 active:bg-slate-100"
        >
          Duplicar
        </Link>

        <button
          type="button"
          onClick={() => onDelete(service.id)}
          disabled={busy}
          className="ml-auto h-8 rounded-md px-3 text-[10px] font-medium text-red-500 active:bg-red-50 disabled:opacity-50"
        >
          Excluir
        </button>
      </div>
    </article>
  );
}
