import type { CategoryName } from "@/lib/categories";

/* ---------- Tipos (espelham o que a API deve devolver) ---------- */

export type UserProfile = {
  id: number;
  name: string;
  email: string;
  /** Apenas dígitos, com DDD (ex.: "75999990000"). A máscara é aplicada no front. */
  phone: string;
  neighborhood: string;
  bio: string;
  verified: boolean;
  /** Data ISO (AAAA-MM-DD) */
  memberSince: string;
  /** Média das avaliações recebidas */
  rating: number;
  reviewsCount: number;
  completedServices: number;
  /** Categorias em que o usuário oferece serviços (vazio = só contrata) */
  categories: CategoryName[];
};

export type Review = {
  id: number;
  authorName: string;
  rating: number;
  comment: string;
  category: CategoryName;
  /** ISO 8601 */
  createdAt: string;
};

/* ---------- Formatadores ---------- */

/** "75999990000" -> "(75) 99999-0000" */
export function formatPhone(phone: string) {
  const digits = phone.replace(/\D/g, "");

  if (digits.length === 11) {
    return `(${digits.slice(0, 2)}) ${digits.slice(2, 7)}-${digits.slice(7)}`;
  }
  if (digits.length === 10) {
    return `(${digits.slice(0, 2)}) ${digits.slice(2, 6)}-${digits.slice(6)}`;
  }
  return phone;
}

const shortDateFormatter = new Intl.DateTimeFormat("pt-BR", {
  day: "2-digit",
  month: "short",
  year: "numeric",
  timeZone: "UTC",
});

/** "2026-10-14T10:00:00Z" -> "14 de out. de 2026" */
export const formatShortDate = (isoDate: string) =>
  shortDateFormatter.format(new Date(isoDate));