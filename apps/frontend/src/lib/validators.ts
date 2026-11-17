export const onlyDigits = (value: string) => value.replace(/\D/g, "");

export function toIsoDate(value: string) {
  const match = /^(\d{2})\/(\d{2})\/(\d{4})$/.exec(value);
  return match ? `${match[3]}-${match[2]}-${match[1]}` : value;
}

export function isPastDate(value: string) {
  if (!/^\d{4}-\d{2}-\d{2}$/.test(value)) return false;

  const date = new Date(`${value}T00:00:00Z`);
  const time = date.getTime();

  return (
    !Number.isNaN(time) &&
    date.toISOString().slice(0, 10) === value &&
    time < Date.now()
  );
}