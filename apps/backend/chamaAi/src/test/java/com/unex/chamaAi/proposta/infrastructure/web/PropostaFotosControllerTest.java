package com.unex.chamaAi.proposta.infrastructure.web;

import com.unex.chamaAi.TestcontainersConfiguration;
import com.unex.chamaAi.anuncio.infrastructure.persistence.AnuncioSpringDataRepository;
import com.unex.chamaAi.anuncio.infrastructure.persistence.CategoriaSpringDataRepository;
import com.unex.chamaAi.notificacao.infrastructure.persistence.NotificacaoSpringDataRepository;
import com.unex.chamaAi.proposta.infrastructure.persistence.PropostaSpringDataRepository;
import com.unex.chamaAi.usuario.infrastructure.persistence.UsuarioSpringDataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class PropostaFotosControllerTest {

    private static final ObjectMapper JSON = new ObjectMapper();
    private static final String SENHA = "Senha123!";
    private static final byte[] JPEG = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0, 0, 0x10, 'J', 'F', 'I', 'F'};
    private static final byte[] PNG = {(byte) 0x89, 'P', 'N', 'G', 0x0D, 0x0A, 0x1A, 0x0A, 0, 0};

    @TempDir
    static Path pastaFotos;

    @DynamicPropertySource
    static void pastaDeFotos(DynamicPropertyRegistry registry) {
        registry.add("app.propostas.fotos-diretorio", () -> pastaFotos.toString());
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbc;

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
    private String pedro;
    private String pedidoDoJoao;

    @BeforeEach
    void setUp() throws Exception {
        notificacoes.deleteAll();
        propostas.deleteAll();
        anuncios.deleteAll();
        usuarios.deleteAll();
        joao = cadastrarELogar("João Silva", "529.982.247-25", "joao@unex.edu.br");
        maria = cadastrarELogar("Maria Souza", "111.444.777-35", "maria@unex.edu.br");
        pedro = cadastrarELogar("Pedro Lima", "390.533.447-05", "pedro@unex.edu.br");
        pedidoDoJoao = criarPedido(joao);
    }

    private String cadastrarELogar(String nome, String cpf, String email) throws Exception {
        mockMvc.perform(post("/usuarios").contentType(MediaType.APPLICATION_JSON).content("""
                        {"nome": "%s", "dataNascimento": "2000-01-01", "cpf": "%s", "email": "%s",
                         "senha": "%s"}""".formatted(nome, cpf, email, SENHA)))
                .andExpect(status().isCreated());
        jdbc.update("update usuarios set status_validacao = 'APROVADO' where email = ?", email);
        String resposta = mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email": "%s", "senha": "%s"}""".formatted(email, SENHA)))
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

    private MockMultipartHttpServletRequestBuilder proposta(String token, byte[]... fotos) {
        MockMultipartHttpServletRequestBuilder requisicao = multipart("/anuncios/{id}/propostas", pedidoDoJoao);
        for (int i = 0; i < fotos.length; i++) {
            requisicao.file(new MockMultipartFile("fotos", "foto" + i + ".jpg", "image/jpeg", fotos[i]));
        }
        requisicao.param("valor", "150.50").param("mensagem", "Segue a foto do último serviço.");
        requisicao.header(AUTHORIZATION, token);
        return requisicao;
    }

    @Test
    void enviaPropostaComFotosEAutorDoAnuncioVe() throws Exception {
        String resposta = mockMvc.perform(proposta(maria, JPEG, PNG))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.valor").value(150.50))
                .andExpect(jsonPath("$.mensagem").value("Segue a foto do último serviço."))
                .andExpect(jsonPath("$.fotos", hasSize(2)))
                .andReturn().getResponse().getContentAsString();
        JsonNode proposta = JSON.readTree(resposta);
        String propostaId = proposta.get("id").asString();
        String primeira = proposta.get("fotos").get(0).asString();
        String segunda = proposta.get("fotos").get(1).asString();

        mockMvc.perform(get("/propostas/{id}/fotos/{fotoId}", propostaId, primeira).header(AUTHORIZATION, joao))
                .andExpect(status().isOk())
                .andExpect(content().contentType("image/jpeg"))
                .andExpect(content().bytes(JPEG));
        mockMvc.perform(get("/propostas/{id}/fotos/{fotoId}", propostaId, segunda).header(AUTHORIZATION, maria))
                .andExpect(status().isOk())
                .andExpect(content().contentType("image/png"));
        assertThat(Files.exists(pastaFotos.resolve(propostaId).resolve(primeira))).isTrue();

        // A listagem de recebidas traz os ids das fotos.
        mockMvc.perform(get("/usuarios/me/propostas/recebidas").header(AUTHORIZATION, joao))
                .andExpect(jsonPath("$.itens[0].fotos", hasSize(2)))
                .andExpect(jsonPath("$.itens[0].fotos[0]").value(primeira));
    }

    @Test
    void quemNaoEstaNaPropostaNaoVeAFoto() throws Exception {
        JsonNode proposta = JSON.readTree(mockMvc.perform(proposta(maria, JPEG))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString());

        mockMvc.perform(get("/propostas/{id}/fotos/{fotoId}", proposta.get("id").asString(),
                        proposta.get("fotos").get(0).asString()).header(AUTHORIZATION, pedro))
                .andExpect(status().isNotFound());
    }

    @Test
    void multipartSemFotoFuncionaEJsonContinuaFuncionando() throws Exception {
        mockMvc.perform(proposta(maria))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.fotos", hasSize(0)));

        mockMvc.perform(post("/anuncios/{id}/propostas", pedidoDoJoao).header(AUTHORIZATION, pedro)
                        .contentType(MediaType.APPLICATION_JSON).content("""
                                {"valor": null, "mensagem": "Posso ir hoje."}"""))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.fotos", hasSize(0)));
    }

    @Test
    void recusaMaisDeCincoFotosOuArquivoQueNaoEhImagem() throws Exception {
        mockMvc.perform(proposta(maria, JPEG, JPEG, JPEG, JPEG, JPEG, JPEG))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Anexe no máximo 5 fotos."));
        mockMvc.perform(proposta(maria, "texto".getBytes()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Anexe as fotos em JPG, PNG ou WEBP."));

        assertThat(propostas.count()).isZero();
    }
}
