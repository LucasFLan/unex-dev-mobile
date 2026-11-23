/**
 * ÚNICO lugar para ajustar ao que a API Java expõe.
 * Confira cada valor no Swagger: http://localhost:8080/swagger-ui.html
 */

/** Nome do cookie que guarda o token de login. */
export const SESSION_COOKIE = "token";

/** Caminhos dos endpoints (ajuste conforme o controller). */
export const ENDPOINTS = {
  login: "/auth/login",
  register: "/usuarios",
  /** PUT: atualiza nome, data de nascimento e telefone do usuário logado. */
  updateUser: "/usuarios/me",
  /** GET: dados do usuário logado. */
  me: "/usuarios/me",
  /** GET (público): lista de categorias, em ordem alfabética. */
  categories: "/categorias",
  /**
   * POST: cria um anúncio cujo autor é o usuário do token.
   * GET (público): lista anúncios ativos. GET/PUT/DELETE `/anuncios/{id}`;
   * PATCH `/anuncios/{id}/ativar` e `/anuncios/{id}/desativar` (só o autor).
   */
  ads: "/anuncios",
  /** GET: anúncios do usuário logado, ativos e inativos (paginado). */
  myAds: "/usuarios/me/anuncios",
  /** POST `/anuncios/{id}/propostas`: envia proposta para o autor do anúncio. */
  proposals: (adId: string) => `/anuncios/${encodeURIComponent(adId)}/propostas`,
  /** GET: notificações do usuário logado (paginado, mais recentes primeiro). */
  notifications: "/usuarios/me/notificacoes",
  /** GET: { naoLidas } para o contador do sino. */
  unreadNotifications: "/usuarios/me/notificacoes/nao-lidas",
  /** PATCH: marca todas as notificações do usuário como lidas. */
  readAllNotifications: "/usuarios/me/notificacoes/lidas",
  /** PATCH: altera a senha do usuário logado (exige a senha atual). */
  changePassword: "/usuarios/me/senha",
  /** PUT (multipart, campo `foto`): envia ou troca a foto de perfil. DELETE: remove. */
  myPhoto: "/usuarios/me/foto",
  /** GET: foto de perfil de qualquer usuário (exige login). */
  userPhoto: (userId: string) => `/usuarios/${encodeURIComponent(userId)}/foto`,
  /** GET: endereço de um CEP (logradouro, bairro, cidade, UF e ponto aproximado). */
  cep: (cep: string) => `/cep/${encodeURIComponent(cep)}`,
  /** GET: média, total e avaliações recebidas por um usuário. */
  userReviews: (userId: string) => `/usuarios/${encodeURIComponent(userId)}/avaliacoes`,
  /** POST: avaliação (nota + comentário) do serviço concluído. */
  proposalReview: (proposalId: string) => `/propostas/${encodeURIComponent(proposalId)}/avaliacao`,
  /** GET: foto anexada a uma proposta (só quem enviou ou recebeu). */
  proposalPhoto: (proposalId: string, photoId: string) =>
    `/propostas/${encodeURIComponent(proposalId)}/fotos/${encodeURIComponent(photoId)}`,
} as const;

/** Campos do JSON de PATCH /usuarios/me/senha. */
export const PASSWORD_FIELDS = {
  current: "senhaAtual",
  next: "novaSenha",
} as const;

/** Campos do JSON de POST /anuncios. */
export const AD_FIELDS = {
  type: "tipo", // "OFERTA" | "PEDIDO"
  categoryId: "categoriaId", // UUID vindo de GET /categorias
  title: "titulo", // 3 a 120 caracteres
  description: "descricao", // obrigatória, até 5000
  price: "preco", // opcional, > 0, até 2 casas. null/ausente = "a combinar"
  city: "cidade", // obrigatória, até 100
  state: "uf", // sigla de um dos 27 estados
} as const;

/** Nomes dos campos do JSON enviado para a API. */
export const FIELDS = {
  name: "nome",
  birthDate: "dataNascimento",
  cpf: "cpf",
  email: "email",
  password: "senha",
  phone: "telefone",
} as const;

/**
 * Lê o token da resposta do login: { accessToken, tokenType, expiresIn }.
 * O cadastro (201) provavelmente não devolve token, e então retorna null.
 */
export function extractToken(data: unknown): string | null {
  if (typeof data !== "object" || data === null) return null;

  const { accessToken } = data as Record<string, unknown>;

  return typeof accessToken === "string" && accessToken.length > 0
    ? accessToken
    : null;
}

/** Procura uma mensagem de erro legível na resposta da API. */
export function extractErrorMessage(data: unknown): string | null {
  if (typeof data !== "object" || data === null) return null;

  const body = data as Record<string, unknown>;
  const message = body.message ?? body.detail;

  return typeof message === "string" && message.length > 0 ? message : null;
}

export type FieldError = { field: string | null; message: string };

const firstString = (values: unknown[]) =>
  values.find((value): value is string => typeof value === "string" && value.length > 0);

/**
 * Lê erros de validação nos formatos mais comuns do Spring: uma lista em
 * `errors`/`erros`/`violations`/`fieldErrors`/`campos` (de textos ou de objetos
 * com campo e mensagem) ou um mapa { campo: mensagem }.
 */
export function extractFieldErrors(data: unknown): FieldError[] {
  if (typeof data !== "object" || data === null) return [];

  const body = data as Record<string, unknown>;
  const list =
    body.errors ?? body.erros ?? body.violations ?? body.fieldErrors ?? body.campos;

  if (Array.isArray(list)) {
    return list.flatMap((item): FieldError[] => {
      if (typeof item === "string") return [{ field: null, message: item }];
      if (typeof item !== "object" || item === null) return [];

      const entry = item as Record<string, unknown>;
      const message = firstString([
        entry.message,
        entry.mensagem,
        entry.defaultMessage,
        entry.detail,
      ]);
      if (!message) return [];

      const field = firstString([
        entry.field,
        entry.campo,
        entry.name,
        entry.propertyPath,
      ]);
      return [{ field: field ?? null, message }];
    });
  }

  if (typeof list === "object" && list !== null) {
    return Object.entries(list).flatMap(([field, value]): FieldError[] => {
      if (typeof value === "string") return [{ field, message: value }];
      if (Array.isArray(value)) {
        return value
          .filter((item): item is string => typeof item === "string")
          .map((message) => ({ field, message }));
      }
      return [];
    });
  }

  return [];
}