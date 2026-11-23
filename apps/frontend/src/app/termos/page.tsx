import type { Metadata } from "next";
import { FileText } from "lucide-react";
import { ComingSoon } from "@/components/ComingSoon";

export const metadata: Metadata = { title: "Termos de uso" };

export default function Page() {
  return (
    <ComingSoon
      title="Termos de uso"
      description="Os termos de uso do ChamaAí estão sendo finalizados e vão aparecer aqui."
      icon={FileText}
      tone="bg-slate-100 text-slate-500"
      backHref="/"
      backLabel="Voltar"
      withNav={false}
    />
  );
}
