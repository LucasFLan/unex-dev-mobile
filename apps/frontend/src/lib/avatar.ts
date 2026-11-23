/**
 * Endereço da foto de perfil de um usuário. A imagem passa pela rota /api/usuarios/[id]/foto do Next, que
 * repassa o token (fica num cookie httpOnly) para a API.
 *
 * `version` é o `fotoAtualizadaEm` do perfil: com ele, a URL muda quando a foto é trocada e o navegador busca a
 * nova na hora. Sem ele (foto de outra pessoa), o cache dura poucos minutos.
 */
export function avatarUrl(userId: string, version?: string | null): string {
  const base = `/api/usuarios/${encodeURIComponent(userId)}/foto`;
  return version ? `${base}?v=${encodeURIComponent(version)}` : base;
}

/** Ids da API são UUIDs; qualquer outra coisa nem chega à API. */
export const isUuid = (value: string) =>
  /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i.test(value);
