"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import { House, Plus, History, Menu } from "lucide-react";

const TABS = [
  { label: "Início", href: "/home", icon: House },
  { label: "Anunciar", href: "/anunciar", icon: Plus, highlight: true },
  { label: "Histórico", href: "/historico", icon: History },
  { label: "Menu", href: "/menu", icon: Menu },
] as const;

export function BottomNav() {
  const pathname = usePathname();

  return (
    <nav
      aria-label="Navegação principal"
      className="fixed inset-x-0 bottom-0 z-20 border-t border-slate-200 bg-white pb-[max(8px,env(safe-area-inset-bottom))]"
    >
      <ul className="mx-auto grid h-15 w-full max-w-lg grid-cols-4">
        {TABS.map(({ label, href, icon: Icon, ...rest }) => {
            const highlight = "highlight" in rest && rest.highlight;
            const active = pathname === href || pathname.startsWith(`${href}/`);

          return (
            <li key={href}>
              <Link
                href={href}
                aria-current={active ? "page" : undefined}
                className={`flex h-full flex-col items-center justify-center gap-1 ${
                  active || highlight ? "text-blue-600" : "text-slate-500"
                }`}
              >
                <span
                  className={`flex h-7 w-7 items-center justify-center rounded-full ${
                    highlight ? "bg-blue-600 text-white" : ""
                  }`}
                >
                  <Icon size={highlight ? 19 : 18} strokeWidth={1.8} />
                </span>
                <span className="text-[9px] font-medium">{label}</span>
              </Link>
            </li>
          );
        })}
      </ul>
    </nav>
  );
}