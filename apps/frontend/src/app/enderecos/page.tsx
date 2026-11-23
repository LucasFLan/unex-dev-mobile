import type { Metadata } from "next";
import { MapPin } from "lucide-react";
import { ComingSoon } from "@/components/ComingSoon";

export const metadata: Metadata = { title: "Endereços" };

export default function Page() {
  return (
    <ComingSoon
      title="Endereços"
      description="Em breve você vai poder salvar seus endereços e usá-los nos anúncios com um toque."
      meanwhile="informe o CEP direto ao criar o anúncio; o endereço é preenchido sozinho."
      icon={MapPin}
      tone="bg-teal-100 text-teal-500"
    />
  );
}
