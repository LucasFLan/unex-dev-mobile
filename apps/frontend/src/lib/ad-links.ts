import { withFrom } from "@/lib/navigation";

/** Abre o formulário de criar anúncio preenchido com os dados de outro (do próprio usuário). */
export const duplicateAdHref = (adId: string, from: string) =>
  withFrom(`/anunciar?copiar=${encodeURIComponent(adId)}`, from);
