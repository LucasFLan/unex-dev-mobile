import { Suspense } from "react";
import { NotificacoesContent } from "@/components/NotificacoesContent";
import { backHrefFrom, currentHref, FROM_PARAM } from "@/lib/navigation";
import { getNotificacoes } from "@/lib/notifications";

type SearchParams = Promise<Record<string, string | string[] | undefined>>;

async function NotificacoesLoader({ searchParams }: { searchParams: SearchParams }) {
  const from = (await searchParams)[FROM_PARAM];
  const notifications = await getNotificacoes();
  const backHref = backHrefFrom(from, "/home");

  return (
    <NotificacoesContent
      notifications={notifications}
      backHref={backHref}
      selfHref={currentHref("/notificacoes", backHref === "/home" ? null : backHref)}
    />
  );
}

export default function NotificacoesPage({
  searchParams,
}: {
  searchParams: SearchParams;
}) {
  return (
    <Suspense fallback={<main className="min-h-dvh bg-fundo" />}>
      <NotificacoesLoader searchParams={searchParams} />
    </Suspense>
  );
}
