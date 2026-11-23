import { normalize } from "@/lib/format";
import { adPhotoUrl } from "@/lib/ad-photos";
import type { AnuncioItem, GeoPoint } from "@/lib/ads";
import { distanceKm } from "@/lib/geo";

/** Reputação de quem publicou; `media` nula = sem avaliações. */
export type Rating = { media: number | null; total: number };

/** Campos de localização comuns aos cards. */
type Located = {
  /** Ponto aproximado do anúncio (~1 km); `null` sem CEP. */
  point: GeoPoint | null;
  /** Só OFERTA: até onde atende. */
  radiusKm: number | null;
  /** Calculada quando o usuário informou a localização. */
  distanceKm: number | null;
};

export type Professional = {
  id: string;
  /** Autor do anúncio: dono da foto de perfil que aparece no card. */
  authorId: string;
  /** Versão da foto do autor; `null` = sem foto (nem busca), ausente = não se sabe. */
  authorPhoto?: string | null;
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
  rating: Rating;
  /** Primeira foto do anúncio (capa do card), já com o endereço; `null` sem fotos. */
  cover: string | null;
} & Located;

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
} & Located;

export const SORT_OPTIONS = [
  { value: "recentes", label: "Mais recentes" },
  { value: "perto", label: "Mais perto" },
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

/** Mais perto primeiro; sem localização vai para o fim. */
const nearestFirst = (a: { distanceKm: number | null }, b: { distanceKm: number | null }) =>
  (a.distanceKm ?? Infinity) - (b.distanceKm ?? Infinity);

/** Calcula a distância de cada item até a origem (quando há origem e ponto). */
export function withDistance<T extends Located>(list: T[], origin: GeoPoint | null): T[] {
  return list.map((item) => ({
    ...item,
    distanceKm: origin && item.point ? distanceKm(origin, item.point) : null,
  }));
}

/** Oferta com raio só aparece em "Mais perto" se o usuário estiver dentro da área de atendimento. */
const servesHere = (item: Located) =>
  item.radiusKm === null || item.distanceKm === null || item.distanceKm <= item.radiusKm;

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
      if (filters.sort === "perto" && !servesHere(professional)) return false;
      return withinPrice(professional.price, filters.maxPrice);
    })
    .sort((a, b) =>
      filters.sort === "menor-preco"
        ? cheapestFirst(a, b)
        : filters.sort === "perto"
          ? nearestFirst(a, b)
          : newestFirst(a, b)
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
      filters.sort === "menor-preco"
        ? cheapestFirst(a, b)
        : filters.sort === "perto"
          ? nearestFirst(a, b)
          : newestFirst(a, b)
    );
}

/** Anúncio do tipo OFERTA no formato do card de profissional. */
export const toProfessional = (anuncio: AnuncioItem): Professional => ({
  id: anuncio.id,
  authorId: anuncio.autorId,
  authorPhoto: anuncio.autor ? anuncio.autor.fotoAtualizadaEm : undefined,
  category: anuncio.categoria.nome,
  categorySlug: anuncio.categoria.slug,
  name: anuncio.autor?.nome || anuncio.titulo,
  service: anuncio.descricao,
  location: anuncio.bairro ? `${anuncio.bairro}, ${anuncio.cidade}` : `${anuncio.cidade}, ${anuncio.uf}`,
  price: anuncio.preco,
  createdAt: anuncio.criadoEm,
  rating: { media: anuncio.autor?.media ?? null, total: anuncio.autor?.total ?? 0 },
  cover: anuncio.fotos[0] ? adPhotoUrl(anuncio.id, anuncio.fotos[0]) : null,
  point: anuncio.regiao,
  radiusKm: anuncio.raioAtendimentoKm,
  distanceKm: null,
});

/** Anúncio do tipo PEDIDO no formato do card de pedido. */
export const toRequest = (anuncio: AnuncioItem): ServiceRequest => ({
  id: anuncio.id,
  category: anuncio.categoria.nome,
  categorySlug: anuncio.categoria.slug,
  title: anuncio.titulo,
  description: anuncio.descricao,
  location: anuncio.bairro ? `${anuncio.bairro}, ${anuncio.cidade}` : `${anuncio.cidade}, ${anuncio.uf}`,
  price: anuncio.preco,
  createdAt: anuncio.criadoEm,
  point: anuncio.regiao,
  radiusKm: null,
  distanceKm: null,
});

export const initials = (name: string) =>
  name
    .split(/\s+/)
    .filter(Boolean)
    .slice(0, 2)
    .map((part) => part[0])
    .join("")
    .toUpperCase();