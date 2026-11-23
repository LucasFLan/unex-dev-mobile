import { Suspense } from "react";
import Link from "next/link";
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
  UserCheck,
  BadgeCheck,
  Star,
  User,
} from "lucide-react";
import { logout } from "@/app/actions";
import { BottomNav } from "@/components/BottomNav";
import { MenuSection, type MenuItem } from "@/components/MenuSection";
import { Avatar } from "@/components/Avatar";
import { getCurrentUser } from "@/lib/user";

const ADMIN_SECTION: { title: string; items: MenuItem[] } = {
  title: "Administração",
  items: [
    {
      label: "Validar usuários",
      description: "Analisar documentos enviados para validação",
      href: "/admin/validacoes",
      icon: UserCheck,
      tone: "bg-indigo-100 text-indigo-600",
    },
  ],
};

const SECTIONS: { title: string; items: MenuItem[] }[] = [
  {
    title: "Conta",
    items: [
      {
        label: "Meu perfil",
        description: "Seus dados, avaliações e anúncios",
        href: "/perfil",
        icon: User,
        tone: "bg-blue-100 text-blue-500",
      },
      {
        label: "Validar conta",
        description: "Envie seus documentos para publicar e propor",
        href: "/validacao?de=%2Fmenu",
        icon: BadgeCheck,
        tone: "bg-amber-100 text-amber-600",
      },
      {
        label: "Endereços",
        description: "Locais salvos para seus serviços",
        href: "/enderecos",
        icon: MapPin,
        tone: "bg-teal-100 text-teal-500",
      },
      {
        label: "Formas de pagamento",
        description: "Cartões e outras formas de pagar",
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
        description: "Acompanhe as propostas que você mandou",
        href: "/historico?aba=propostas",
        icon: MessageSquare,
        tone: "bg-orange-100 text-orange-500",
      },
      {
        label: "Favoritos",
        description: "Anúncios e profissionais que você salvou",
        href: "/favoritos",
        icon: Heart,
        tone: "bg-pink-100 text-pink-500",
      },
      {
        label: "Minhas avaliações",
        description: "O que você avaliou e o que recebeu",
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
        description: "Avisos de propostas, avaliações e validação",
        href: "/notificacoes?de=%2Fmenu",
        icon: Bell,
        tone: "bg-blue-100 text-blue-500",
      },
      {
        label: "Privacidade e segurança",
        description: "Senha e dados da sua conta",
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
        description: "Dúvidas frequentes e contato",
        href: "/ajuda",
        icon: CircleHelp,
        tone: "bg-indigo-100 text-indigo-500",
      },
      {
        label: "Termos e políticas",
        description: "Termos de uso e política de privacidade",
        href: "/termos",
        icon: FileText,
        tone: "bg-slate-100 text-slate-500",
      },
    ],
  },
];

async function MenuContent() {
  const user = await getCurrentUser();
  const sections = user.role === "ADMIN" ? [ADMIN_SECTION, ...SECTIONS] : SECTIONS;
  const verified = user.validation === "APROVADO";

  /*
   * Celular/tablet: cartão do perfil no topo, seções em lista e "Sair" no fim (como antes).
   * Desktop (lg+): painel do perfil à esquerda, que acompanha a rolagem, e as seções em grade à direita.
   */
  return (
    <main className="min-h-dvh bg-fundo pb-28 text-slate-800 lg:pb-12">
      <div className="mx-auto w-full max-w-lg px-3.5 pt-4 lg:max-w-6xl lg:px-8 lg:pt-8">
        <h1 className="mb-3 text-[17px] font-bold tracking-tight lg:mb-6 lg:text-2xl">Menu</h1>

        <div className="lg:grid lg:grid-cols-[19rem_minmax(0,1fr)] lg:items-start lg:gap-8">
          <aside className="hidden lg:sticky lg:top-8 lg:block lg:rounded-2xl lg:border lg:border-slate-200 lg:bg-white lg:p-6 lg:text-center lg:shadow-sm">
            <Avatar
              userId={user.id}
              name={user.name}
              version={user.photoVersion}
              className="mx-auto h-24 w-24 text-2xl"
            />

            <p className="mt-4 truncate text-lg font-bold">{user.name}</p>
            <p className="truncate text-sm text-slate-500">{user.email}</p>

            {verified ? (
              <p className="mt-3 inline-flex items-center gap-1 rounded-full bg-emerald-50 px-3 py-1 text-xs font-medium text-emerald-700">
                <BadgeCheck size={14} aria-hidden="true" />
                Conta verificada
              </p>
            ) : (
              <Link
                href="/validacao?de=%2Fmenu"
                className="mt-3 inline-flex items-center gap-1 rounded-full bg-amber-50 px-3 py-1 text-xs font-medium text-amber-700 hover:bg-amber-100"
              >
                <BadgeCheck size={14} aria-hidden="true" />
                Validar conta
              </Link>
            )}

            <div className="mt-6 grid gap-2">
              <Link
                href="/perfil"
                className="flex h-11 items-center justify-center rounded-xl bg-blue-600 text-sm font-semibold text-white transition hover:bg-blue-700"
              >
                Ver perfil
              </Link>
              <Link
                href="/perfil/editar"
                className="flex h-11 items-center justify-center rounded-xl border border-slate-200 text-sm font-semibold text-slate-700 transition hover:bg-slate-50"
              >
                Editar perfil
              </Link>
            </div>

            <form action={logout} className="mt-6 border-t border-slate-100 pt-6">
              <button
                type="submit"
                className="flex h-11 w-full items-center justify-center gap-2 rounded-xl text-sm font-semibold text-red-500 transition hover:bg-red-50"
              >
                <LogOut size={16} aria-hidden="true" />
                Sair da conta
              </button>
            </form>
          </aside>

          <div>
            <Link
              href="/perfil"
              className="flex items-center gap-3 rounded-xl border border-slate-200 bg-white p-3 shadow-sm transition hover:border-blue-200 focus-visible:outline-2 focus-visible:outline-blue-600 lg:hidden"
            >
              <Avatar
                userId={user.id}
                name={user.name}
                version={user.photoVersion}
                className="h-12 w-12 text-sm"
              />

              <div className="min-w-0 flex-1">
                <p className="truncate text-sm font-bold">{user.name}</p>
                <p className="truncate text-[11px] text-slate-500">
                  {user.email}
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

            <div className="mt-4 space-y-4 lg:mt-0 lg:grid lg:grid-cols-2 lg:items-start lg:gap-6 lg:space-y-0">
              {/* A seção do admin tem um item só: no desktop ocupa a linha toda, e as outras ficam em pares. */}
              {sections.map((section) => (
                <div key={section.title} className={section === ADMIN_SECTION ? "lg:col-span-2" : undefined}>
                  <MenuSection title={section.title} items={section.items} />
                </div>
              ))}

              <form action={logout} className="lg:hidden">
                <button
                  type="submit"
                  className="flex h-11 w-full items-center justify-center gap-2 rounded-xl border border-slate-200 bg-white text-xs font-semibold text-red-500 shadow-sm active:bg-red-50"
                >
                  <LogOut size={15} aria-hidden="true" />
                  Sair da conta
                </button>
              </form>
            </div>
          </div>
        </div>
      </div>

      <BottomNav />
    </main>
  );
}

export default function MenuPage() {
  return (
    <Suspense fallback={<main className="min-h-dvh bg-fundo" />}>
      <MenuContent />
    </Suspense>
  );
}