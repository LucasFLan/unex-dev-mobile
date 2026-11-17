"use client";

import Link from "next/link";
import { useRouter } from "next/navigation";
import {
  Bell,
  ChevronRight,
  CircleHelp,
  CreditCard,
  FileText,
  Heart,
  LogOut,
  MapPin,
  MessageSquare,
  ShieldCheck,
  Star,
  User,
} from "lucide-react";
import { BottomNav } from "@/components/BottomNav";
import { MenuSection, type MenuItem } from "@/components/MenuSection";
import { initials } from "@/lib/marketplace";
import { currentUser } from "@/lib/mocks";

const SECTIONS: { title: string; items: MenuItem[] }[] = [
  {
    title: "Conta",
    items: [
      {
        label: "Meu perfil",
        href: "/perfil",
        icon: User,
        tone: "bg-blue-100 text-blue-500",
      },
      {
        label: "Endereços",
        href: "/enderecos",
        icon: MapPin,
        tone: "bg-teal-100 text-teal-500",
      },
      {
        label: "Formas de pagamento",
        href: "/pagamentos",
        icon: CreditCard,
        tone: "bg-violet-100 text-violet-500",
      },
    ],
  },
  {
    title: "Atividade",
    items: [
      {
        label: "Propostas enviadas",
        href: "/propostas",
        icon: MessageSquare,
        tone: "bg-orange-100 text-orange-500",
      },
      {
        label: "Favoritos",
        href: "/favoritos",
        icon: Heart,
        tone: "bg-pink-100 text-pink-500",
      },
      {
        label: "Minhas avaliações",
        href: "/avaliacoes",
        icon: Star,
        tone: "bg-amber-100 text-amber-500",
      },
    ],
  },
  {
    title: "Preferências",
    items: [
      {
        label: "Notificações",
        href: "/notificacoes",
        icon: Bell,
        tone: "bg-blue-100 text-blue-500",
      },
      {
        label: "Privacidade e segurança",
        href: "/seguranca",
        icon: ShieldCheck,
        tone: "bg-emerald-100 text-emerald-500",
      },
    ],
  },
  {
    title: "Ajuda",
    items: [
      {
        label: "Central de ajuda",
        href: "/ajuda",
        icon: CircleHelp,
        tone: "bg-indigo-100 text-indigo-500",
      },
      {
        label: "Termos e políticas",
        href: "/termos",
        icon: FileText,
        tone: "bg-slate-100 text-slate-500",
      },
    ],
  },
];

export default function MenuPage() {
  const router = useRouter();

  const handleLogout = () => {
    router.push("/login");
  };

  return (
    <main className="min-h-dvh bg-fundo pb-28 text-slate-800">
      <div className="mx-auto w-full max-w-lg px-3.5 pt-4">
        <h1 className="mb-3 text-[17px] font-bold tracking-tight">Menu</h1>

        <Link
          href="/perfil"
          className="flex items-center gap-3 rounded-xl border border-slate-200 bg-white p-3 shadow-sm focus-visible:outline-2 focus-visible:outline-blue-600"
        >
          <span
            className="flex h-12 w-12 shrink-0 items-center justify-center rounded-full bg-blue-100 text-sm font-bold text-blue-600"
            aria-hidden="true"
          >
            {initials(currentUser.name)}
          </span>

          <div className="min-w-0 flex-1">
            <p className="truncate text-sm font-bold">{currentUser.name}</p>
            <p className="truncate text-[11px] text-slate-500">
              {currentUser.email}
            </p>
            <p className="mt-0.5 text-[11px] font-medium text-blue-600">
              Ver perfil
            </p>
          </div>

          <ChevronRight
            size={15}
            className="shrink-0 text-slate-400"
            aria-hidden="true"
          />
        </Link>

        <div className="mt-4 space-y-4">
          {SECTIONS.map((section) => (
            <MenuSection
              key={section.title}
              title={section.title}
              items={section.items}
            />
          ))}

          <button
            type="button"
            onClick={handleLogout}
            className="flex h-11 w-full items-center justify-center gap-2 rounded-xl border border-slate-200 bg-white text-xs font-semibold text-red-500 shadow-sm active:bg-red-50"
          >
            <LogOut size={15} aria-hidden="true" />
            Sair da conta
          </button>
        </div>
      </div>

      <BottomNav />
    </main>
  );
}