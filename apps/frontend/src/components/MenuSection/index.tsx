import Link from "next/link";
import { ChevronRight, type LucideIcon } from "lucide-react";

export type MenuItem = {
  label: string;
  href: string;
  icon: LucideIcon;
  /** Classes de fundo e cor do ícone, no mesmo padrão das categorias. */
  tone: string;
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
      <h2 className="mb-2 px-1 text-[10px] font-medium uppercase tracking-wide text-slate-500">
        {title}
      </h2>

      <ul className="divide-y divide-slate-100 overflow-hidden rounded-xl border border-slate-200 bg-white shadow-sm">
        {items.map(({ label, href, icon: Icon, tone }) => (
          <li key={href}>
            <Link
              href={href}
              className="flex h-12 items-center gap-3 px-3 active:bg-slate-50 focus-visible:outline-2 focus-visible:-outline-offset-2 focus-visible:outline-blue-600"
            >
              <span
                className={`flex h-8 w-8 shrink-0 items-center justify-center rounded-lg ${tone}`}
              >
                <Icon size={17} strokeWidth={1.8} aria-hidden="true" />
              </span>

              <span className="flex-1 text-xs font-medium text-slate-800">
                {label}
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