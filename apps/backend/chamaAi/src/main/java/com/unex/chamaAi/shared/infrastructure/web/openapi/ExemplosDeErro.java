package com.unex.chamaAi.shared.infrastructure.web.openapi;

/**
 * Corpos de erro (RFC 9457 / ProblemDetail) usados como exemplo na documentação OpenAPI.
 * Devem refletir exatamente o que a API devolve: o GlobalExceptionHandler omite "type" (about:blank) e
 * inclui "instance"; o RespostasDeSeguranca (401/403 do Spring Security) inclui "type" e omite "instance".
 */
public final class ExemplosDeErro {

    public static final String PROBLEM_JSON = "application/problem+json";

    public static final String CORPO_INVALIDO = """
            {"title": "Bad Request", "status": 400,
             "detail": "Corpo da requisição inválido.", "instance": "/usuarios"}""";

    public static final String CPF_INVALIDO = """
            {"title": "Bad Request", "status": 400,
             "detail": "CPF inválido.", "instance": "/usuarios"}""";

    public static final String SENHA_FRACA = """
            {"title": "Bad Request", "status": 400,
             "detail": "A senha deve ter no mínimo 8 caracteres, uma letra maiúscula, um número e um caractere especial.",
             "instance": "/usuarios"}""";

    public static final String NOME_OBRIGATORIO = """
            {"title": "Bad Request", "status": 400,
             "detail": "Nome é obrigatório.", "instance": "/usuarios/me"}""";

    public static final String DATA_NASCIMENTO_FUTURA = """
            {"title": "Bad Request", "status": 400,
             "detail": "A data de nascimento não pode estar no futuro.", "instance": "/usuarios/me"}""";

    public static final String NOVA_SENHA_FRACA = """
            {"title": "Bad Request", "status": 400,
             "detail": "A senha deve ter no mínimo 8 caracteres, uma letra maiúscula, um número e um caractere especial.",
             "instance": "/usuarios/me/senha"}""";

    public static final String CREDENCIAIS_INVALIDAS = """
            {"title": "Unauthorized", "status": 401,
             "detail": "Email ou senha inválidos.", "instance": "/auth/login"}""";

    public static final String TOKEN_INVALIDO = """
            {"type": "about:blank", "title": "Unauthorized", "status": 401,
             "detail": "Token de acesso ausente, inválido ou expirado."}""";

    public static final String USUARIO_NAO_ENCONTRADO = """
            {"title": "Not Found", "status": 404,
             "detail": "Usuário não encontrado.", "instance": "/usuarios/me"}""";

    public static final String CPF_JA_CADASTRADO = """
            {"title": "Conflict", "status": 409,
             "detail": "CPF já cadastrado.", "instance": "/usuarios"}""";

    public static final String EMAIL_JA_CADASTRADO = """
            {"title": "Conflict", "status": 409,
             "detail": "Email já cadastrado.", "instance": "/usuarios"}""";

    public static final String SENHA_ATUAL_INCORRETA = """
            {"title": "Unprocessable Content", "status": 422,
             "detail": "Senha atual incorreta.", "instance": "/usuarios/me/senha"}""";

    public static final String TIPO_ANUNCIO_INVALIDO = """
            {"title": "Bad Request", "status": 400,
             "detail": "Tipo de anúncio inválido. Use OFERTA ou PEDIDO.", "instance": "/anuncios"}""";

    public static final String CATEGORIA_INVALIDA = """
            {"title": "Bad Request", "status": 400,
             "detail": "Categoria inválida.", "instance": "/anuncios"}""";

    public static final String PRECO_INVALIDO = """
            {"title": "Bad Request", "status": 400,
             "detail": "O preço deve ser maior que zero.", "instance": "/anuncios"}""";

    public static final String UF_INVALIDA = """
            {"title": "Bad Request", "status": 400,
             "detail": "UF inválida.", "instance": "/anuncios"}""";

    public static final String TAMANHO_PAGINA_INVALIDO = """
            {"title": "Bad Request", "status": 400,
             "detail": "O tamanho da página deve estar entre 1 e 50.", "instance": "/anuncios"}""";

    public static final String PARAMETRO_INVALIDO = """
            {"title": "Bad Request", "status": 400,
             "detail": "Parâmetro 'categoriaId' com formato inválido.", "instance": "/anuncios"}""";

    public static final String ID_INVALIDO = """
            {"title": "Bad Request", "status": 400,
             "detail": "Parâmetro 'id' com formato inválido.", "instance": "/anuncios/abc"}""";

    public static final String ACESSO_NEGADO_ANUNCIO = """
            {"title": "Forbidden", "status": 403,
             "detail": "Você não tem permissão para alterar este anúncio.",
             "instance": "/anuncios/7a1c9e2b-4d3f-4b8a-9e6d-2c5f8a1b3d4e"}""";

    public static final String ANUNCIO_NAO_ENCONTRADO = """
            {"title": "Not Found", "status": 404, "detail": "Anúncio não encontrado.",
             "instance": "/anuncios/7a1c9e2b-4d3f-4b8a-9e6d-2c5f8a1b3d4e"}""";

    public static final String ERRO_INTERNO = """
            {"title": "Internal Server Error", "status": 500,
             "detail": "Erro interno do servidor."}""";

    private ExemplosDeErro() {
    }
}
