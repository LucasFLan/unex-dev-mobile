import Link from "next/link";
import { ShieldAlert } from "lucide-react";
import { BackButton } from "@/components/BackButton";
import { BottomNav } from "@/components/BottomNav";
import { withFrom } from "@/lib/navigation";
import type { AccountValidation } from "@/lib/user";

/** Tela no lugar de uma ação que exige conta validada (ex.: criar anúncio). */
export function ValidationRequired({
  validation,
  action,
  from,
}: {
  validation: AccountValidation;
  /** Ex.: "publicar anúncios". */
  action: string;
  from: string;
}) {
  return (
    <main className="min-h-dvh bg-fundo pb-24 text-slate-800">
      <div className="mx-auto w-full max-w-md">
        <header className="flex items-center gap-1 px-2 pb-2 pt-3">
          <BackButton href="/home" />
        </header>

        <section className="mx-4 flex flex-col items-center rounded-xl border border-amber-200 bg-amber-50 px-5 py-8 text-center">
          <ShieldAlert size={38} className="text-amber-600" aria-hidden="true" />
          <h1 className="mt-3 text-base font-bold text-amber-900">Valide sua conta primeiro</h1>
          <p className="mt-1 text-xs leading-5 text-amber-800">
            Para {action}, precisamos confirmar sua identidade. É rápido: envie uma foto do seu
            documento e uma selfie segurando ele.
            {validation === "REPROVADO" && " Seu último envio não foi aprovado: veja o motivo e envie de novo."}
          </p>
          <Link
            href={withFrom("/validacao", from)}
            className="mt-5 h-11 w-full rounded-lg bg-blue-600 text-xs font-semibold leading-11 text-white"
          >
            {validation === "REPROVADO" ? "Ver motivo e reenviar" : "Validar minha conta"}
          </Link>
        </section>
      </div>

      <BottomNav />
    </main>
  );
}
