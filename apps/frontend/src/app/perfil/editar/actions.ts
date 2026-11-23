"use server";

import { revalidatePath } from "next/cache";
import { redirect } from "next/navigation";
import { apiFetch, apiUpload, type ApiResult } from "@/lib/api";
import { ACCEPTED_PHOTO_TYPES, MAX_PHOTO_BYTES, photoSrc } from "@/lib/photos";
import { ENDPOINTS, FIELDS, extractErrorMessage } from "@/lib/api-contract";
import { clearSessionToken } from "@/lib/session";
import { isPastDate, onlyDigits, toIsoDate } from "@/lib/validators";

export type ProfileFormState = { error: string } | null;

const field = (formData: FormData, key: string) =>
  String(formData.get(key) ?? "").trim();

function messageFor(result: ApiResult) {
  const serverMessage = extractErrorMessage(result.data);

  switch (result.status) {
    case 400:
    case 422:
      return serverMessage ?? "Verifique os dados informados.";
    case 403:
      return "Você não tem permissão para alterar este perfil.";
    case 404:
      return "Usuário não encontrado.";
    case 409:
      return "Já existe uma conta com este e-mail ou CPF.";
    default:
      return "Algo deu errado no servidor. Tente novamente em instantes.";
  }
}

export async function updateProfile(
  _previous: ProfileFormState,
  formData: FormData
): Promise<ProfileFormState> {
  const nome = field(formData, "nome");
  const dataNascimento = toIsoDate(field(formData, "dataNascimento"));
  const telefone = onlyDigits(field(formData, "telefone"));

  if (!nome || !dataNascimento || !telefone) {
    return { error: "Preencha todos os campos." };
  }
  if (telefone.length < 10 || telefone.length > 11) {
    return { error: "Telefone inválido. Informe o DDD e o número." };
  }
  if (!isPastDate(dataNascimento)) {
    return { error: "Data de nascimento inválida." };
  }

  let result: ApiResult;
  try {
    result = await apiFetch(ENDPOINTS.updateUser, {
      method: "PUT",
      auth: true,
      body: {
        [FIELDS.name]: nome,
        // PUT /usuarios/me só aceita estes três campos (CPF e e-mail não mudam).
        [FIELDS.birthDate]: dataNascimento,
        [FIELDS.phone]: telefone,
      },
    });
  } catch {
    return {
      error: "Não foi possível conectar ao servidor. Tente novamente.",
    };
  }

  if (result.status === 401) {
    await clearSessionToken();
    redirect("/login");
  }

  if (!result.ok) return { error: messageFor(result) };

  revalidatePath("/perfil");
  revalidatePath("/menu");
  revalidatePath("/home");
  redirect("/perfil");
}
export type ProfilePhotoResult =
  | { ok: true; photo: string | null }
  | { ok: false; error: string };

const PROFILE_PHOTO = "/usuarios/me/foto";

async function finishPhotoRequest(result: ApiResult): Promise<ProfilePhotoResult> {
  if (result.status === 401) {
    await clearSessionToken();
    redirect("/login");
  }
  if (!result.ok) {
    return {
      ok: false,
      error:
        extractErrorMessage(result.data) ??
        (result.status === 413
          ? "A foto é grande demais (máximo 5 MB)."
          : "Algo deu errado no servidor. Tente novamente em instantes."),
    };
  }

  revalidatePath("/perfil");
  revalidatePath("/menu");
  revalidatePath("/home");
  const body = (result.data ?? {}) as Record<string, unknown>;
  return { ok: true, photo: photoSrc(body.fotoPerfil) };
}

/** PUT /usuarios/me/foto (multipart: foto). A foto chega já reduzida pelo navegador. */
export async function uploadProfilePhoto(formData: FormData): Promise<ProfilePhotoResult> {
  const foto = formData.get("foto");
  if (!(foto instanceof File) || foto.size === 0) return { ok: false, error: "Escolha uma foto." };
  if (foto.size > MAX_PHOTO_BYTES) return { ok: false, error: "A foto passa de 5 MB." };
  if (foto.type && !ACCEPTED_PHOTO_TYPES.includes(foto.type)) {
    return { ok: false, error: "Envie a foto em JPG, PNG ou WEBP." };
  }

  const form = new FormData();
  form.append("foto", foto, foto.name || "foto.jpg");
  try {
    return await finishPhotoRequest(await apiUpload(PROFILE_PHOTO, form, "PUT"));
  } catch {
    return { ok: false, error: "Não foi possível conectar ao servidor. Tente novamente." };
  }
}

/** DELETE /usuarios/me/foto. */
export async function removeProfilePhoto(): Promise<ProfilePhotoResult> {
  try {
    return await finishPhotoRequest(await apiFetch(PROFILE_PHOTO, { method: "DELETE", auth: true }));
  } catch {
    return { ok: false, error: "Não foi possível conectar ao servidor. Tente novamente." };
  }
}
