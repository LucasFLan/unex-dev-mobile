import Link from "next/link";
import { ChevronRight, type LucideIcon } from "lucide-react";

export type MenuItem = {
  label: string;
  href: string;
  icon: LucideIcon;
  /** Classes de fundo e cor do ícone, no mesmo padrão das categorias. */
  tone: string;
  /** Explicação curta, mostrada só no desktop (onde sobra espaço). */
  description?: string;
};

export function MenuSection({
  title,
  items,
}: {
  title: string;
  items: MenuItem[];
}) {
  return (
    <section aria-label={title}>
      <h2 className="mb-2 px-1 text-[10px] font-medium uppercase tracking-wide text-slate-500 lg:mb-3 lg:text-xs">
        {title}
      </h2>

      <ul className="divide-y divide-slate-100 overflow-hidden rounded-xl border border-slate-200 bg-white shadow-sm">
        {items.map(({ label, href, icon: Icon, tone, description }) => (
          <li key={href}>
            <Link
              href={href}
              className="flex h-12 items-center gap-3 px-3 hover:bg-slate-50 active:bg-slate-50 focus-visible:outline-2 focus-visible:-outline-offset-2 focus-visible:outline-blue-600 lg:h-auto lg:gap-4 lg:px-4 lg:py-3.5"
            >
              <span
                className={`flex h-8 w-8 shrink-0 items-center justify-center rounded-lg lg:h-10 lg:w-10 lg:rounded-xl ${tone}`}
              >
                <Icon size={17} strokeWidth={1.8} aria-hidden="true" className="lg:size-5" />
              </span>

              <span className="min-w-0 flex-1">
                <span className="block text-xs font-medium text-slate-800 lg:text-sm lg:font-semibold">
                  {label}
                </span>
                {description && (
                  <span className="hidden text-xs text-slate-500 lg:mt-0.5 lg:block">{description}</span>
                )}
              </span>

              <ChevronRight
                size={15}
                className="shrink-0 text-slate-400"
                aria-hidden="true"
              />
            </Link>
          </li>
        ))}
      </ul>
    </section>
  );
}