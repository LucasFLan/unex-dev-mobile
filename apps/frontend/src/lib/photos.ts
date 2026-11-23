/** Fotos (perfil e anúncios): limites iguais aos da API. */
export const MAX_AD_PHOTOS = 6;
export const MAX_PHOTO_BYTES = 5 * 1024 * 1024;
export const ACCEPTED_PHOTO_TYPES = ["image/jpeg", "image/png", "image/webp"];

/** Parâmetro da edição do anúncio que avisa que ele foi criado sem as fotos (ver createAd). */
export const PHOTO_NOTICE_PARAM = "fotos-pendentes";

/** Prefixo do proxy do Next que repassa as fotos da API (ver app/api/fotos). */
const PROXY_PREFIX = "/api/fotos";

/**
 * Converte o caminho que a API devolve (ex.: "/usuarios/{id}/foto?v=123") no endereço que o navegador abre.
 * A API fica na rede interna; as imagens passam pelo servidor do Next.
 */
export function photoSrc(apiPath: unknown): string | null {
  if (typeof apiPath !== "string" || !apiPath.startsWith("/")) return null;
  return `${PROXY_PREFIX}${apiPath}`;
}

export type AdPhoto = {
  id: string;
  /** Endereço para o navegador (já passa pelo proxy). */
  src: string;
};
