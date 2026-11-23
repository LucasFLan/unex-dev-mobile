import type { Metadata } from "next";
import { CircleHelp } from "lucide-react";
import { ComingSoon } from "@/components/ComingSoon";

export const metadata: Metadata = { title: "Central de ajuda" };

export default function Page() {
  return (
    <ComingSoon
      title="Central de ajuda"
      description="Estamos reunindo as dúvidas mais comuns sobre anúncios, propostas, avaliações e validação de conta."
      icon={CircleHelp}
      tone="bg-indigo-100 text-indigo-500"
    />
  );
}
