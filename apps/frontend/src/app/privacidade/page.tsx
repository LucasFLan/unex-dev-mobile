import type { Metadata } from "next";
import { ShieldCheck } from "lucide-react";
import { ComingSoon } from "@/components/ComingSoon";

export const metadata: Metadata = { title: "Política de privacidade" };

export default function Page() {
  return (
    <ComingSoon
      title="Política de privacidade"
      description="A política de privacidade está sendo finalizada e vai explicar como seus dados são guardados e usados."
      meanwhile="seus documentos da validação só são vistos pela equipe de administração, e seu endereço só por quem você aceitar."
      icon={ShieldCheck}
      tone="bg-emerald-100 text-emerald-500"
      backHref="/"
      backLabel="Voltar"
      withNav={false}
    />
  );
}
