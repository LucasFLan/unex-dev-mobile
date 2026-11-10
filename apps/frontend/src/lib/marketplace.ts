import type { CategoryName } from "@/lib/categories";
import { normalize } from "@/lib/format";

export type Professional = {
  id: number;
  name: string;
  category: CategoryName;
  service: string;
  rating: number;
  reviewsCount: number;
  neighborhood: string;
  distanceKm: number;
  priceFrom: number;
  createdAt: string;
};

export type ServiceRequest = {
  id: number;
  category: CategoryName;
  urgent: boolean;
  createdAt: string;
  title: string;
  description: string;
  neighborhood: string;
  distanceKm: number;
  price: number;
};

export const SORT_OPTIONS = [
  { value: "recentes", label: "Mais recentes" },
  { value: "menor-preco", label: "Menor preço" },
  { value: "melhor-avaliados", label: "Melhor avaliados" },
] as const;

export type SortOption = (typeof SORT_OPTIONS)[number]["value"];

export const DEFAULT_SORT: SortOption = "recentes";

export const isSortOption = (value: string | null): value is SortOption =>
  SORT_OPTIONS.some((option) => option.value === value);

export type HomeFilters = {
  query: string;
  category: CategoryName | null;
  sort: SortOption;
  maxPrice: number | null;
};

const newestFirst = (a: { createdAt: string }, b: { createdAt: string }) =>
  b.createdAt.localeCompare(a.createdAt);

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
      if (filters.category && professional.category !== filters.category) {
        return false;
      }
      if (filters.maxPrice !== null && professional.priceFrom > filters.maxPrice) {
        return false;
      }
      return true;
    })
    .sort((a, b) => {
      switch (filters.sort) {
        case "menor-preco":
          return a.priceFrom - b.priceFrom;
        case "melhor-avaliados":
          return b.rating - a.rating || b.reviewsCount - a.reviewsCount;
        default:
          return newestFirst(a, b);
      }
    });
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
      if (filters.category && request.category !== filters.category) {
        return false;
      }
      if (filters.maxPrice !== null && request.price > filters.maxPrice) {
        return false;
      }
      return true;
    })
    .sort((a, b) =>
      filters.sort === "menor-preco" ? a.price - b.price : newestFirst(a, b)
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