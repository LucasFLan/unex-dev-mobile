"use client";

import { useEffect, useRef, useTransition } from "react";
import { usePathname, useRouter } from "next/navigation";

/** De quanto em quanto tempo as telas "vivas" buscam dados novos. */
const INTERVAL_MS = 5_000;

/**
 * Telas que mostram o que outras pessoas fazem (anúncios, propostas, notificações). Formulários ficam de fora:
 * o refresh mantém o que foi digitado, mas não há o que atualizar neles.
 */
const LIVE_PATHS = [
  "/home",
  "/profissionais",
  "/servicos",
  "/historico",
  "/notificacoes",
  "/anuncios",
  "/propostas",
  "/usuarios",
  "/menu",
  "/admin",
];

const isLive = (pathname: string) =>
  LIVE_PATHS.some((path) => pathname === path || pathname.startsWith(`${path}/`));

/**
 * Mantém a tela atualizada sem F5: refaz a renderização no servidor (router.refresh) ao abrir a tela, ao voltar
 * para a aba ou para o app, e a cada {@link INTERVAL_MS} enquanto ela está visível. O estado da tela (filtros,
 * campos, rolagem) é preservado; só os dados mudam.
 */
export function AutoRefresh() {
  const pathname = usePathname();
  const router = useRouter();
  const [pending, startTransition] = useTransition();
  // O intervalo lê o valor atual sem precisar recriar os listeners a cada refresh.
  const pendingRef = useRef(false);
  useEffect(() => {
    pendingRef.current = pending;
  }, [pending]);

  const live = isLive(pathname);

  useEffect(() => {
    if (!live) return;

    const refresh = () => {
      if (document.visibilityState !== "visible" || !navigator.onLine || pendingRef.current) return;
      startTransition(() => router.refresh());
    };

    // Ao chegar na tela: o Next pode ter mostrado uma cópia guardada no navegador.
    refresh();
    const timer = window.setInterval(refresh, INTERVAL_MS);
    const onVisible = () => {
      if (document.visibilityState === "visible") refresh();
    };
    document.addEventListener("visibilitychange", onVisible);
    window.addEventListener("focus", refresh);
    window.addEventListener("online", refresh);

    return () => {
      window.clearInterval(timer);
      document.removeEventListener("visibilitychange", onVisible);
      window.removeEventListener("focus", refresh);
      window.removeEventListener("online", refresh);
    };
  }, [live, pathname, router]);

  return null;
}
