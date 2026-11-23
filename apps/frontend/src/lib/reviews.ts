import { cache } from "react";
import { redirect } from "next/navigation";
import { apiFetch } from "@/lib/api";
import { ENDPOINTS } from "@/lib/api-contract";

export type Review = {
  id: string;
  nota: number;
  comentario: string | null;
  servicoTitulo: string;
  servicoCategoria: string | null;
  /** `null` = quem avaliou apagou a conta. */
  autor: { id: string; nome: string; fotoAtualizadaEm: string | null } | null;
  criadoEm: string;
};

export type UserReviews = {
  userId: string;
  nome: string;
  fotoAtualizadaEm: string | null;
  /** `null` = ainda sem avaliações. */
  media: number | null;
  total: number;
  itens: Review[];
};

const text = (value: unknown) => (typeof value === "string" ? value : "");
const record = (value: unknown) =>
  typeof value === "object" && value !== null ? (value as Record<string, unknown>) : null;

function parseReview(value: unknown): Review | null {
  const r = record(value);
  if (!r || !text(r.id) || typeof r.nota !== "number") return null;
  const autor = record(r.autor);
  return {
    id: text(r.id),
    nota: r.nota,
    comentario: text(r.comentario) || null,
    servicoTitulo: text(r.servicoTitulo),
    servicoCategoria: text(r.servicoCategoria) || null,
    autor: autor && text(autor.id)
      ? { id: text(autor.id), nome: text(autor.nome), fotoAtualizadaEm: text(autor.fotoAtualizadaEm) || null }
      : null,
    criadoEm: text(r.criadoEm),
  };
}

/** GET /usuarios/{id}/avaliacoes: média, total e as avaliações mais recentes. `null` se o usuário não existe. */
export const getUserReviews = cache(async (userId: string): Promise<UserReviews | null> => {
  let result;
  try {
    result = await apiFetch(`${ENDPOINTS.userReviews(userId)}?tamanho=50`, { auth: true });
  } catch {
    throw new Error("Não foi possível conectar ao servidor.");
  }
  if (result.status === 401) redirect("/api/logout");
  if (result.status === 404 || result.status === 400) return null;
  if (!result.ok) throw new Error(`Falha ao carregar as avaliações (status ${result.status}).`);

  const b = record(result.data) ?? {};
  return {
    userId: text(b.usuarioId),
    nome: text(b.nome),
    fotoAtualizadaEm: text(b.fotoAtualizadaEm) || null,
    media: typeof b.media === "number" ? b.media : null,
    total: typeof b.total === "number" ? b.total : 0,
    itens: (Array.isArray(b.itens) ? b.itens : []).map(parseReview).filter((r): r is Review => r !== null),
  };
});

export { formatRating } from "@/lib/rating";
