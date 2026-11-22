"use client";

import { useEffect } from "react";

/**
 * Registra o service worker (public/sw.js). Só em produção: no dev, o cache atrapalharia o hot reload.
 */
export function ServiceWorkerRegister() {
  useEffect(() => {
    if (process.env.NODE_ENV !== "production" || !("serviceWorker" in navigator)) return;
    navigator.serviceWorker.register("/sw.js", { scope: "/" }).catch(() => {
      // Sem service worker o app continua funcionando, só não instala/abre offline.
    });
  }, []);

  return null;
}
