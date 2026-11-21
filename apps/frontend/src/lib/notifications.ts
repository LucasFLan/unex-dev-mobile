import { cache } from "react";
import { redirect } from "next/navigation";
import { apiFetch, type ApiResult } from "@/lib/api";
import { ENDPOINTS } from "@/lib/api-contract";

export type NotificationItem = {
  id: string;
  tipo: string;
  titulo: string;
  mensagem: string;
  /** `null` quando o anúncio foi excluído. */
  anuncioId: string | null;
  propostaId: string | null;
  lida: boolean;
  criadoEm: string;
};

const text = (value: unknown) => (typeof value === "string" ? value : "");
const optionalText = (value: unknown) =>
  typeof value === "string" && value ? value : null;

function parseNotification(item: unknown): NotificationItem | null {
  if (typeof item !== "object" || item === null) return null;
  const b = item as Record<string, unknown>;
  const id = text(b.id);
  if (!id) return null;

  return {
    id,
    tipo: text(b.tipo),
    titulo: text(b.titulo),
    mensagem: text(b.mensagem),
    anuncioId: optionalText(b.anuncioId),
    propostaId: optionalText(b.propostaId),
    lida: b.lida === true,
    criadoEm: text(b.criadoEm),
  };
}

async function request(path: string): Promise<ApiResult> {
  let result: ApiResult;
  try {
    result = await apiFetch(path, { auth: true });
  } catch {
    throw new Error("Não foi possível conectar ao servidor.");
  }

  if (result.status === 401 || result.status === 403) redirect("/api/logout");
  return result;
}

/** GET /usuarios/me/notificacoes: as 50 mais recentes, lidas e não lidas. */
export const getNotificacoes = cache(async (): Promise<NotificationItem[]> => {
  const result = await request(`${ENDPOINTS.notifications}?tamanho=50`);

  if (!result.ok) {
    throw new Error(
      `Falha ao carregar as notificações (status ${result.status}).`
    );
  }

  const itens = (result.data as { itens?: unknown[] } | null)?.itens;
  return (Array.isArray(itens) ? itens : [])
    .map(parseNotification)
    .filter((item): item is NotificationItem => item !== null);
});

/**
 * GET /usuarios/me/notificacoes/nao-lidas. Devolve 0 se der erro: o contador
 * do sino não deve derrubar a página.
 */
export const getNaoLidas = cache(async (): Promise<number> => {
  try {
    const result = await request(ENDPOINTS.unreadNotifications);
    const total = (result.data as { naoLidas?: unknown } | null)?.naoLidas;
    return result.ok && typeof total === "number" ? total : 0;
  } catch {
    return 0;
  }
});
