import Link from "next/link";
import { MapPin } from "lucide-react";
import { getCategoryMeta } from "@/lib/categories";
import { Avatar } from "@/components/Avatar";
import { type Professional } from "@/lib/marketplace";
import { formatPrice } from "@/lib/format";
import { withFrom } from "@/lib/navigation";

export function ProfessionalCard({
  professional,
  from,
  className = "w-47.5",
}: {
  professional: Professional;
  /** Tela de onde o detalhe foi aberto, para o voltar (padrão: Home). */
  from?: string;
  className?: string;
}) {
  const href = `/profissionais/${professional.id}`;

  const { icon: Icon, bg, text } = getCategoryMeta(professional.categorySlug);

  return (
    <Link
      href={from ? withFrom(href, from) : href}
      className={`block ${className} overflow-hidden rounded-xl border border-slate-200 bg-white shadow-sm focus-visible:outline-2 focus-visible:outline-blue-600`}
    >
      <div
        className={`relative flex h-21.5 items-center justify-center ${bg}`}
      >
        <span className="absolute left-2 top-2 max-w-[85%] truncate rounded-md bg-white/90 px-2 py-1 text-[10px] font-medium text-amber-800">
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
        <h3 className="line-clamp-2 min-h-8 text-[11px] font-semibold leading-3.5">
          {professional.service}
        </h3>

        <div className="mt-1.5 flex items-center gap-1 text-[10px] text-slate-600">
          <Avatar
            userId={professional.authorId}
            name={professional.name}
            className="h-5 w-5 text-[8px]"
          />

          <span className="truncate">{professional.name}</span>
        </div>

        <div className="mt-1.5 flex items-center gap-1 text-[10px] text-slate-500">
          <MapPin size={11} className="shrink-0" aria-hidden="true" />
          <span className="truncate">{professional.location}</span>
        </div>

        <p className="mt-1.5 text-[10px] text-slate-500">
          {professional.price !== null && "A partir de "}
          <strong className="text-xs text-slate-900">
            {formatPrice(professional.price)}
          </strong>
        </p>
      </div>
    </Link>
  );
}
