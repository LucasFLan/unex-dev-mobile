import Link from "next/link";
import { Construction, type LucideIcon } from "lucide-react";
import { BottomNav } from "@/components/BottomNav";

type ComingSoonProps = {
  title: string;
  /** O que a tela vai permitir fazer. */
  description: string;
  /** Como resolver isso hoje, enquanto a tela não fica pronta. */
  meanwhile?: string;
  icon: LucideIcon;
  /** Fundo e cor do ícone (mesmas classes do Menu). */
  tone: string;
  /** Para onde o botão principal volta. */
  backHref?: string;
  backLabel?: string;
  /** Telas da área logada mostram a navegação do app; as públicas (termos), não. */
  withNav?: boolean;
};

/** Tela das funções que ainda estão em desenvolvimento, no lugar do 404. */
export function ComingSoon({
  title,
  description,
  meanwhile,
  icon: Icon,
  tone,
  backHref = "/menu",
  backLabel = "Voltar ao menu",
  withNav = true,
}: ComingSoonProps) {
  return (
    <main className="flex min-h-dvh flex-col bg-fundo px-4 pb-28 pt-10 text-slate-800 md:justify-center md:pb-16 lg:pb-12">
      <section className="mx-auto w-full max-w-md rounded-2xl border border-slate-200 bg-white px-6 py-10 text-center shadow-sm md:px-10 md:py-12">
        <div className="relative mx-auto w-fit">
          <span className={`flex h-20 w-20 items-center justify-center rounded-3xl ${tone}`}>
            <Icon size={36} strokeWidth={1.6} aria-hidden="true" />
          </span>
          <span className="absolute -bottom-2 -right-2 flex h-9 w-9 items-center justify-center rounded-full border-4 border-white bg-amber-400 text-white">
            <Construction size={16} strokeWidth={2.2} aria-hidden="true" />
          </span>
        </div>

        <p className="mt-6 inline-flex items-center gap-1.5 rounded-full bg-amber-50 px-3 py-1 text-xs font-semibold text-amber-700">
          <span className="h-1.5 w-1.5 animate-pulse rounded-full bg-amber-500" aria-hidden="true" />
          Em desenvolvimento
        </p>

        <h1 className="mt-3 text-2xl font-bold tracking-tight">{title}</h1>
        <p className="mt-2 text-sm leading-6 text-slate-500">{description}</p>

        {meanwhile && (
          <p className="mt-5 rounded-xl bg-slate-50 px-4 py-3 text-left text-sm leading-5 text-slate-600">
            <strong className="font-semibold text-slate-800">Enquanto isso: </strong>
            {meanwhile}
          </p>
        )}

        <div className="mt-8 grid gap-2 sm:grid-cols-2">
          <Link
            href={backHref}
            className="flex h-11 items-center justify-center rounded-xl bg-blue-600 text-sm font-semibold text-white transition hover:bg-blue-700"
          >
            {backLabel}
          </Link>
          <Link
            href="/home"
            className="flex h-11 items-center justify-center rounded-xl border border-slate-200 text-sm font-semibold text-slate-700 transition hover:bg-slate-50"
          >
            Ir para o início
          </Link>
        </div>
      </section>

      {withNav && <BottomNav />}
    </main>
  );
}
