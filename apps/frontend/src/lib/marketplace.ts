import type { CategoryName } from "@/lib/categories";
import { normalize } from "@/lib/format";
import { Review } from "./perfil";

export type Professional = {
  id: string;
  category: string;
  name: string;
  service: string;
  location: string;
  price: number;
  rating: number;
  reviews: number;
  distance: number;
  createdAt: string;
};

export type AnuncioMarketplace = {
  id: string; 
  tipo: "OFERTA" | "PEDIDO";
  titulo: string;
  descricao: string;
  preco: number;
  categoria: {
    id: string;
    slug: string;
    nome: string;
  };
  cidade: string;
  uf: string;
  ativo: boolean;
  autorId: string;
  criadoEm: string;
};

export type ServiceRequest = {
  id: string;
  category: string;
  urgent: boolean;
  time: string;
  title: string;
  description: string;
  location: string;
  distance: number;
  price: number;
  createdAt: string;
};

export type RequestClient = {
  name: string;
  rating: number | null;
  reviewsCount: number;
  verified: boolean;
  memberSince: string;
  requestsCount: number;
};

export type ServiceRequestDetail = ServiceRequest & {
  client: RequestClient;
  proposalsCount: number;
  /** URLs das fotos enviadas pelo cliente (pode ser vazio) */
  photos: string[];
  /** Data ISO (AAAA-MM-DD); null = a combinar */
  preferredDate: string | null;
  /** Informações extras que o cliente listou. */
  requirements: string[];
};
export type ProfessionalService = {
  name: string;
  price: number;
  unit: string | null;
};

export type ProfessionalDetail = Professional & {
  description: string;
  priceUnit: string | null;
  serviceRadiusKm: number;
  verified: boolean;
  memberSince: string;
  completedServices: number;
  availability: string;
  services: ProfessionalService[];
  reviews: Review[];
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
      if (filters.maxPrice !== null && professional.price > filters.maxPrice) {
        return false;
      }
      return true;
    })
    .sort((a, b) => {
      switch (filters.sort) {
        case "menor-preco":
          return a.price - b.price;
        // case "melhor-avaliados":
        //   return b.rating - a.rating || b.reviewsCount - a.reviewsCount;
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