import type { Metadata } from "next";
import { CreditCard } from "lucide-react";
import { ComingSoon } from "@/components/ComingSoon";

export const metadata: Metadata = { title: "Formas de pagamento" };

export default function Page() {
  return (
    <ComingSoon
      title="Formas de pagamento"
      description="Estamos preparando o pagamento pelo app, com cartão e Pix, para contratar com mais segurança."
      meanwhile="combine o valor e a forma de pagamento direto com o profissional ou o cliente depois que a proposta for aceita."
      icon={CreditCard}
      tone="bg-violet-100 text-violet-500"
    />
  );
}
