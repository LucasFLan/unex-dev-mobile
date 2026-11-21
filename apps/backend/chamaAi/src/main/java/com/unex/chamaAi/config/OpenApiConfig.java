package com.unex.chamaAi.config;

import com.unex.chamaAi.shared.infrastructure.web.openapi.ExemplosDeErro;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    public static final String ESQUEMA_BEARER = "bearerAuth";
    public static final String TAG_AUTENTICACAO = "Autenticação";
    public static final String TAG_USUARIOS = "Usuários";
    public static final String TAG_ANUNCIOS = "Anúncios";
    public static final String TAG_CATEGORIAS = "Categorias";
    public static final String TAG_PROPOSTAS = "Propostas";
    public static final String TAG_NOTIFICACOES = "Notificações";

    private static final String RESPOSTA_ERRO_INTERNO = "ErroInterno";
    private static final String SCHEMA_PROBLEM_DETAIL = "#/components/schemas/ProblemDetail";

    private static final String DESCRICAO = """
            API do **ChamaAí**: plataforma que conecta clientes a profissionais autônomos verificados.

            ## Autenticação
            1. Cadastre-se em `POST /usuarios` (rota pública).
            2. Faça login em `POST /auth/login` e copie o `accessToken` da resposta.
            3. Clique em **Authorize** e cole o token (sem o prefixo `Bearer`).
            Nas chamadas fora do Swagger, envie o cabeçalho `Authorization: Bearer <accessToken>`.

            O token é um JWT (HS256) com validade padrão de 1 hora (`expiresIn`, em segundos). Não há refresh token:
            quando ele expirar, faça login de novo. O `sub` do token é o id do usuário e o claim `papel` traz
            `USUARIO` ou `ADMIN`.

            ## Formato dos erros
            Todos os erros seguem o padrão [RFC 9457 — Problem Details](https://www.rfc-editor.org/rfc/rfc9457),
            com `Content-Type: application/problem+json`. O campo `detail` traz uma mensagem em português,
            pronta para exibir ao usuário:

            ```json
            {"title": "Bad Request", "status": 400, "detail": "CPF inválido.", "instance": "/usuarios"}
            ```

            | Status | Quando acontece |
            |---|---|
            | 400 | Dado inválido (regra de validação) ou corpo JSON malformado |
            | 401 | Token ausente, inválido ou expirado, ou credenciais de login erradas |
            | 403 | Usuário autenticado sem permissão (ex.: editar anúncio de outra pessoa, rotas `/admin/**` sem ADMIN) |
            | 404 | Recurso não encontrado |
            | 409 | Conflito com dado já existente (ex.: CPF ou email duplicado) |
            | 422 | Regra de negócio violada (ex.: senha atual incorreta) |
            | 500 | Erro inesperado no servidor |
            """;

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ChamaAí API")
                        .version("1.0")
                        .description(DESCRICAO))
                .addTagsItem(new Tag().name(TAG_AUTENTICACAO).description("Login e emissão do token de acesso"))
                .addTagsItem(new Tag().name(TAG_USUARIOS).description("Cadastro e perfil do usuário logado"))
                .addTagsItem(new Tag().name(TAG_ANUNCIOS).description(
                        "Ofertas de serviço (prestador) e pedidos de serviço (cliente)"))
                .addTagsItem(new Tag().name(TAG_CATEGORIAS).description("Categorias de serviço dos anúncios"))
                .addTagsItem(new Tag().name(TAG_PROPOSTAS).description(
                        "Propostas enviadas para o autor de um anúncio"))
                .addTagsItem(new Tag().name(TAG_NOTIFICACOES).description("Avisos para o usuário logado"))
                .components(new Components()
                        .addSecuritySchemes(ESQUEMA_BEARER, new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Cole o accessToken retornado por POST /auth/login."))
                        .addResponses(RESPOSTA_ERRO_INTERNO, respostaDeErro(
                                "Erro inesperado no servidor. Os detalhes ficam só no log da aplicação.",
                                "erroInterno", ExemplosDeErro.ERRO_INTERNO))
                        .addResponses("NaoAutenticado", respostaDeErro(
                                "Token ausente, inválido ou expirado. Faça login de novo em POST /auth/login. "
                                        + "A resposta traz o cabeçalho `WWW-Authenticate: Bearer`.",
                                "tokenInvalido", ExemplosDeErro.TOKEN_INVALIDO))
                        .addResponses("UsuarioNaoEncontrado", respostaDeErro(
                                "O token é válido, mas o usuário dono dele não existe mais.",
                                "usuarioNaoEncontrado", ExemplosDeErro.USUARIO_NAO_ENCONTRADO))
                        .addResponses("AnuncioNaoEncontrado", respostaDeErro(
                                "Anúncio inexistente, ou inativo e o solicitante não é o autor.",
                                "anuncioNaoEncontrado", ExemplosDeErro.ANUNCIO_NAO_ENCONTRADO)))
                // Por padrão toda rota exige token; as públicas removem o requisito com @SecurityRequirements().
                .addSecurityItem(new SecurityRequirement().addList(ESQUEMA_BEARER));
    }

    /** Toda operação pode responder 500: adiciona a resposta uma vez só, em vez de anotar cada endpoint. */
    @Bean
    public OpenApiCustomizer adicionaErroInternoEmTodasAsOperacoes() {
        return openApi -> openApi.getPaths().values().forEach(caminho ->
                caminho.readOperations().forEach(operacao -> operacao.getResponses()
                        .addApiResponse("500", new ApiResponse().$ref("#/components/responses/" + RESPOSTA_ERRO_INTERNO))));
    }

    /**
     * O schema gerado a partir de ProblemDetail expõe um campo "properties" que nunca aparece no JSON
     * (o Jackson o achata). Substitui pelo formato real, com descrição de cada campo.
     */
    @Bean
    public OpenApiCustomizer descreveProblemDetail() {
        return openApi -> openApi.getComponents().addSchemas("ProblemDetail", new Schema<>()
                .type("object")
                .description("Erro no formato RFC 9457 (application/problem+json).")
                .addProperty("type", new Schema<>().type("string").format("uri")
                        .description("Presente só nos erros 401/403 do Spring Security, sempre `about:blank`."))
                .addProperty("title", new Schema<>().type("string").description("Nome do status HTTP.")
                        .example("Bad Request"))
                .addProperty("status", new Schema<>().type("integer").format("int32").description("Código HTTP.")
                        .example(400))
                .addProperty("detail", new Schema<>().type("string")
                        .description("Mensagem em português, pronta para exibir ao usuário.").example("CPF inválido."))
                .addProperty("instance", new Schema<>().type("string").format("uri")
                        .description("Caminho da requisição que falhou.").example("/usuarios")));
    }

    private static ApiResponse respostaDeErro(String descricao, String nomeExemplo, String exemplo) {
        return new ApiResponse()
                .description(descricao)
                .content(new Content().addMediaType(ExemplosDeErro.PROBLEM_JSON, new MediaType()
                        .schema(new Schema<>().$ref(SCHEMA_PROBLEM_DETAIL))
                        .addExamples(nomeExemplo, new Example().value(comoJson(exemplo)))));
    }

    /** Converte para objeto JSON; com String pura o Swagger UI mostraria o exemplo entre aspas. */
    private static Object comoJson(String json) {
        try {
            return Json.mapper().readTree(json);
        } catch (Exception e) {
            throw new IllegalStateException("Exemplo OpenAPI com JSON inválido.", e);
        }
    }
}
