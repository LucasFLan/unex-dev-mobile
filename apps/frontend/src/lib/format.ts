export const formatPrice = (value: number) =>
  value.toLocaleString("pt-BR", {
    style: "currency",
    currency: "BRL",
    maximumFractionDigits: 0,
  });

export const normalize = (text: string) =>
  text
    .normalize("NFD")
    .replace(/\p{Diacritic}/gu, "")
    .toLowerCase();

const monthFormatter = new Intl.DateTimeFormat("pt-BR", {
  month: "long",
  year: "numeric",
  timeZone: "UTC",
});

export const formatMonth = (isoDate: string) =>
  monthFormatter.format(new Date(isoDate));

export const formatDistance = (km: number) =>
  `${km.toLocaleString("pt-BR")} km`;

export const formatRating = (rating: number) =>
  rating.toLocaleString("pt-BR", {
    minimumFractionDigits: 1,
    maximumFractionDigits: 1,
  });

const relativeFormatter = new Intl.RelativeTimeFormat("pt-BR", {
  numeric: "auto",
});


export function formatRelativeTime(isoDate: string, now = Date.now()) {
  const minutes = Math.round((new Date(isoDate).getTime() - now) / 60_000);

  if (Math.abs(minutes) < 1) return "agora";
  if (Math.abs(minutes) < 60) return relativeFormatter.format(minutes, "minute");

  const hours = Math.round(minutes / 60);
  if (Math.abs(hours) < 24) return relativeFormatter.format(hours, "hour");

  return relativeFormatter.format(Math.round(hours / 24), "day");
}