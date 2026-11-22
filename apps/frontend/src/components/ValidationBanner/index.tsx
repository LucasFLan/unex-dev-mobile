import Link from "next/link";
import { ChevronRight, ShieldAlert } from "lucide-react";
import type { AccountValidation } from "@/lib/user";
import { withFrom } from "@/lib/navigation";

const TEXT: Record<Exclude<AccountValidation, "APROVADO">, { title: string; body: string }> = {
  PENDENTE: {
    title: "Valide sua conta",
    body: "Envie a foto do documento e uma selfie para publicar anúncios e enviar propostas.",
  },
  REPROVADO: {
    title: "Documentos não aprovados",
    body: "Veja o motivo e envie de novo para liberar anúncios e propostas.",
  },
};

/** Aviso para quem ainda não tem a conta aprovada. Não aparece para contas aprovadas. */
export function ValidationBanner({
  validation,
  from,
  className = "",
}: {
  validation: AccountValidation;
  /** Tela atual, para o voltar da validação trazer de volta. */
  from: string;
  className?: string;
}) {
  if (validation === "APROVADO") return null;
  const { title, body } = TEXT[validation];

  return (
    <Link
      href={withFrom("/validacao", from)}
      className={`flex items-center gap-2.5 rounded-xl border border-amber-200 bg-amber-50 p-3 ${className}`}
    >
      <ShieldAlert size={20} className="shrink-0 text-amber-600" aria-hidden="true" />
      <span className="min-w-0 flex-1">
        <span className="block text-xs font-semibold text-amber-900">{title}</span>
        <span className="block text-[11px] leading-4 text-amber-800">{body}</span>
      </span>
      <ChevronRight size={16} className="shrink-0 text-amber-600" aria-hidden="true" />
    </Link>
  );
}
