import { normalize } from "@/lib/format";

export type Professional = {
  id: string;
  /** Nome da categoria, para exibição. */
  category: string;
  /** Slug da categoria (GET /categorias), usada no filtro e no ícone. */
  categorySlug: string;
  name: string;
  service: string;
  location: string;
  /** `null` = "a combinar". */
  price: number | null;
  createdAt: string;
};

export type ServiceRequest = {
  id: string;
  category: string;
  categorySlug: string;
  title: string;
  description: string;
  location: string;
  /** `null` = "a combinar". */
  price: number | null;
  createdAt: string;
};

export const SORT_OPTIONS = [
  { value: "recentes", label: "Mais recentes" },
  { value: "menor-preco", label: "Menor preço" },
] as const;

export type SortOption = (typeof SORT_OPTIONS)[number]["value"];

export const DEFAULT_SORT: SortOption = "recentes";

export const isSortOption = (value: string | null): value is SortOption =>
  SORT_OPTIONS.some((option) => option.value === value);

export type HomeFilters = {
  query: string;
  /** Slug da categoria selecionada. */
  category: string | null;
  sort: SortOption;
  maxPrice: number | null;
};

const newestFirst = (a: { createdAt: string }, b: { createdAt: string }) =>
  b.createdAt.localeCompare(a.createdAt);

/** Menor preço primeiro; "a combinar" (null) vai para o fim. */
const cheapestFirst = (
  a: { price: number | null },
  b: { price: number | null }
) => (a.price ?? Infinity) - (b.price ?? Infinity);

/** Preço "a combinar" não é descartado pelo filtro de valor máximo. */
const withinPrice = (price: number | null, maxPrice: number | null) =>
  maxPrice === null || price === null || price <= maxPrice;

export function filterProfessionals(
  list: Professional[],
  filters: HomeFilters
): Professional[] {
  const query = normalize(filters.query.trim());

  return list
    .filter((professional) => {
      if (
        query &&
        !normalize(
          `${professional.name} ${professional.category} ${professional.service}`
        ).includes(query)
      ) {
        return false;
      }
      if (filters.category && professional.categorySlug !== filters.category) {
        return false;
      }
      return withinPrice(professional.price, filters.maxPrice);
    })
    .sort((a, b) =>
      filters.sort === "menor-preco" ? cheapestFirst(a, b) : newestFirst(a, b)
    );
}

export function filterRequests(
  list: ServiceRequest[],
  filters: HomeFilters
): ServiceRequest[] {
  const query = normalize(filters.query.trim());

  return list
    .filter((request) => {
      if (
        query &&
        !normalize(
          `${request.title} ${request.category} ${request.description}`
        ).includes(query)
      ) {
        return false;
      }
      if (filters.category && request.categorySlug !== filters.category) {
        return false;
      }
      return withinPrice(request.price, filters.maxPrice);
    })
    .sort((a, b) =>
      filters.sort === "menor-preco" ? cheapestFirst(a, b) : newestFirst(a, b)
    );
}

export const initials = (name: string) =>
  name
    .split(/\s+/)
    .filter(Boolean)
    .slice(0, 2)
    .map((part) => part[0])
    .join("")
    .toUpperCase();