import type { CategoryName } from "@/lib/categories";

export type UserProfile = {
  id: number;
  name: string;
  email: string;
  phone: string;
  cpf: string;
  birthDate: string;
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
  createdAt: string;
};

