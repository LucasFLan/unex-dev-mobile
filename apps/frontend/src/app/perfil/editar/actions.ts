"use server";

import { revalidatePath } from "next/cache";
import { redirect } from "next/navigation";
import { apiFetch, type ApiResult } from "@/lib/api";
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

  const email = field(formData, "email");
  const cpf = onlyDigits(field(formData, "cpf"));

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
        [FIELDS.birthDate]: dataNascimento,
        [FIELDS.cpf]: cpf,
        [FIELDS.email]: email,
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
  redirect("/perfil");
}