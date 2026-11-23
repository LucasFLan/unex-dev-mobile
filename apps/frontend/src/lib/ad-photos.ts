/** Limites das fotos do anúncio (os mesmos da API). */
export const MAX_AD_PHOTOS = 6;
export const MAX_AD_PHOTO_BYTES = 5 * 1024 * 1024;
export const ACCEPTED_AD_PHOTO_TYPES = ["image/jpeg", "image/png", "image/webp"];

/** Parâmetro da edição que avisa que o anúncio foi criado sem as fotos (ver createAd). */
export const PHOTO_NOTICE_PARAM = "fotos-pendentes";

/**
 * Endereço de uma foto do anúncio. Passa pela rota /api/anuncios/[id]/fotos/[fotoId] do Next, que repassa o
 * token (o autor vê as fotos do próprio anúncio pausado).
 */
export const adPhotoUrl = (adId: string, photoId: string) =>
  `/api/anuncios/${encodeURIComponent(adId)}/fotos/${encodeURIComponent(photoId)}`;
