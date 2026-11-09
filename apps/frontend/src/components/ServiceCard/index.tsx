import {
  Droplet,
  MoreVertical,
  PaintRoller,
  Sparkles,
  Star,
  Wrench,
  Zap,
} from "lucide-react";
import type {
  Service,
  ServiceCategory,
  ServiceStatus,
  ServiceType,
} from "@/lib/services";

const CATEGORY = {
  wrench: { icon: Wrench, style: "bg-emerald-100 text-emerald-500" },
  paint: { icon: PaintRoller, style: "bg-indigo-100 text-indigo-500" },
  zap: { icon: Zap, style: "bg-amber-100 text-amber-500" },
  drop: { icon: Droplet, style: "bg-blue-100 text-blue-500" },
  sparkles: { icon: Sparkles, style: "bg-purple-100 text-purple-500" },
} satisfies Record<
  ServiceCategory,
  { icon: typeof Wrench; style: string }
>;

const TYPE_STYLE: Record<ServiceType, string> = {
  Procuro: "bg-amber-50 text-amber-700",
  Ofereço: "bg-blue-50 text-blue-600",
  Contratei: "bg-purple-50 text-purple-600",
};

const STATUS_STYLE: Record<ServiceStatus, { text: string; dot: string }> = {
  Ativo: { text: "text-emerald-600", dot: "bg-emerald-500" },
  Pausado: { text: "text-slate-500", dot: "bg-slate-400" },
  Concluído: { text: "text-emerald-600", dot: "bg-emerald-500" },
  Cancelado: { text: "text-red-500", dot: "bg-red-500" },
};

type ServiceCardProps = {
  service: Service;
  onTogglePause: (id: number) => void;
  onNotify: (message: string) => void;
};

export function ServiceCard({
  service,
  onTogglePause,
  onNotify,
}: ServiceCardProps) {
  const { icon: Icon, style: iconStyle } = CATEGORY[service.category];
  const status = STATUS_STYLE[service.status];
  const isPaused = service.status === "Pausado";
  const canManage = service.status === "Ativo" || isPaused;

  return (
    <article className="rounded-xl border border-slate-200 bg-white p-2.5 shadow-sm">
      <div className="flex gap-2.5">
        <div
          className={`flex h-9 w-9 shrink-0 items-center justify-center rounded-lg ${iconStyle}`}
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

          {(service.price || service.payment) && (
            <p className="mt-0.5 text-[11px] font-semibold text-slate-800">
              {service.price}{" "}
              <span className="font-normal text-slate-500">
                {service.payment}
              </span>
            </p>
          )}
        </div>

        <button
          type="button"
          onClick={() => onNotify(`Opções de "${service.title}"`)}
          className="-mr-2 -mt-2 flex h-10 w-10 shrink-0 items-center justify-center text-slate-500"
          aria-label={`Mais opções de ${service.title}`}
        >
          <MoreVertical size={17} aria-hidden="true" />
        </button>
      </div>

      {service.rating && (
        <div className="mt-2 border-t border-slate-100 pt-2">
          <button
            type="button"
            onClick={() => onNotify("Área de avaliação selecionada.")}
            className="flex h-9 w-full items-center justify-center gap-1 rounded-md bg-blue-50 text-[10px] font-semibold text-blue-600"
          >
            <Star size={11} aria-hidden="true" />
            Avaliar serviço
          </button>
        </div>
      )}

      {service.status === "Cancelado" && (
        <div className="mt-2 border-t border-slate-100 pt-1">
          <p className="text-[10px] text-slate-500 line-through">
            Pago {service.price}
          </p>
        </div>
      )}

      {canManage && (
        <div className="mt-2 flex items-center gap-1.5">
          <button
            type="button"
            onClick={() => onNotify(`Editar: ${service.title}`)}
            className="h-8 rounded-md border border-slate-200 px-3 text-[10px] font-medium text-slate-700 active:bg-slate-100"
          >
            Editar
          </button>

          <button
            type="button"
            onClick={() => onTogglePause(service.id)}
            className="h-8 rounded-md border border-slate-200 px-3 text-[10px] font-medium text-slate-700 active:bg-slate-100"
          >
            {isPaused ? "Retomar" : "Pausar"}
          </button>

          {service.action && (
            <button
              type="button"
              onClick={() => onNotify(service.action ?? "")}
              className={`ml-auto flex h-8 items-center gap-1 rounded-md px-2 text-[10px] font-medium ${
                service.proposals
                  ? "bg-blue-50 text-blue-600"
                  : "text-slate-500"
              }`}
            >
              {service.action}
              {service.proposals ? (
                <span className="flex h-3.5 min-w-3.5 items-center justify-center rounded-full bg-blue-600 px-1 text-[9px] text-white">
                  {service.proposals}
                </span>
              ) : null}
            </button>
          )}
        </div>
      )}
    </article>
  );
}