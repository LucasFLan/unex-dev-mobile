import { cookies } from "next/headers";
import { SESSION_COOKIE } from "@/lib/api-contract";

const BASE_URL = process.env.API_URL ?? "http://localhost:8080";

export type ApiResult<T = unknown> = {
  ok: boolean;
  status: number;
  data: T | null;
};

type ApiOptions = {
  method?: "GET" | "POST" | "PUT" | "PATCH" | "DELETE";
  body?: unknown;
  auth?: boolean;
};

export async function apiFetch<T = unknown>(
  path: string,
  { method = "GET", body, auth = false }: ApiOptions = {}
): Promise<ApiResult<T>> {
  const headers: Record<string, string> = { Accept: "application/json" };

  if (body !== undefined) headers["Content-Type"] = "application/json";

  if (auth) {
    const token = (await cookies()).get(SESSION_COOKIE)?.value;
    if (token) headers.Authorization = `Bearer ${token}`;
  }

  const response = await fetch(`${BASE_URL}${path}`, {
    method,
    headers,
    body: body === undefined ? undefined : JSON.stringify(body),
    cache: "no-store",
  });

  let data: T | null = null;
  const text = await response.text();

  if (text) {
    try {
      data = JSON.parse(text) as T;
    } catch {
      data = null;
    }
  }

  return { ok: response.ok, status: response.status, data };
}

/**
 * Envia um formulário multipart (ex.: imagens) para a API com o token do usuário logado.
 * O Content-Type com o boundary é definido pelo próprio fetch.
 */
export async function apiUpload<T = unknown>(
  path: string,
  form: FormData,
  method: "POST" | "PUT" = "POST"
): Promise<ApiResult<T>> {
  const headers: Record<string, string> = { Accept: "application/json" };
  const token = (await cookies()).get(SESSION_COOKIE)?.value;
  if (token) headers.Authorization = `Bearer ${token}`;

  const response = await fetch(`${BASE_URL}${path}`, {
    method,
    headers,
    body: form,
    cache: "no-store",
  });

  let data: T | null = null;
  const text = await response.text();
  if (text) {
    try {
      data = JSON.parse(text) as T;
    } catch {
      data = null;
    }
  }

  return { ok: response.ok, status: response.status, data };
}

/** Busca um arquivo binário da API com o token do usuário logado (ex.: imagem para o admin). */
export async function apiFetchBinary(path: string): Promise<Response> {
  const token = (await cookies()).get(SESSION_COOKIE)?.value;
  return fetch(`${BASE_URL}${path}`, {
    headers: token ? { Authorization: `Bearer ${token}` } : {},
    cache: "no-store",
  });
}
