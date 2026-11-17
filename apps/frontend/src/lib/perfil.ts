import type { CategoryName } from "@/lib/categories";

export type UserProfile = {
  id: number;
  name: string;
  email: string;
  phone: string;
  neighborhood: string;
  bio: string;
  verified: boolean;
  memberSince: string;
  rating: number;
  reviewsCount: number;
  completedServices: number;
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

export const formatShortDate = (isoDate: string) =>
  shortDateFormatter.format(new Date(isoDate));