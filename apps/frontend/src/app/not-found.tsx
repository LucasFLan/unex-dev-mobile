import Link from "next/link";
import { SearchX } from "lucide-react";

/** Página para endereços que não existem (ou anúncios que foram excluídos). */
export default function NotFound() {
  return (
    <main className="flex min-h-dvh flex-col justify-center bg-fundo px-4 py-10 text-slate-800">
      <section className="mx-auto w-full max-w-md rounded-2xl border border-slate-200 bg-white px-6 py-10 text-center shadow-sm md:px-10 md:py-12">
        <span className="mx-auto flex h-20 w-20 items-center justify-center rounded-3xl bg-blue-50 text-blue-600">
          <SearchX size={36} strokeWidth={1.6} aria-hidden="true" />
        </span>

        <p className="mt-6 text-sm font-semibold text-blue-600">Erro 404</p>
        <h1 className="mt-1 text-2xl font-bold tracking-tight">Página não encontrada</h1>
        <p className="mt-2 text-sm leading-6 text-slate-500">
          O endereço pode estar errado, ou o anúncio foi pausado ou excluído por quem publicou.
        </p>

        <div className="mt-8 grid gap-2 sm:grid-cols-2">
          <Link
            href="/home"
            className="flex h-11 items-center justify-center rounded-xl bg-blue-600 text-sm font-semibold text-white transition hover:bg-blue-700"
          >
            Ir para o início
          </Link>
          <Link
            href="/menu"
            className="flex h-11 items-center justify-center rounded-xl border border-slate-200 text-sm font-semibold text-slate-700 transition hover:bg-slate-50"
          >
            Abrir o menu
          </Link>
        </div>
      </section>
    </main>
  );
}
