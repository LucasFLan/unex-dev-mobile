import { Suspense } from "react";
import { redirect } from "next/navigation";
import { getCurrentUser } from "@/lib/user";

/** "Minhas avaliações" do menu: é o perfil público do próprio usuário. */
async function MinhasAvaliacoes(): Promise<null> {
  const user = await getCurrentUser();
  redirect(`/usuarios/${user.id}?de=%2Fmenu`);
}

export default function MinhasAvaliacoesPage() {
  return (
    <Suspense fallback={<main className="min-h-dvh bg-fundo" />}>
      <MinhasAvaliacoes />
    </Suspense>
  );
}
