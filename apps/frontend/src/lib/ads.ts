import { cache } from "react";
import { redirect } from "next/navigation";
import { apiFetch, type ApiResult } from "@/lib/api";
import { ENDPOINTS, AD_FIELDS } from "@/lib/api-contract";

export type AdType = "OFERTA" | "PEDIDO";

export type AnuncioItem = {
  id: string;
  tipo: AdType;
  titulo: string;
  descricao: string;
  /** `null` = "a combinar". */
  preco: number | null;
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
  atualizadoEm: string;
};

type AnunciosPageResponse = {
  itens?: unknown[];
  pagina?: number;
  totalPaginas?: number;
};

/** Maior tamanho de página aceito pela API. */
const MAX_PAGE_SIZE = 50;
/** Limite de páginas lidas em "meus anúncios", para não ficar em loop. */
const MAX_PAGES = 20;

const text = (value: unknown) => (typeof value === "string" ? value : "");

function price(value: unknown): number | null {
  if (typeof value === "number") return value;
  if (typeof value === "string" && value.trim() !== "") {
    const parsed = Number(value);
    return Number.isFinite(parsed) ? parsed : null;
  }
  return null;
}

export function parseAnuncio(item: unknown): AnuncioItem | null {
  if (typeof item !== "object" || item === null) return null;
  const b = item as Record<string, unknown>;
  const id = text(b.id);
  if (!id) return null;

  const categoria =
    typeof b.categoria === "object" && b.categoria !== null
      ? (b.categoria as Record<string, unknown>)
      : {};

  return {
    id,
    tipo: text(b[AD_FIELDS.type]) === "PEDIDO" ? "PEDIDO" : "OFERTA",
    titulo: text(b[AD_FIELDS.title]),
    descricao: text(b[AD_FIELDS.description]),
    preco: price(b[AD_FIELDS.price]),
    categoria: {
      id: text(categoria.id),
      slug: text(categoria.slug),
      nome: text(categoria.nome),
    },
    cidade: text(b[AD_FIELDS.city]),
    uf: text(b[AD_FIELDS.state]),
    ativo: Boolean(b.ativo),
    autorId: text(b.autorId),
    criadoEm: text(b.criadoEm),
    atualizadoEm: text(b.atualizadoEm),
  };
}

function parsePage(data: unknown) {
  const page = (data ?? {}) as AnunciosPageResponse;
  const itens = Array.isArray(page.itens) ? page.itens : [];

  return {
    itens: itens
      .map(parseAnuncio)
      .filter((item): item is AnuncioItem => item !== null),
    pagina: typeof page.pagina === "number" ? page.pagina : 0,
    totalPaginas: typeof page.totalPaginas === "number" ? page.totalPaginas : 1,
  };
}

async function request(path: string, auth: boolean): Promise<ApiResult> {
  try {
    return await apiFetch(path, { auth });
  } catch {
    throw new Error("Não foi possível conectar ao servidor.");
  }
}

/** GET /anuncios (público): anúncios ativos, do mais recente ao mais antigo. */
export const getAnuncios = cache(async (): Promise<AnuncioItem[]> => {
  const result = await request(
    `${ENDPOINTS.ads}?tamanho=${MAX_PAGE_SIZE}`,
    false
  );

  if (!result.ok) {
    throw new Error(`Falha ao carregar os anúncios (status ${result.status}).`);
  }

  return parsePage(result.data).itens;
});

/**
 * GET /anuncios/{id}. Envia o token (se houver) para o autor conseguir ver o
 * próprio anúncio inativo. Devolve `null` quando o anúncio não existe.
 */
export const getAnuncio = cache(
  async (id: string): Promise<AnuncioItem | null> => {
    const result = await request(
      `${ENDPOINTS.ads}/${encodeURIComponent(id)}`,
      true
    );

    if (result.status === 401) redirect("/api/logout");
    if (result.status === 404 || result.status === 400) return null;
    if (!result.ok) {
      throw new Error(`Falha ao carregar o anúncio (status ${result.status}).`);
    }

    return parseAnuncio(result.data);
  }
);

/** GET /usuarios/me/anuncios: todos os anúncios do usuário logado (ativos e inativos). */
export const getMeusAnuncios = cache(async (): Promise<AnuncioItem[]> => {
  const all: AnuncioItem[] = [];

  for (let pagina = 0; pagina < MAX_PAGES; pagina++) {
    const result = await request(
      `${ENDPOINTS.myAds}?pagina=${pagina}&tamanho=${MAX_PAGE_SIZE}`,
      true
    );

    if (result.status === 401 || result.status === 403) {
      redirect("/api/logout");
    }
    if (!result.ok) {
      throw new Error(
        `Falha ao carregar seus anúncios (status ${result.status}).`
      );
    }

    const page = parsePage(result.data);
    all.push(...page.itens);

    if (pagina + 1 >= page.totalPaginas || page.itens.length === 0) break;
  }

  return all;
});
