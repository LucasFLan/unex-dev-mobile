import {
  Droplet,
  Hammer,
  KeyRound,
  Layers,
  LayoutGrid,
  Leaf,
  type LucideIcon,
  Paintbrush,
  PanelsTopLeft,
  Snowflake,
  Sparkles,
  Wrench,
  Zap,
} from "lucide-react";

export type CategoryMeta = { icon: LucideIcon; bg: string; text: string };

/**
 * Visual de cada categoria, pela `slug` devolvida em GET /categorias.
 * Categorias novas no back caem no visual padrão (FALLBACK_META).
 */
export const CATEGORY_META: Record<string, CategoryMeta> = {
  eletricista: { icon: Zap, bg: "bg-amber-100", text: "text-amber-500" },
  encanador: { icon: Droplet, bg: "bg-blue-100", text: "text-blue-500" },
  pedreiro: { icon: Hammer, bg: "bg-orange-100", text: "text-orange-500" },
  pintor: { icon: Paintbrush, bg: "bg-pink-100", text: "text-pink-500" },
  marceneiro: { icon: Hammer, bg: "bg-yellow-100", text: "text-yellow-600" },
  serralheiro: { icon: Wrench, bg: "bg-zinc-100", text: "text-zinc-500" },
  gesseiro: { icon: Layers, bg: "bg-stone-100", text: "text-stone-500" },
  vidraceiro: { icon: PanelsTopLeft, bg: "bg-cyan-100", text: "text-cyan-500" },
  chaveiro: { icon: KeyRound, bg: "bg-lime-100", text: "text-lime-600" },
  "montador-de-moveis": { icon: Wrench, bg: "bg-teal-100", text: "text-teal-500" },
  "ar-condicionado": { icon: Snowflake, bg: "bg-sky-100", text: "text-sky-500" },
  limpeza: { icon: Sparkles, bg: "bg-violet-100", text: "text-violet-500" },
  jardinagem: { icon: Leaf, bg: "bg-green-100", text: "text-green-600" },
};

const FALLBACK_META: CategoryMeta = {
  icon: LayoutGrid,
  bg: "bg-slate-100",
  text: "text-slate-500",
};

export const getCategoryMeta = (slug: string): CategoryMeta =>
  CATEGORY_META[slug] ?? FALLBACK_META;
