package com.unex.chamaAi.proposta.infrastructure.web;

import com.unex.chamaAi.TestcontainersConfiguration;
import com.unex.chamaAi.anuncio.infrastructure.persistence.AnuncioSpringDataRepository;
import com.unex.chamaAi.anuncio.infrastructure.persistence.CategoriaSpringDataRepository;
import com.unex.chamaAi.notificacao.infrastructure.persistence.NotificacaoSpringDataRepository;
import com.unex.chamaAi.proposta.infrastructure.persistence.AvaliacaoSpringDataRepository;
import com.unex.chamaAi.proposta.infrastructure.persistence.PropostaSpringDataRepository;
import com.unex.chamaAi.usuario.infrastructure.persistence.UsuarioSpringDataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import tools.jackson.databind.ObjectMapper;

import static com.unex.chamaAi.TestcontainersConfiguration.CEP_FORA_DO_AR;
import static com.unex.chamaAi.TestcontainersConfiguration.CEP_SALVADOR;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Do anúncio com CEP até a avaliação: endereço protegido, conclusão, nota e média no anúncio. */
@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class ConclusaoAvaliacaoLocalizacaoControllerTest {

    private static final ObjectMapper JSON = new ObjectMapper();
    private static final String SENHA = "Senha123!";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    private AvaliacaoSpringDataRepository avaliacoes;

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
    private String mariaId;
    private String categoriaId;

    @BeforeEach
    void setUp() throws Exception {
        avaliacoes.deleteAll();
        notificacoes.deleteAll();
        propostas.deleteAll();
        anuncios.deleteAll();
        usuarios.deleteAll();
        categoriaId = categorias.findAll().getFirst().getId().toString();
        joao = cadastrarELogar("João Silva", "529.982.247-25", "joao@unex.edu.br");
        maria = cadastrarELogar("Maria Souza", "111.444.777-35", "maria@unex.edu.br");
        mariaId = JSON.readTree(mockMvc.perform(get("/usuarios/me").header(AUTHORIZATION, maria))
                .andReturn().getResponse().getContentAsString()).get("id").asString();
    }

    private String cadastrarELogar(String nome, String cpf, String email) throws Exception {
        mockMvc.perform(post("/usuarios").contentType(MediaType.APPLICATION_JSON).content("""
                        {"nome": "%s", "dataNascimento": "2000-01-01", "cpf": "%s", "email": "%s",
                         "senha": "%s", "telefone": "71999999999"}""".formatted(nome, cpf, email, SENHA)))
                .andExpect(status().isCreated());
        jdbc.update("update usuarios set status_validacao = 'APROVADO' where email = ?", email);
        String resposta = mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email": "%s", "senha": "%s"}""".formatted(email, SENHA)))
                .andReturn().getResponse().getContentAsString();
        return "Bearer " + JSON.readTree(resposta).get("accessToken").asString();
    }

    private ResultActions criarAnuncio(String token, String tipo, String cep, String extra) throws Exception {
        return mockMvc.perform(post("/anuncios").header(AUTHORIZATION, token).contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"tipo": "%s", "categoriaId": "%s", "titulo": "Trocar chuveiro", "descricao": "Queimou",
                         "preco": null, "cidade": "Salvador", "uf": "BA", "cep": %s %s}"""
                        .formatted(tipo, categoriaId, cep == null ? "null" : "\"" + cep + "\"", extra)));
    }

    private String id(ResultActions resultado) throws Exception {
        return JSON.readTree(resultado.andReturn().getResponse().getContentAsString()).get("id").asString();
    }

    @Test
    void publicoVeSoARegiaoEOAutorVeOEndereco() throws Exception {
        String pedido = id(criarAnuncio(joao, "PEDIDO", "40140-110", ", \"numero\": \"12\", \"complemento\": \"Ap 3\"")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.endereco.numero").value("12")));

        mockMvc.perform(get("/anuncios/{id}", pedido).header(AUTHORIZATION, maria))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bairro").value("Barra"))
                .andExpect(jsonPath("$.regiao.latitude").value(-13.01))
                .andExpect(jsonPath("$.regiao.longitude").value(-38.52))
                .andExpect(jsonPath("$.endereco").value(nullValue()))
                .andExpect(jsonPath("$.autor.nome").value("João Silva"));
        // Sem login também não vê o endereço.
        mockMvc.perform(get("/anuncios/{id}", pedido)).andExpect(jsonPath("$.endereco").value(nullValue()));

        mockMvc.perform(get("/anuncios/{id}", pedido).header(AUTHORIZATION, joao))
                .andExpect(jsonPath("$.endereco.cep").value(CEP_SALVADOR))
                .andExpect(jsonPath("$.endereco.logradouro").value("Avenida Sete de Setembro"))
                .andExpect(jsonPath("$.endereco.complemento").value("Ap 3"))
                .andExpect(jsonPath("$.endereco.latitude").value(-13.0123456));
        mockMvc.perform(get("/usuarios/me/anuncios").header(AUTHORIZATION, joao))
                .andExpect(jsonPath("$.itens[0].endereco.numero").value("12"));
    }

    @Test
    void regrasDoCep() throws Exception {
        criarAnuncio(joao, "PEDIDO", "12345678", "").andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("CEP não encontrado."));
        criarAnuncio(joao, "PEDIDO", "123", "").andExpect(status().isBadRequest());
        // Serviço de CEP fora do ar não impede publicar: fica sem o ponto no mapa.
        criarAnuncio(joao, "PEDIDO", CEP_FORA_DO_AR, "").andExpect(status().isCreated())
                .andExpect(jsonPath("$.regiao").value(nullValue()));
        // Sem CEP continua valendo (anúncios antigos e clientes antigos).
        criarAnuncio(joao, "PEDIDO", null, "").andExpect(status().isCreated())
                .andExpect(jsonPath("$.bairro").value(nullValue()));

        mockMvc.perform(get("/cep/{cep}", "40140-110").header(AUTHORIZATION, joao))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bairro").value("Barra"))
                .andExpect(jsonPath("$.cidade").value("Salvador"))
                .andExpect(jsonPath("$.latitude").value(-13.01));
        mockMvc.perform(get("/cep/{cep}", "12345678").header(AUTHORIZATION, joao)).andExpect(status().isNotFound());
        mockMvc.perform(get("/cep/{cep}", CEP_FORA_DO_AR).header(AUTHORIZATION, joao))
                .andExpect(status().isServiceUnavailable());
        mockMvc.perform(get("/cep/{cep}", CEP_SALVADOR)).andExpect(status().isUnauthorized());
    }

    @Test
    void raioSoValeParaOferta() throws Exception {
        criarAnuncio(maria, "OFERTA", CEP_SALVADOR, ", \"raioAtendimentoKm\": 10").andExpect(status().isCreated())
                .andExpect(jsonPath("$.raioAtendimentoKm").value(10));
        criarAnuncio(maria, "PEDIDO", CEP_SALVADOR, ", \"raioAtendimentoKm\": 10").andExpect(status().isCreated())
                .andExpect(jsonPath("$.raioAtendimentoKm").value(nullValue()));
        criarAnuncio(maria, "OFERTA", CEP_SALVADOR, ", \"raioAtendimentoKm\": 500").andExpect(status().isBadRequest());
    }

    @Test
    void concluiAvaliaEAMediaApareceNosAnunciosDoProfissional() throws Exception {
        String pedido = id(criarAnuncio(joao, "PEDIDO", CEP_SALVADOR, ", \"numero\": \"12\""));
        String proposta = id(mockMvc.perform(post("/anuncios/{id}/propostas", pedido).header(AUTHORIZATION, maria)
                        .contentType(MediaType.APPLICATION_JSON).content("""
                                {"valor": 150, "mensagem": "Troco hoje"}"""))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.endereco").value(nullValue())));

        // Antes do aceite: não dá para concluir nem avaliar.
        mockMvc.perform(patch("/propostas/{id}/concluir", proposta).header(AUTHORIZATION, joao))
                .andExpect(status().isUnprocessableContent());

        mockMvc.perform(patch("/propostas/{id}/aceitar", proposta).header(AUTHORIZATION, joao))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.endereco.numero").value("12"));
        // Depois do aceite a profissional recebe o endereço completo.
        mockMvc.perform(get("/usuarios/me/propostas/enviadas").header(AUTHORIZATION, maria))
                .andExpect(jsonPath("$.itens[0].endereco.logradouro").value("Avenida Sete de Setembro"))
                .andExpect(jsonPath("$.itens[0].endereco.numero").value("12"));

        mockMvc.perform(post("/propostas/{id}/avaliacao", proposta).header(AUTHORIZATION, joao)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"nota\": 5}"))
                .andExpect(status().isUnprocessableContent());
        mockMvc.perform(patch("/propostas/{id}/concluir", proposta).header(AUTHORIZATION, maria))
                .andExpect(status().isForbidden());
        mockMvc.perform(patch("/propostas/{id}/concluir", proposta).header(AUTHORIZATION, joao))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONCLUIDA"));

        mockMvc.perform(post("/propostas/{id}/avaliacao", proposta).header(AUTHORIZATION, joao)
                        .contentType(MediaType.APPLICATION_JSON).content("""
                                {"nota": 4, "comentario": "Pontual."}"""))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nota").value(4))
                .andExpect(jsonPath("$.autor.nome").value("João Silva"));
        mockMvc.perform(post("/propostas/{id}/avaliacao", proposta).header(AUTHORIZATION, joao)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"nota\": 5}"))
                .andExpect(status().isConflict());
        mockMvc.perform(post("/propostas/{id}/avaliacao", proposta).header(AUTHORIZATION, joao)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"nota\": 9}"))
                .andExpect(status().isConflict()); // já avaliada: a duplicidade vem antes da nota

        mockMvc.perform(get("/usuarios/{id}/avaliacoes", mariaId).header(AUTHORIZATION, joao))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Maria Souza"))
                .andExpect(jsonPath("$.media").value(4.0))
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.itens[0].comentario").value("Pontual."))
                .andExpect(jsonPath("$.itens[0].servicoTitulo").value("Trocar chuveiro"));
        mockMvc.perform(get("/usuarios/me/propostas/recebidas").header(AUTHORIZATION, joao))
                .andExpect(jsonPath("$.itens[0].avaliacao.nota").value(4))
                .andExpect(jsonPath("$.itens[0].concluidoEm").exists());

        // A média vai junto nos anúncios da Maria.
        criarAnuncio(maria, "OFERTA", CEP_SALVADOR, "").andExpect(status().isCreated());
        mockMvc.perform(get("/anuncios").param("tipo", "OFERTA"))
                .andExpect(jsonPath("$.itens[0].autor.nome").value("Maria Souza"))
                .andExpect(jsonPath("$.itens[0].autor.mediaAvaliacoes").value(4.0))
                .andExpect(jsonPath("$.itens[0].autor.totalAvaliacoes").value(1));

        mockMvc.perform(get("/usuarios/me/notificacoes").header(AUTHORIZATION, maria))
                .andExpect(jsonPath("$.itens[*].tipo").value(hasItems("SERVICO_CONCLUIDO", "AVALIACAO_RECEBIDA")));
    }

    @Test
    void avaliacaoSobreviveAExclusaoDoAnuncio() throws Exception {
        String pedido = id(criarAnuncio(joao, "PEDIDO", CEP_SALVADOR, ""));
        String proposta = id(mockMvc.perform(post("/anuncios/{id}/propostas", pedido).header(AUTHORIZATION, maria)
                .contentType(MediaType.APPLICATION_JSON).content("{\"mensagem\": \"Oi\"}")));
        mockMvc.perform(patch("/propostas/{id}/aceitar", proposta).header(AUTHORIZATION, joao));
        mockMvc.perform(patch("/propostas/{id}/concluir", proposta).header(AUTHORIZATION, joao));
        mockMvc.perform(post("/propostas/{id}/avaliacao", proposta).header(AUTHORIZATION, joao)
                .contentType(MediaType.APPLICATION_JSON).content("{\"nota\": 5}")).andExpect(status().isCreated());

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                .delete("/anuncios/{id}", pedido).header(AUTHORIZATION, joao)).andExpect(status().isNoContent());

        mockMvc.perform(get("/usuarios/{id}/avaliacoes", mariaId).header(AUTHORIZATION, joao))
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.itens[0].propostaId").value(nullValue()))
                .andExpect(jsonPath("$.itens[0].servicoTitulo").value("Trocar chuveiro"));
    }
}
