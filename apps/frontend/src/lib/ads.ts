import { cache } from "react";
import { apiFetch } from "@/lib/api";
import { ENDPOINTS, AD_FIELDS, FIELDS } from "@/lib/api-contract";

export type AnuncioItem = {
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
  atualizadoEm?: string;
};

type AnunciosPageResponse = {
  itens: AnuncioItem[];
  pagina: number;
  tamanho: number;
  totalItens: number;
  totalPaginas: number;
};

const text = (value: unknown) => (typeof value === "string" ? value : "");
const num = (value: unknown) => (typeof value === "number" ? value : 0);

function parseAnuncio(item: unknown): AnuncioItem | null {
  if (typeof item !== "object" || item === null) return null;
  const b = item as Record<string, unknown>;

  return {
    id: text(b.id),
    tipo: (text(b.tipo) === "PEDIDO" ? "PEDIDO" : "OFERTA") as "OFERTA" | "PEDIDO",
    titulo: text(b[AD_FIELDS.title]),
    descricao: text(b[AD_FIELDS.description]),
    preco: num(b[AD_FIELDS.price]),
    categoria: {
      id: text((b.categoria as Record<string, unknown>)?.id),
      slug: text((b.categoria as Record<string, unknown>)?.slug),
      nome: text((b.categoria as Record<string, unknown>)?.nome),
    },
    cidade: text(b[AD_FIELDS.city]),
    uf: text(b[AD_FIELDS.state]),
    ativo: Boolean(b.ativo),
    autorId: text(b.autorId),
    criadoEm: text(b.criadoEm),
    atualizadoEm: text(b.atualizadoEm),
  };
}

export const getAnuncios = cache(async (): Promise<AnuncioItem[]> => {
  let result;

  try {
    // Utilizando a rota pública mapeada no seu api-contract
    result = await apiFetch(ENDPOINTS.ads, { method: "GET" });
  } catch {
    throw new Error("Não foi possível conectar ao servidor.");
  }

  if (!result.ok) {
    throw new Error(`Falha ao carregar os anúncios (status ${result.status}).`);
  }

  // A API Java retorna uma página paginada com a propriedade "itens"
  const data = result.data as AnunciosPageResponse;
  const rawList = Array.isArray(data?.itens) ? data.itens : [];

  return rawList
    .map(parseAnuncio)
    .filter((item): item is AnuncioItem => item !== null);
});