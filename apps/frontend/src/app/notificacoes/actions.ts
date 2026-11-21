"use server";

import { revalidatePath } from "next/cache";
import { apiFetch } from "@/lib/api";
import { ENDPOINTS } from "@/lib/api-contract";

/** PATCH /usuarios/me/notificacoes/lidas. Chamado ao abrir a tela de notificações. */
export async function markAllNotificationsRead(): Promise<boolean> {
  try {
    const result = await apiFetch(ENDPOINTS.readAllNotifications, {
      method: "PATCH",
      auth: true,
    });
    if (!result.ok) return false;
  } catch {
    return false;
  }

  revalidatePath("/home");
  return true;
}
