import { cacheLife } from "next/cache";

/**
 * Ano atual. Com Cache Components, `new Date()` direto no render quebra o
 * prerender; aqui o valor fica em cache e é recalculado no máximo uma vez por dia.
 */
export async function CurrentYear() {
  "use cache";
  cacheLife("days");

  return <>{new Date().getFullYear()}</>;
}
