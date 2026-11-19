import { cache } from "react";
import { apiFetch } from "@/lib/api";
import { ENDPOINTS } from "@/lib/api-contract";

export type Category = {
  id: string;
  slug: string;
  nome: string;
};

function isCategory(value: unknown): value is Category {
  if (typeof value !== "object" || value === null) return false;

  const item = value as Record<string, unknown>;
  return (
    typeof item.id === "string" &&
    typeof item.slug === "string" &&
    typeof item.nome === "string"
  );
}

export const getCategories = cache(async (): Promise<Category[]> => {
  let result;

  try {
    result = await apiFetch(ENDPOINTS.categories);
  } catch {
    throw new Error("Não foi possível conectar ao servidor.");
  }

  if (!result.ok || !Array.isArray(result.data)) {
    throw new Error(`Falha ao carregar as categorias (status ${result.status}).`);
  }

  return result.data.filter(isCategory);
});