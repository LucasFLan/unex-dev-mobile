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

    public static final String MENSAGEM_PROPOSTA_OBRIGATORIA = """
            {"title": "Bad Request", "status": 400, "detail": "A mensagem da proposta é obrigatória.",
             "instance": "/anuncios/7a1c9e2b-4d3f-4b8a-9e6d-2c5f8a1b3d4e/propostas"}""";

    public static final String PROPOSTA_DUPLICADA = """
            {"title": "Conflict", "status": 409, "detail": "Você já enviou uma proposta para este anúncio.",
             "instance": "/anuncios/7a1c9e2b-4d3f-4b8a-9e6d-2c5f8a1b3d4e/propostas"}""";

    public static final String PROPOSTA_PROPRIO_ANUNCIO = """
            {"title": "Unprocessable Content", "status": 422,
             "detail": "Você não pode enviar proposta para o seu próprio anúncio.",
             "instance": "/anuncios/7a1c9e2b-4d3f-4b8a-9e6d-2c5f8a1b3d4e/propostas"}""";

    public static final String PROPOSTA_NAO_ENCONTRADA = """
            {"title": "Not Found", "status": 404, "detail": "Proposta não encontrada.",
             "instance": "/propostas/c4d5e6f7-1a2b-4c3d-8e9f-0a1b2c3d4e5f/aceitar"}""";

    public static final String PROPOSTA_JA_RESPONDIDA = """
            {"title": "Unprocessable Content", "status": 422, "detail": "Esta proposta já foi respondida.",
             "instance": "/propostas/c4d5e6f7-1a2b-4c3d-8e9f-0a1b2c3d4e5f/aceitar"}""";

    public static final String ACESSO_NEGADO_RESPONDER_PROPOSTA = """
            {"title": "Forbidden", "status": 403,
             "detail": "Só o autor do anúncio pode responder a esta proposta.",
             "instance": "/propostas/c4d5e6f7-1a2b-4c3d-8e9f-0a1b2c3d4e5f/aceitar"}""";

    public static final String ACESSO_NEGADO_CANCELAR_PROPOSTA = """
            {"title": "Forbidden", "status": 403, "detail": "Só quem enviou a proposta pode cancelá-la.",
             "instance": "/propostas/c4d5e6f7-1a2b-4c3d-8e9f-0a1b2c3d4e5f/cancelar"}""";

    public static final String NOTIFICACAO_NAO_ENCONTRADA = """
            {"title": "Not Found", "status": 404, "detail": "Notificação não encontrada.",
             "instance": "/notificacoes/5b6c7d8e-9f0a-4b1c-8d2e-3f4a5b6c7d8e/lida"}""";

    public static final String CODIGO_RECUPERACAO_INVALIDO = """
            {"title": "Bad Request", "status": 400,
             "detail": "Código inválido ou expirado. Solicite um novo código.",
             "instance": "/auth/recuperar-senha/validar"}""";

    public static final String IMAGEM_VALIDACAO_INVALIDA = """
            {"title": "Bad Request", "status": 400, "detail": "Envie a foto do documento em JPG, PNG ou WEBP.",
             "instance": "/usuarios/me/validacao"}""";

    public static final String DOCUMENTOS_EM_ANALISE = """
            {"title": "Conflict", "status": 409, "detail": "Seus documentos já estão em análise. Aguarde a resposta.",
             "instance": "/usuarios/me/validacao"}""";

    public static final String CONTA_JA_VALIDADA = """
            {"title": "Unprocessable Content", "status": 422, "detail": "Sua conta já está validada.",
             "instance": "/usuarios/me/validacao"}""";

    public static final String CONTA_NAO_VALIDADA = """
            {"title": "Forbidden", "status": 403,
             "detail": "Valide sua conta para continuar: envie a foto do documento e uma selfie em Validar conta.",
             "instance": "/anuncios"}""";

    public static final String ENVIO_VALIDACAO_NAO_ENCONTRADO = """
            {"title": "Not Found", "status": 404, "detail": "Envio de validação não encontrado.",
             "instance": "/admin/validacoes/8d7c6b5a-4f3e-4d2c-9b1a-0f9e8d7c6b5a"}""";

    public static final String ENVIO_VALIDACAO_JA_ANALISADO = """
            {"title": "Unprocessable Content", "status": 422, "detail": "Este envio já foi analisado.",
             "instance": "/admin/validacoes/8d7c6b5a-4f3e-4d2c-9b1a-0f9e8d7c6b5a/aprovar"}""";

    public static final String MOTIVO_REPROVACAO_OBRIGATORIO = """
            {"title": "Bad Request", "status": 400,
             "detail": "Explique o motivo da reprovação (pelo menos 5 caracteres): o usuário vai ver essa mensagem.",
             "instance": "/admin/validacoes/8d7c6b5a-4f3e-4d2c-9b1a-0f9e8d7c6b5a/reprovar"}""";

    public static final String ERRO_INTERNO = """
            {"title": "Internal Server Error", "status": 500,
             "detail": "Erro interno do servidor."}""";

    private ExemplosDeErro() {
    }
}
