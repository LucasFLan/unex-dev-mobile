import type { AnuncioItem } from "@/lib/ads";
import { formatPrice, formatShortDate } from "@/lib/format";
import { withFrom } from "@/lib/navigation";

export const SERVICE_TYPES = ["Procuro", "Ofereço"] as const;
export const SERVICE_STATUSES = ["Ativo", "Pausado"] as const;

export type ServiceType = (typeof SERVICE_TYPES)[number];
export type ServiceStatus = (typeof SERVICE_STATUSES)[number];

/** Item do histórico: um anúncio do usuário logado. */
export type Service = {
  id: string;
  /** Data ISO de criação. */
  date: string;
  type: ServiceType;
  status: ServiceStatus;
  title: string;
  description: string;
  price: string;
  categorySlug: string;
  /** Página de detalhe do anúncio. */
  href: string;
};

export function toService(ad: AnuncioItem): Service {
  const isOffer = ad.tipo === "OFERTA";

  return {
    id: ad.id,
    date: ad.criadoEm,
    type: isOffer ? "Ofereço" : "Procuro",
    status: ad.ativo ? "Ativo" : "Pausado",
    title: ad.titulo,
    description: `Publicado em ${formatShortDate(ad.criadoEm)} · ${ad.categoria.nome} · ${ad.cidade}/${ad.uf}`,
    price: formatPrice(ad.preco),
    categorySlug: ad.categoria.slug,
    href: withFrom(
      isOffer ? `/profissionais/${ad.id}` : `/servicos/${ad.id}`,
      "/historico"
    ),
  };
}
