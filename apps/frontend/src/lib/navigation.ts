/**
 * Navegação "para cima": o botão de voltar leva à tela-pai, não à última página do histórico.
 *
 * Telas que podem ser abertas de vários lugares recebem a origem em `?de=` (ex.: o detalhe de um anúncio
 * aberto a partir do Histórico). Só caminhos internos são aceitos, para o parâmetro não virar um
 * redirecionamento para fora do app.
 */

export const FROM_PARAM = "de";

/** Caminho interno seguro: começa com uma única "/" e não tem barra invertida. */
function isInternalPath(value: string) {
  return value.startsWith("/") && !value.startsWith("//") && !value.includes("\\");
}

/** Lê a origem de `?de=`; sem origem válida, usa a tela-pai padrão. */
export function backHrefFrom(
  from: string | string[] | undefined,
  fallback: string
): string {
  const value = Array.isArray(from) ? from[0] : from;
  return value && isInternalPath(value) ? value : fallback;
}

/** Monta um link que lembra de onde o usuário veio. */
export function withFrom(href: string, from: string): string {
  const separator = href.includes("?") ? "&" : "?";
  return `${href}${separator}${FROM_PARAM}=${encodeURIComponent(from)}`;
}

/** URL da tela atual (com a própria origem), para os links que saem dela. */
export function currentHref(path: string, from?: string | null): string {
  return from ? withFrom(path, from) : path;
}
