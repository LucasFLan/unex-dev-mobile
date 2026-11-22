import { Suspense } from "react";
import { notFound } from "next/navigation";
import { BackButton } from "@/components/BackButton";
import { RelativeTime } from "@/components/RelativeTime";
import { ValidationReview } from "@/components/ValidationReview";
import { formatCpf, formatPhone, formatShortDate } from "@/lib/format";
import { getCurrentUser } from "@/lib/user";
import { getEnvioValidacao } from "@/lib/validation";

const STATUS_LABEL = { PENDENTE: "Pendente", APROVADO: "Aprovado", REPROVADO: "Reprovado" } as const;

function Row({ label, value }: { label: string; value: string }) {
  return (
    <div className="flex justify-between gap-3 py-1.5 text-[11px]">
      <dt className="text-slate-500">{label}</dt>
      <dd className="text-right font-medium text-slate-800">{value}</dd>
    </div>
  );
}

async function EnvioContent({ params }: { params: Promise<{ id: string }> }) {
  const [{ id }, user] = await Promise.all([params, getCurrentUser()]);
  if (user.role !== "ADMIN") notFound();

  const envio = await getEnvioValidacao(id);
  if (!envio) notFound();
  const pessoa = envio.usuario;

  return (
    <main className="min-h-dvh bg-fundo pb-10 text-slate-800">
      <div className="mx-auto w-full max-w-lg">
        <header className="sticky top-0 z-20 flex items-center gap-1 bg-fundo px-2 pb-2 pt-3">
          <BackButton href={`/admin/validacoes?status=${envio.status}`} />
          <h1 className="text-[17px] font-bold tracking-tight">Analisar documentos</h1>
        </header>

        <div className="space-y-4 px-3.5">
          <section className="rounded-xl border border-slate-200 bg-white p-3 shadow-sm">
            <div className="flex items-center justify-between">
              <h2 className="text-xs font-bold">Dados do cadastro</h2>
              <span className="rounded bg-slate-100 px-1.5 py-0.5 text-[10px] font-semibold text-slate-600">
                {STATUS_LABEL[envio.status]}
              </span>
            </div>
            <p className="mt-0.5 text-[10px] text-slate-500">
              Confira se batem com o documento e se o rosto da selfie é o mesmo da foto do documento.
            </p>
            <dl className="mt-2 divide-y divide-slate-100">
              <Row label="Nome" value={pessoa?.nome ?? "—"} />
              <Row label="CPF" value={pessoa ? formatCpf(pessoa.cpf) : "—"} />
              <Row label="Nascimento" value={pessoa?.dataNascimento ? formatShortDate(pessoa.dataNascimento) : "—"} />
              <Row label="E-mail" value={pessoa?.email ?? "—"} />
              <Row label="Telefone" value={pessoa?.telefone ? formatPhone(pessoa.telefone) : "—"} />
            </dl>
            <p className="mt-1 text-[10px] text-slate-400">
              Enviado <RelativeTime isoDate={envio.enviadoEm} />
            </p>
          </section>

          {(["DOCUMENTO", "SELFIE"] as const).map((tipo) => (
            <section key={tipo} className="rounded-xl border border-slate-200 bg-white p-3 shadow-sm">
              <h2 className="text-xs font-bold">{tipo === "DOCUMENTO" ? "Documento" : "Selfie com o documento"}</h2>
              <a
                href={`/api/admin/validacoes/${envio.id}/${tipo.toLowerCase()}`}
                target="_blank"
                rel="noopener noreferrer"
                className="mt-2 block overflow-hidden rounded-lg bg-slate-100"
                title="Abrir em tamanho original"
              >
                {/* eslint-disable-next-line @next/next/no-img-element -- imagem privada servida pelo proxy, sem cache */}
                <img
                  src={`/api/admin/validacoes/${envio.id}/${tipo.toLowerCase()}`}
                  alt={tipo === "DOCUMENTO" ? "Foto do documento enviada" : "Selfie enviada"}
                  className="max-h-96 w-full object-contain"
                />
              </a>
              <p className="mt-1 text-[10px] text-slate-400">Toque na imagem para ver em tamanho original.</p>
            </section>
          ))}

          {envio.status === "PENDENTE" ? (
            <ValidationReview envioId={envio.id} />
          ) : (
            <p className="rounded-xl border border-slate-200 bg-white p-3 text-[11px] text-slate-600">
              {envio.status === "APROVADO" ? "Aprovado" : "Reprovado"}
              {envio.analisadoEm && <> <RelativeTime isoDate={envio.analisadoEm} /></>}
              {envio.motivoReprovacao && <>. Motivo: {envio.motivoReprovacao}</>}
            </p>
          )}
        </div>
      </div>
    </main>
  );
}

export default function AdminEnvioPage({ params }: { params: Promise<{ id: string }> }) {
  return (
    <Suspense fallback={<main className="min-h-dvh bg-fundo" />}>
      <EnvioContent params={params} />
    </Suspense>
  );
}
