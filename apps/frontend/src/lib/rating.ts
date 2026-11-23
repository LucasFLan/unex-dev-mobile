/** "4,8" com uma casa, no formato brasileiro (sem dependências de servidor: usado em componentes de cliente). */
export const formatRating = (media: number) => media.toFixed(1).replace(".", ",");
