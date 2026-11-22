import { Suspense } from "react";
import Link from "next/link";
import { notFound } from "next/navigation";
import { ChevronRight, Inbox } from "lucide-react";
import { BackButton } from "@/components/BackButton";
import { RelativeTime } from "@/components/RelativeTime";
import { formatCpf } from "@/lib/format";
import { getCurrentUser } from "@/lib/user";
import { getFilaValidacao, type ReviewStatus } from "@/lib/validation";

type SearchParams = Promise<Record<string, string | string[] | undefined>>;

const TABS: { status: ReviewStatus; label: string }[] = [
  { status: "PENDENTE", label: "Pendentes" },
  { status: "APROVADO", label: "Aprovados" },
  { status: "REPROVADO", label: "Reprovados" },
];

async function FilaContent({ searchParams }: { searchParams: SearchParams }) {
  const user = await getCurrentUser();
  if (user.role !== "ADMIN") notFound();

  const param = (await searchParams).status;
  const status =
    TABS.find((tab) => tab.status === (Array.isArray(param) ? param[0] : param))?.status ?? "PENDENTE";
  const fila = await getFilaValidacao(status);
  if (!fila) notFound();

  return (
    <main className="min-h-dvh bg-fundo pb-10 text-slate-800">
      <div className="mx-auto w-full max-w-lg">
        <header className="sticky top-0 z-20 bg-fundo px-2 pb-2 pt-3">
          <div className="flex items-center gap-1">
            <BackButton href="/menu" />
            <h1 className="text-[17px] font-bold tracking-tight">Validar usuários</h1>
          </div>

          <nav aria-label="Filtrar por situação" className="mt-2 grid grid-cols-3 rounded-lg bg-slate-100 p-0.5 text-[11px] font-medium">
            {TABS.map((tab) => (
              <Link
                key={tab.status}
                href={`/admin/validacoes?status=${tab.status}`}
                aria-current={tab.status === status ? "page" : undefined}
                className={`flex h-8 items-center justify-center rounded-md ${
                  tab.status === status ? "bg-white text-blue-600 shadow-sm" : "text-slate-600"
                }`}
              >
                {tab.label}
              </Link>
            ))}
          </nav>
          <p className="mt-2 px-1 text-[10px] text-slate-500" role="status">
            {fila.total} {fila.total === 1 ? "envio" : "envios"}
            {status === "PENDENTE" && " na fila, do mais antigo para o mais novo"}
          </p>
        </header>

        <section className="space-y-2 px-3.5">
          {fila.itens.length === 0 ? (
            <div className="rounded-xl border border-slate-200 bg-white px-4 py-10 text-center">
              <Inbox size={25} className="mx-auto mb-2 text-slate-300" aria-hidden="true" />
              <p className="text-xs font-semibold text-slate-600">Nada por aqui</p>
              <p className="mt-1 text-[11px] text-slate-500">
                {status === "PENDENTE" ? "Nenhum documento esperando análise." : "Nenhum envio nessa situação."}
              </p>
            </div>
          ) : (
            fila.itens.map((envio) => (
              <Link
                key={envio.id}
                href={`/admin/validacoes/${envio.id}`}
                className="flex items-center gap-2 rounded-xl border border-slate-200 bg-white p-3 shadow-sm"
              >
                <div className="min-w-0 flex-1">
                  <p className="truncate text-xs font-semibold">{envio.usuario?.nome ?? "Usuário removido"}</p>
                  <p className="truncate text-[10px] text-slate-500">
                    {envio.usuario ? `${envio.usuario.email} · CPF ${formatCpf(envio.usuario.cpf)}` : "—"}
                  </p>
                  <p className="mt-0.5 text-[10px] text-slate-400">
                    Enviado <RelativeTime isoDate={envio.enviadoEm} />
                    {envio.motivoReprovacao && ` · Motivo: ${envio.motivoReprovacao}`}
                  </p>
                </div>
                <ChevronRight size={16} className="shrink-0 text-slate-400" aria-hidden="true" />
              </Link>
            ))
          )}
        </section>
      </div>
    </main>
  );
}

export default function AdminValidacoesPage({ searchParams }: { searchParams: SearchParams }) {
  return (
    <Suspense fallback={<main className="min-h-dvh bg-fundo" />}>
      <FilaContent searchParams={searchParams} />
    </Suspense>
  );
}
