"use server";

import { revalidatePath } from "next/cache";
import { redirect } from "next/navigation";
import { apiFetch, apiUpload, type ApiResult } from "@/lib/api";
import { ENDPOINTS, extractErrorMessage } from "@/lib/api-contract";
import { clearSessionToken } from "@/lib/session";

export type PhotoResult = { version: string | null } | { error: string };

const MAX_BYTES = 5 * 1024 * 1024;

function versionFrom(data: unknown): string | null {
  const value = (data as Record<string, unknown> | null)?.fotoAtualizadaEm;
  return typeof value === "string" && value ? value : null;
}

async function finish(result: ApiResult): Promise<PhotoResult> {
  if (result.status === 401) {
    await clearSessionToken();
    redirect("/login");
  }
  if (!result.ok) {
    return {
      error:
        result.status === 400
          ? (extractErrorMessage(result.data) ?? "Não foi possível usar esta foto.")
          : "Algo deu errado no servidor. Tente novamente em instantes.",
    };
  }
  // A foto aparece no perfil, no menu e nos anúncios do usuário.
  revalidatePath("/perfil");
  revalidatePath("/menu");
  revalidatePath("/home");
  return { version: versionFrom(result.data) };
}

/** PUT /usuarios/me/foto (multipart, campo `foto`). */
export async function updateProfilePhoto(formData: FormData): Promise<PhotoResult> {
  const foto = formData.get("foto");
  if (!(foto instanceof File) || foto.size === 0) return { error: "Escolha uma foto." };
  if (foto.size > MAX_BYTES) return { error: "A foto pode ter no máximo 5 MB." };

  const form = new FormData();
  form.set("foto", foto, foto.name || "perfil.jpg");

  try {
    return await finish(await apiUpload(ENDPOINTS.myPhoto, form, "PUT"));
  } catch (error) {
    if (isRedirect(error)) throw error;
    return { error: "Não foi possível conectar ao servidor. Tente novamente." };
  }
}

/** DELETE /usuarios/me/foto. */
export async function removeProfilePhoto(): Promise<PhotoResult> {
  try {
    return await finish(await apiFetch(ENDPOINTS.myPhoto, { method: "DELETE", auth: true }));
  } catch (error) {
    if (isRedirect(error)) throw error;
    return { error: "Não foi possível conectar ao servidor. Tente novamente." };
  }
}

/** O redirect() do Next é uma exceção: precisa seguir adiante, não virar mensagem de erro. */
function isRedirect(error: unknown) {
  return typeof error === "object" && error !== null && "digest" in error
    && String((error as { digest: unknown }).digest).startsWith("NEXT_REDIRECT");
}
