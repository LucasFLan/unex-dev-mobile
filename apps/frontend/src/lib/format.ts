/** `null` = preço "a combinar" (anúncio sem valor definido). */
export const formatPrice = (value: number | null) =>
  value === null
    ? "A combinar"
    : value.toLocaleString("pt-BR", {
        style: "currency",
        currency: "BRL",
        maximumFractionDigits: Number.isInteger(value) ? 0 : 2,
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