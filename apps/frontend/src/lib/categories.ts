import {
  Droplet,
  Hammer,
  Paintbrush,
  Sparkles,
  Wrench,
  Zap,
  type LucideIcon,
} from "lucide-react";

export const CATEGORY_META = {
  Eletricista: { icon: Zap, bg: "bg-amber-100", text: "text-amber-500" },
  Encanador: { icon: Droplet, bg: "bg-blue-100", text: "text-blue-500" },
  Diarista: { icon: Sparkles, bg: "bg-violet-100", text: "text-violet-500" },
  Pedreiro: { icon: Hammer, bg: "bg-orange-100", text: "text-orange-500" },
  Montador: { icon: Wrench, bg: "bg-teal-100", text: "text-teal-500" },
  Pintor: { icon: Paintbrush, bg: "bg-pink-100", text: "text-pink-500" },
} satisfies Record<string, { icon: LucideIcon; bg: string; text: string }>;

export type CategoryName = keyof typeof CATEGORY_META;

export const CATEGORY_NAMES = Object.keys(CATEGORY_META) as CategoryName[];

export const isCategoryName = (value: string | null): value is CategoryName =>
  value !== null && (CATEGORY_NAMES as string[]).includes(value);