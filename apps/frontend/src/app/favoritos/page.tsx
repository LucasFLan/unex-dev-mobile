import type { Metadata } from "next";
import { Heart } from "lucide-react";
import { ComingSoon } from "@/components/ComingSoon";

export const metadata: Metadata = { title: "Favoritos" };

export default function Page() {
  return (
    <ComingSoon
      title="Favoritos"
      description="Em breve você vai poder salvar anúncios e profissionais para encontrar de novo quando precisar."
      meanwhile="use a busca da Home para achar de novo um anúncio ou profissional."
      icon={Heart}
      tone="bg-pink-100 text-pink-500"
    />
  );
}
