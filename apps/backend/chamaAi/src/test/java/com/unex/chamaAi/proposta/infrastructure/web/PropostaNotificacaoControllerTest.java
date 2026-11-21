package com.unex.chamaAi.proposta.infrastructure.web;

import com.unex.chamaAi.TestcontainersConfiguration;
import com.unex.chamaAi.anuncio.infrastructure.persistence.AnuncioSpringDataRepository;
import com.unex.chamaAi.anuncio.infrastructure.persistence.CategoriaSpringDataRepository;
import com.unex.chamaAi.notificacao.infrastructure.persistence.NotificacaoSpringDataRepository;
import com.unex.chamaAi.proposta.infrastructure.persistence.PropostaSpringDataRepository;
import com.unex.chamaAi.usuario.infrastructure.persistence.UsuarioSpringDataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class PropostaNotificacaoControllerTest {

    private static final ObjectMapper JSON = new ObjectMapper();
    private static final String SENHA = "Senha123!";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private NotificacaoSpringDataRepository notificacoes;

    @Autowired
    private PropostaSpringDataRepository propostas;

    @Autowired
    private AnuncioSpringDataRepository anuncios;

    @Autowired
    private CategoriaSpringDataRepository categorias;

    @Autowired
    private UsuarioSpringDataRepository usuarios;

    private String joao;
    private String maria;
    private String pedidoDoJoao;

    @BeforeEach
    void setUp() throws Exception {
        notificacoes.deleteAll();
        propostas.deleteAll();
        anuncios.deleteAll();
        usuarios.deleteAll();
        joao = cadastrarELogar("João Silva", "529.982.247-25", "joao@unex.edu.br");
        maria = cadastrarELogar("Maria Souza", "111.444.777-35", "maria@unex.edu.br");
        pedidoDoJoao = criarPedido(joao);
    }

    private String cadastrarELogar(String nome, String cpf, String email) throws Exception {
        mockMvc.perform(post("/usuarios").contentType(MediaType.APPLICATION_JSON).content("""
                        {"nome": "%s", "dataNascimento": "2000-01-01", "cpf": "%s", "email": "%s",
                         "senha": "%s", "telefone": "71999999999"}
                        """.formatted(nome, cpf, email, SENHA)))
                .andExpect(status().isCreated());
        String resposta = mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email": "%s", "senha": "%s"}
                                """.formatted(email, SENHA)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return "Bearer " + JSON.readTree(resposta).get("accessToken").asString();
    }

    private String criarPedido(String token) throws Exception {
        String categoriaId = categorias.findAll().getFirst().getId().toString();
        String resposta = mockMvc.perform(post("/anuncios").header(AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON).content("""
                                {"tipo": "PEDIDO", "categoriaId": "%s", "titulo": "Trocar chuveiro",
                                 "descricao": "Chuveiro queimou", "preco": null, "cidade": "Salvador", "uf": "BA"}
                                """.formatted(categoriaId)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return JSON.readTree(resposta).get("id").asString();
    }

    private ResultActions propor(String token, String anuncioId, String corpo) throws Exception {
        return mockMvc.perform(post("/anuncios/{id}/propostas", anuncioId).header(AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON).content(corpo));
    }

    @Test
    void enviaPropostaEAutorDoAnuncioEhNotificado() throws Exception {
        propor(maria, pedidoDoJoao, """
                {"valor": 180, "mensagem": "Posso ir amanhã cedo."}""")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.valor").value(180.00))
                .andExpect(jsonPath("$.mensagem").value("Posso ir amanhã cedo."))
                .andExpect(jsonPath("$.remetente.nome").value("Maria Souza"))
                .andExpect(jsonPath("$.anuncio.id").value(pedidoDoJoao))
                .andExpect(jsonPath("$.anuncio.titulo").value("Trocar chuveiro"));

        mockMvc.perform(get("/usuarios/me/notificacoes/nao-lidas").header(AUTHORIZATION, joao))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.naoLidas").value(1));
        mockMvc.perform(get("/usuarios/me/notificacoes").header(AUTHORIZATION, joao))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itens", hasSize(1)))
                .andExpect(jsonPath("$.itens[0].tipo").value("PROPOSTA_RECEBIDA"))
                .andExpect(jsonPath("$.itens[0].titulo").value("Nova proposta recebida"))
                .andExpect(jsonPath("$.itens[0].mensagem").value(containsString("Maria Souza enviou uma proposta")))
                .andExpect(jsonPath("$.itens[0].anuncioId").value(pedidoDoJoao))
                .andExpect(jsonPath("$.itens[0].lida").value(false));

        // Quem enviou não é notificado.
        mockMvc.perform(get("/usuarios/me/notificacoes/nao-lidas").header(AUTHORIZATION, maria))
                .andExpect(jsonPath("$.naoLidas").value(0));

        mockMvc.perform(get("/usuarios/me/propostas/recebidas").header(AUTHORIZATION, joao))
                .andExpect(jsonPath("$.totalItens").value(1))
                .andExpect(jsonPath("$.itens[0].remetente.nome").value("Maria Souza"));
        mockMvc.perform(get("/usuarios/me/propostas/enviadas").header(AUTHORIZATION, maria))
                .andExpect(jsonPath("$.totalItens").value(1));
    }

    @Test
    void marcaNotificacoesComoLidas() throws Exception {
        propor(maria, pedidoDoJoao, "{\"mensagem\": \"Oi\"}").andExpect(status().isCreated());
        String lista = mockMvc.perform(get("/usuarios/me/notificacoes").header(AUTHORIZATION, joao))
                .andReturn().getResponse().getContentAsString();
        String notificacaoId = JSON.readTree(lista).get("itens").get(0).get("id").asString();

        mockMvc.perform(patch("/notificacoes/{id}/lida", notificacaoId).header(AUTHORIZATION, maria))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Notificação não encontrada."));
        mockMvc.perform(patch("/notificacoes/{id}/lida", notificacaoId).header(AUTHORIZATION, joao))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/usuarios/me/notificacoes/nao-lidas").header(AUTHORIZATION, joao))
                .andExpect(jsonPath("$.naoLidas").value(0));

        String outroPedido = criarPedido(joao);
        propor(maria, outroPedido, "{\"mensagem\": \"Oi de novo\"}").andExpect(status().isCreated());
        mockMvc.perform(patch("/usuarios/me/notificacoes/lidas").header(AUTHORIZATION, joao))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/usuarios/me/notificacoes/nao-lidas").header(AUTHORIZATION, joao))
                .andExpect(jsonPath("$.naoLidas").value(0));
        mockMvc.perform(get("/usuarios/me/notificacoes").header(AUTHORIZATION, joao))
                .andExpect(jsonPath("$.itens", hasSize(2)))
                .andExpect(jsonPath("$.itens[0].lida").value(true));
    }

    @Test
    void regrasDeEnvio() throws Exception {
        propor(joao, pedidoDoJoao, "{\"mensagem\": \"Oi\"}")
                .andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.detail").value("Você não pode enviar proposta para o seu próprio anúncio."));
        propor(maria, pedidoDoJoao, "{\"mensagem\": \"  \"}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("A mensagem da proposta é obrigatória."));
        propor(maria, pedidoDoJoao, "{\"valor\": -5, \"mensagem\": \"Oi\"}").andExpect(status().isBadRequest());
        propor(maria, UUID.randomUUID().toString(), "{\"mensagem\": \"Oi\"}").andExpect(status().isNotFound());

        propor(maria, pedidoDoJoao, "{\"mensagem\": \"Oi\"}").andExpect(status().isCreated());
        propor(maria, pedidoDoJoao, "{\"mensagem\": \"De novo\"}")
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value("Você já enviou uma proposta para este anúncio."));

        mockMvc.perform(post("/anuncios/{id}/propostas", pedidoDoJoao).contentType(MediaType.APPLICATION_JSON)
                .content("{\"mensagem\": \"Oi\"}")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/usuarios/me/notificacoes")).andExpect(status().isUnauthorized());

        assertEquals(1, propostas.count());
        assertEquals(1, notificacoes.count());
    }

    private String proporEObterId(String token, String anuncioId) throws Exception {
        String resposta = propor(token, anuncioId, "{\"valor\": 150, \"mensagem\": \"Proposta\"}")
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        return JSON.readTree(resposta).get("id").asString();
    }

    @Test
    void aceitarPedidoLiberaContatoPausaPedidoENaoSelecionaAsOutras() throws Exception {
        String pedro = cadastrarELogar("Pedro Lima", "390.533.447-05", "pedro@unex.edu.br");
        String daMaria = proporEObterId(maria, pedidoDoJoao);
        String doPedro = proporEObterId(pedro, pedidoDoJoao);

        mockMvc.perform(get("/usuarios/me/propostas/recebidas").header(AUTHORIZATION, joao))
                .andExpect(jsonPath("$.itens[0].status").value("PENDENTE"))
                .andExpect(jsonPath("$.itens[0].remetente.telefone").doesNotExist());

        mockMvc.perform(patch("/propostas/{id}/aceitar", daMaria).header(AUTHORIZATION, maria))
                .andExpect(status().isForbidden());
        mockMvc.perform(patch("/propostas/{id}/aceitar", daMaria).header(AUTHORIZATION, pedro))
                .andExpect(status().isNotFound());

        mockMvc.perform(patch("/propostas/{id}/aceitar", daMaria).header(AUTHORIZATION, joao))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACEITA"))
                .andExpect(jsonPath("$.respondidoEm").exists())
                .andExpect(jsonPath("$.remetente.telefone").value("71999999999"))
                .andExpect(jsonPath("$.destinatario.telefone").value("71999999999"));

        mockMvc.perform(get("/anuncios/{id}", pedidoDoJoao).header(AUTHORIZATION, joao))
                .andExpect(jsonPath("$.ativo").value(false));
        mockMvc.perform(get("/usuarios/me/propostas/enviadas").header(AUTHORIZATION, pedro))
                .andExpect(jsonPath("$.itens[0].status").value("NAO_SELECIONADA"))
                .andExpect(jsonPath("$.itens[0].destinatario.telefone").doesNotExist());
        mockMvc.perform(get("/usuarios/me/notificacoes").header(AUTHORIZATION, maria))
                .andExpect(jsonPath("$.itens[0].tipo").value("PROPOSTA_ACEITA"));
        mockMvc.perform(get("/usuarios/me/notificacoes").header(AUTHORIZATION, pedro))
                .andExpect(jsonPath("$.itens[0].tipo").value("PROPOSTA_NAO_SELECIONADA"));

        mockMvc.perform(patch("/propostas/{id}/recusar", doPedro).header(AUTHORIZATION, joao))
                .andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.detail").value("Esta proposta já foi respondida."));
    }

    @Test
    void recusarECancelar() throws Exception {
        String daMaria = proporEObterId(maria, pedidoDoJoao);

        mockMvc.perform(patch("/propostas/{id}/cancelar", daMaria).header(AUTHORIZATION, joao))
                .andExpect(status().isForbidden());
        mockMvc.perform(patch("/propostas/{id}/recusar", daMaria).header(AUTHORIZATION, joao))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("RECUSADA"));
        mockMvc.perform(get("/anuncios/{id}", pedidoDoJoao)).andExpect(jsonPath("$.ativo").value(true));
        mockMvc.perform(get("/usuarios/me/notificacoes").header(AUTHORIZATION, maria))
                .andExpect(jsonPath("$.itens[0].tipo").value("PROPOSTA_RECUSADA"));

        String outroPedido = criarPedido(joao);
        String outra = proporEObterId(maria, outroPedido);
        mockMvc.perform(patch("/propostas/{id}/cancelar", outra).header(AUTHORIZATION, maria))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELADA"));
        mockMvc.perform(patch("/propostas/{id}/aceitar", outra).header(AUTHORIZATION, joao))
                .andExpect(status().isUnprocessableContent());
    }

    @Test
    void anuncioPausadoNaoRecebePropostas() throws Exception {
        mockMvc.perform(patch("/anuncios/{id}/desativar", pedidoDoJoao).header(AUTHORIZATION, joao))
                .andExpect(status().isOk());
        propor(maria, pedidoDoJoao, "{\"mensagem\": \"Oi\"}").andExpect(status().isNotFound());
    }

    @Test
    void excluirAnuncioApagaPropostasEMantemNotificacaoSemLink() throws Exception {
        propor(maria, pedidoDoJoao, "{\"mensagem\": \"Oi\"}").andExpect(status().isCreated());
        mockMvc.perform(delete("/anuncios/{id}", pedidoDoJoao).header(AUTHORIZATION, joao))
                .andExpect(status().isNoContent());

        assertEquals(0, propostas.count());
        mockMvc.perform(get("/usuarios/me/notificacoes").header(AUTHORIZATION, joao))
                .andExpect(jsonPath("$.itens", hasSize(1)))
                .andExpect(jsonPath("$.itens[0].anuncioId").doesNotExist());
    }
}
