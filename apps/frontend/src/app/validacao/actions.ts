"use server";

import { revalidatePath } from "next/cache";
import { redirect } from "next/navigation";
import { apiUpload, type ApiResult } from "@/lib/api";
import { extractErrorMessage } from "@/lib/api-contract";
import { clearSessionToken } from "@/lib/session";
import { VALIDATION_ENDPOINTS } from "@/lib/validation";

export type ValidationFormState = { error: string } | { success: true } | null;

const MAX_BYTES = 5 * 1024 * 1024;
const ACCEPTED = ["image/jpeg", "image/png", "image/webp"];

function checkFile(value: FormDataEntryValue | null, label: string): File | string {
  if (!(value instanceof File) || value.size === 0) return `Envie ${label}.`;
  if (value.size > MAX_BYTES) return `${label[0].toUpperCase()}${label.slice(1)} passa de 5 MB. Tire outra foto ou reduza a imagem.`;
  if (value.type && !ACCEPTED.includes(value.type)) return `Envie ${label} em JPG, PNG ou WEBP.`;
  return value;
}

/** POST /usuarios/me/validacao (multipart: documento + selfie). */
export async function sendValidationDocuments(
  _previous: ValidationFormState,
  formData: FormData
): Promise<ValidationFormState> {
  const documento = checkFile(formData.get("documento"), "a foto do documento");
  if (typeof documento === "string") return { error: documento };
  const selfie = checkFile(formData.get("selfie"), "a selfie com o documento");
  if (typeof selfie === "string") return { error: selfie };

  const form = new FormData();
  form.append("documento", documento, documento.name || "documento");
  form.append("selfie", selfie, selfie.name || "selfie");

  let result: ApiResult;
  try {
    result = await apiUpload(VALIDATION_ENDPOINTS.mine, form);
  } catch {
    return { error: "Não foi possível conectar ao servidor. Tente novamente." };
  }

  if (result.status === 401) {
    await clearSessionToken();
    redirect("/login");
  }
  if (!result.ok) {
    return {
      error:
        extractErrorMessage(result.data) ??
        (result.status === 413
          ? "As imagens são grandes demais (máximo 5 MB cada)."
          : "Algo deu errado no servidor. Tente novamente em instantes."),
    };
  }

  revalidatePath("/validacao");
  revalidatePath("/perfil");
  revalidatePath("/menu");
  return { success: true };
}
