import { KeyRound } from "lucide-react";
import { BackButton } from "@/components/BackButton";
import { MenuSection, type MenuItem } from "@/components/MenuSection";

const SECURITY_ITEMS: MenuItem[] = [
  {
    label: "Alterar senha",
    href: "/seguranca/senha",
    icon: KeyRound,
    tone: "bg-emerald-100 text-emerald-500",
  },
];

export default function SegurancaPage() {
  return (
    <main className="min-h-dvh bg-fundo pb-10 text-slate-800">
      <div className="mx-auto w-full max-w-lg">
        <header className="sticky top-0 z-20 flex items-center gap-1 bg-fundo px-2 pb-2 pt-3">
          <BackButton fallbackHref="/menu" label="Voltar para o menu" />
          <h1 className="text-[17px] font-bold tracking-tight">
            Privacidade e segurança
          </h1>
        </header>

        <div className="space-y-4 px-3.5">
          <MenuSection title="Acesso" items={SECURITY_ITEMS} />
        </div>
      </div>
    </main>
  );
}