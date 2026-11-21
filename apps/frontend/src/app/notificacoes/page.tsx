import { Suspense } from "react";
import { NotificacoesContent } from "@/components/NotificacoesContent";
import { getNotificacoes } from "@/lib/notifications";

async function NotificacoesLoader() {
  const notifications = await getNotificacoes();

  return <NotificacoesContent notifications={notifications} />;
}

export default function NotificacoesPage() {
  return (
    <Suspense fallback={<main className="min-h-dvh bg-fundo" />}>
      <NotificacoesLoader />
    </Suspense>
  );
}
