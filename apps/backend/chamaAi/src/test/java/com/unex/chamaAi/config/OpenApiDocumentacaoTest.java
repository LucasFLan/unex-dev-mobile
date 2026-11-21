package com.unex.chamaAi.config;

import com.unex.chamaAi.TestcontainersConfiguration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Garante que a documentação OpenAPI continua completa (exemplos, erros e segurança). */
@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class OpenApiDocumentacaoTest {

    private static JsonNode spec;

    @BeforeAll
    static void carregar(@Autowired MockMvc mockMvc) throws Exception {
        String json = mockMvc.perform(get("/v3/api-docs")).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        spec = new ObjectMapper().readTree(json);
    }

    private static JsonNode operacao(String caminho, String metodo) {
        JsonNode operacao = spec.path("paths").path(caminho).path(metodo);
        assertFalse(operacao.isMissingNode(), metodo + " " + caminho + " não está documentado");
        return operacao;
    }

    private static List<JsonNode> todasAsOperacoes() {
        return StreamSupport.stream(spec.path("paths").spliterator(), false)
                .flatMap(caminho -> StreamSupport.stream(caminho.spliterator(), false))
                .toList();
    }

    @Test
    void rotasPublicasNaoExigemToken() {
        assertTrue(operacao("/usuarios", "post").path("security").isEmpty());
        assertTrue(operacao("/auth/login", "post").path("security").isEmpty());
        assertEquals("bearerAuth", spec.path("security").get(0).propertyNames().iterator().next());
    }

    @Test
    void todaOperacaoTemResumoTagEErro500() {
        for (JsonNode operacao : todasAsOperacoes()) {
            String id = operacao.path("operationId").asString();
            assertFalse(operacao.path("summary").asString().isBlank(), id + " sem summary");
            assertFalse(operacao.path("tags").isEmpty(), id + " sem tag");
            assertTrue(operacao.path("responses").has("500"), id + " sem resposta 500");
        }
    }

    @Test
    void rotasPublicasDeAnunciosNaoExigemToken() {
        assertTrue(operacao("/anuncios", "get").path("security").isEmpty());
        assertTrue(operacao("/categorias", "get").path("security").isEmpty());
    }

    @Test
    void rotasProtegidasDocumentam401() {
        for (String[] rota : new String[][]{{"/usuarios/me", "get"}, {"/usuarios/me", "put"}, {"/usuarios/me/senha", "patch"},
                {"/anuncios", "post"}, {"/anuncios/{id}", "put"}, {"/anuncios/{id}", "delete"},
                {"/anuncios/{id}/ativar", "patch"}, {"/anuncios/{id}/desativar", "patch"},
                {"/usuarios/me/anuncios", "get"}, {"/anuncios/{id}/propostas", "post"},
                {"/usuarios/me/propostas/recebidas", "get"}, {"/usuarios/me/propostas/enviadas", "get"},
                {"/usuarios/me/notificacoes", "get"}, {"/usuarios/me/notificacoes/nao-lidas", "get"},
                {"/usuarios/me/notificacoes/lidas", "patch"}, {"/notificacoes/{id}/lida", "patch"},
                {"/propostas/{id}/aceitar", "patch"}, {"/propostas/{id}/recusar", "patch"},
                {"/propostas/{id}/cancelar", "patch"}}) {
            assertTrue(operacao(rota[0], rota[1]).path("responses").has("401"), rota[1] + " " + rota[0] + " sem 401");
        }
    }

    @Test
    void cadastroDocumentaErrosPrevistosComExemplos() {
        JsonNode respostas = operacao("/usuarios", "post").path("responses");
        for (String status : List.of("201", "400", "409")) {
            assertTrue(respostas.has(status), "cadastro sem " + status);
        }
        JsonNode conflito = respostas.path("409").path("content").path("application/problem+json").path("examples");
        assertEquals("CPF já cadastrado.", conflito.path("cpfDuplicado").path("value").path("detail").asString());
        assertEquals("Email já cadastrado.", conflito.path("emailDuplicado").path("value").path("detail").asString());
    }

    @Test
    void exemplosSaoObjetosJsonENaoTexto() {
        JsonNode exemplo = operacao("/auth/login", "post").path("requestBody").path("content")
                .path("application/json").path("examples").path("login").path("value");
        assertTrue(exemplo.isObject(), "exemplo do login deveria ser objeto JSON");
        assertEquals("joao@unex.edu.br", exemplo.path("email").asString());

        JsonNode erro500 = spec.path("components").path("responses").path("ErroInterno").path("content")
                .path("application/problem+json").path("examples").path("erroInterno").path("value");
        assertEquals(500, erro500.path("status").asInt());
    }

    @Test
    void camposDosRequestsEResponsesTemDescricaoEExemplo() {
        assertTrue(spec.path("components").path("schemas").path("CadastrarUsuarioRequest").path("required")
                .toString().contains("senha"));
        for (String schema : List.of("CadastrarUsuarioRequest", "EditarPerfilRequest", "AlterarSenhaRequest",
                "LoginRequest", "UsuarioCadastradoResponse", "PerfilUsuarioResponse", "TokenAcessoResponse",
                "AnuncioRequest", "AnuncioResponse", "CategoriaResponse", "PaginaAnunciosResponse",
                "PropostaRequest", "PropostaResponse", "PropostaAnuncioResponse", "PropostaParticipanteResponse",
                "PaginaPropostasResponse", "NotificacaoResponse", "PaginaNotificacoesResponse",
                "ContagemNotificacoesResponse")) {
            JsonNode propriedades = spec.path("components").path("schemas").path(schema).path("properties");
            assertFalse(propriedades.isEmpty(), schema + " não está na especificação");
            propriedades.properties().forEach(campo -> {
                String nome = schema + "." + campo.getKey();
                assertFalse(campo.getValue().path("description").isMissingNode(), nome + " sem descrição");
                // Objetos e listas aninhados são exemplificados pelos próprios schemas.
                boolean escalar = !campo.getValue().has("$ref") && !campo.getValue().path("type").toString().contains("\"array\"");
                assertTrue(!escalar || campo.getValue().has("example"), nome + " sem exemplo");
            });
        }
    }

    @Test
    void edicaoEExclusaoDeAnuncioDocumentam403E404() {
        for (String metodo : List.of("put", "delete")) {
            JsonNode respostas = operacao("/anuncios/{id}", metodo).path("responses");
            assertTrue(respostas.has("403"), metodo + " /anuncios/{id} sem 403");
            assertTrue(respostas.has("404"), metodo + " /anuncios/{id} sem 404");
        }
        for (String acao : List.of("ativar", "desativar")) {
            JsonNode respostas = operacao("/anuncios/{id}/" + acao, "patch").path("responses");
            assertTrue(respostas.has("403"), "patch /anuncios/{id}/" + acao + " sem 403");
            assertTrue(respostas.has("404"), "patch /anuncios/{id}/" + acao + " sem 404");
        }
    }

    @Test
    void problemDetailDescreveOFormatoReal() {
        JsonNode propriedades = spec.path("components").path("schemas").path("ProblemDetail").path("properties");
        assertTrue(propriedades.has("detail"));
        assertFalse(propriedades.has("properties"), "campo interno do ProblemDetail não deve aparecer");
    }
}
