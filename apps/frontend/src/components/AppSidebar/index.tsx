"use client";

import Image from "next/image";
import Link from "next/link";
import { usePathname } from "next/navigation";
import { Bell, History, House, Menu, Plus } from "lucide-react";
import Logo from "@/../public/logo.svg";

/** Telas sem a navegação do app (landing e telas públicas). */
const PUBLIC_PATHS = ["/login", "/register", "/recuperar-senha", "/termos", "/privacidade"];

const matches = (pathname: string, paths: readonly string[]) =>
  paths.some((path) => pathname === path || pathname.startsWith(`${path}/`));

/** Cada item fica ativo na própria rota e nas telas que pertencem a ela. */
const ITEMS = [
  { label: "Início", href: "/home", icon: House, also: ["/profissionais", "/servicos"] },
  { label: "Histórico", href: "/historico", icon: History, also: ["/anuncios"] },
  { label: "Notificações", href: "/notificacoes", icon: Bell, also: [] },
  {
    label: "Menu",
    href: "/menu",
    icon: Menu,
    also: ["/perfil", "/seguranca", "/validacao", "/admin"],
  },
] as const;

/**
 * Navegação lateral do app no desktop (lg+). No mobile quem navega é a BottomNav.
 * O recuo do conteúdo (15rem) é aplicado em globals.css quando a barra está na página.
 */
export function AppSidebar() {
  const pathname = usePathname();

  if (pathname === "/" || matches(pathname, PUBLIC_PATHS)) return null;

  const creating = matches(pathname, ["/anunciar"]);

  return (
    <aside
      data-app-sidebar
      className="fixed inset-y-0 left-0 z-40 hidden w-60 flex-col border-r border-slate-200 bg-white px-4 py-6 lg:flex"
    >
      <Link
        href="/home"
        className="mb-8 flex items-center gap-2 rounded-xl px-2 focus-visible:outline-2 focus-visible:outline-blue-600"
      >
        <Image src={Logo} alt="" width={40} height={40} />
        <span className="text-xl font-extrabold tracking-tight text-slate-900">
          Chama<span className="text-blue-600">Aí</span>
        </span>
      </Link>

      <nav aria-label="Navegação principal">
        <ul className="flex flex-col gap-1">
          {ITEMS.map(({ label, href, icon: Icon, also }) => {
            const active = matches(pathname, [href, ...also]);

            return (
              <li key={href}>
                <Link
                  href={href}
                  aria-current={active ? "page" : undefined}
                  className={`flex h-11 items-center gap-3 rounded-xl px-3 text-sm font-semibold transition ${
                    active
                      ? "bg-blue-50 text-blue-600"
                      : "text-slate-600 hover:bg-slate-50 hover:text-slate-900"
                  }`}
                >
                  <Icon size={19} strokeWidth={1.8} aria-hidden="true" />
                  {label}
                </Link>
              </li>
            );
          })}
        </ul>

        <Link
          href="/anunciar"
          aria-current={creating ? "page" : undefined}
          className="mt-6 flex h-11 items-center gap-3 rounded-xl bg-blue-600 px-3 text-sm font-semibold text-white shadow-lg shadow-blue-200 transition hover:bg-blue-700"
        >
          <Plus size={19} strokeWidth={2} aria-hidden="true" />
          Criar anúncio
        </Link>
      </nav>
    </aside>
  );
}
