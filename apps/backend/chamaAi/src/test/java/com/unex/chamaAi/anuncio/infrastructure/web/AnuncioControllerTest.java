package com.unex.chamaAi.anuncio.infrastructure.web;

import com.unex.chamaAi.TestcontainersConfiguration;
import com.unex.chamaAi.anuncio.infrastructure.persistence.AnuncioSpringDataRepository;
import com.unex.chamaAi.anuncio.infrastructure.persistence.CategoriaSpringDataRepository;
import com.unex.chamaAi.usuario.application.usecases.PromoverUsuarioParaAdmin;
import com.unex.chamaAi.usuario.domain.valueobjects.Email;
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

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class AnuncioControllerTest {

    private static final ObjectMapper JSON = new ObjectMapper();
    private static final String SENHA = "Senha123!";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AnuncioSpringDataRepository anuncios;

    @Autowired
    private CategoriaSpringDataRepository categorias;

    @Autowired
    private UsuarioSpringDataRepository usuarios;

    @Autowired
    private PromoverUsuarioParaAdmin promoverParaAdmin;

    private String eletricistaId;
    private String pintorId;

    @BeforeEach
    void setUp() {
        anuncios.deleteAll();
        usuarios.deleteAll();
        eletricistaId = categoriaPorSlug("eletricista");
        pintorId = categoriaPorSlug("pintor");
    }

    private String categoriaPorSlug(String slug) {
        return categorias.findAll().stream().filter(c -> c.getSlug().equals(slug)).findFirst().orElseThrow()
                .getId().toString();
    }

    private String cadastrarELogar(String cpf, String email) throws Exception {
        mockMvc.perform(post("/usuarios").contentType(MediaType.APPLICATION_JSON).content("""
                        {"nome": "Fulano", "dataNascimento": "2000-01-01", "cpf": "%s", "email": "%s",
                         "senha": "%s", "telefone": "71999999999"}
                        """.formatted(cpf, email, SENHA)))
                .andExpect(status().isCreated());
        return login(email);
    }

    private String login(String email) throws Exception {
        String resposta = mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email": "%s", "senha": "%s"}
                                """.formatted(email, SENHA)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return "Bearer " + JSON.readTree(resposta).get("accessToken").asString();
    }

    private String joao() throws Exception {
        return cadastrarELogar("529.982.247-25", "joao@unex.edu.br");
    }

    private String maria() throws Exception {
        return cadastrarELogar("111.444.777-35", "maria@unex.edu.br");
    }

    private String corpo(String tipo, String categoriaId, String preco, String cidade, String uf) {
        return """
                {"tipo": "%s", "categoriaId": "%s", "titulo": "Eletricista residencial",
                 "descricao": "Instalação de chuveiros e tomadas", "preco": %s, "cidade": "%s", "uf": "%s"}
                """.formatted(tipo, categoriaId, preco, cidade, uf);
    }

    private ResultActions criar(String token, String corpo) throws Exception {
        return mockMvc.perform(post("/anuncios").header(AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON).content(corpo));
    }

    private String criarEObterId(String token, String corpo) throws Exception {
        String resposta = criar(token, corpo).andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return JSON.readTree(resposta).get("id").asString();
    }

    @Test
    void categoriasSaoPublicasEVemDoSeed() throws Exception {
        mockMvc.perform(get("/categorias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(14)))
                .andExpect(jsonPath("$[0].nome").value("Ar-condicionado e refrigeração"));
    }

    @Test
    void criaAnuncioERetorna201() throws Exception {
        String token = joao();
        String autorId = usuarios.findAll().getFirst().getId().toString();

        criar(token, corpo("OFERTA", eletricistaId, "150.5", "Salvador", "ba"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.tipo").value("OFERTA"))
                .andExpect(jsonPath("$.preco").value(150.50))
                .andExpect(jsonPath("$.categoria.slug").value("eletricista"))
                .andExpect(jsonPath("$.uf").value("BA"))
                .andExpect(jsonPath("$.ativo").value(true))
                .andExpect(jsonPath("$.autorId").value(autorId))
                .andExpect(jsonPath("$.criadoEm").exists());

        assertEquals(1, anuncios.count());
    }

    @Test
    void criarExigeLogin() throws Exception {
        mockMvc.perform(post("/anuncios").contentType(MediaType.APPLICATION_JSON)
                        .content(corpo("OFERTA", eletricistaId, "null", "Salvador", "BA")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void dadosInvalidosRetornam400() throws Exception {
        String token = joao();

        criar(token, corpo("VENDA", eletricistaId, "null", "Salvador", "BA"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Tipo de anúncio inválido. Use OFERTA ou PEDIDO."));
        criar(token, corpo("OFERTA", java.util.UUID.randomUUID().toString(), "null", "Salvador", "BA"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Categoria inválida."));
        criar(token, corpo("OFERTA", eletricistaId, "-10", "Salvador", "BA")).andExpect(status().isBadRequest());
        criar(token, corpo("OFERTA", eletricistaId, "null", "Salvador", "XX")).andExpect(status().isBadRequest());
        criar(token, "{}").andExpect(status().isBadRequest());

        assertEquals(0, anuncios.count());
    }

    @Test
    void listagemEhPublicaComFiltrosEPaginacao() throws Exception {
        String token = joao();
        criarEObterId(token, corpo("OFERTA", eletricistaId, "100", "Salvador", "BA"));
        criarEObterId(token, corpo("PEDIDO", pintorId, "null", "Feira de Santana", "BA"));
        criarEObterId(token, corpo("OFERTA", pintorId, "null", "Recife", "PE"));

        mockMvc.perform(get("/anuncios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalItens").value(3))
                .andExpect(jsonPath("$.itens[0].cidade").value("Recife"));
        mockMvc.perform(get("/anuncios").param("tipo", "PEDIDO"))
                .andExpect(jsonPath("$.totalItens").value(1))
                .andExpect(jsonPath("$.itens[0].preco").doesNotExist());
        mockMvc.perform(get("/anuncios").param("categoriaId", pintorId)).andExpect(jsonPath("$.totalItens").value(2));
        mockMvc.perform(get("/anuncios").param("uf", "ba")).andExpect(jsonPath("$.totalItens").value(2));
        mockMvc.perform(get("/anuncios").param("cidade", "salvador")).andExpect(jsonPath("$.totalItens").value(1));
        mockMvc.perform(get("/anuncios").param("pagina", "1").param("tamanho", "2"))
                .andExpect(jsonPath("$.itens", hasSize(1)))
                .andExpect(jsonPath("$.totalPaginas").value(2));
        mockMvc.perform(get("/anuncios").param("tamanho", "500")).andExpect(status().isBadRequest());
        mockMvc.perform(get("/anuncios").param("categoriaId", "nao-e-uuid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Parâmetro 'categoriaId' com formato inválido."));
        mockMvc.perform(get("/anuncios/{id}", "abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Parâmetro 'id' com formato inválido."));
    }

    @Test
    void detalheEhPublico() throws Exception {
        String id = criarEObterId(joao(), corpo("OFERTA", eletricistaId, "100", "Salvador", "BA"));

        mockMvc.perform(get("/anuncios/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));
        mockMvc.perform(get("/anuncios/{id}", java.util.UUID.randomUUID()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Anúncio não encontrado."));
    }

    @Test
    void autorEditaEOutroUsuarioRecebe403() throws Exception {
        String joao = joao();
        String maria = maria();
        String id = criarEObterId(joao, corpo("OFERTA", eletricistaId, "100", "Salvador", "BA"));

        mockMvc.perform(put("/anuncios/{id}", id).header(AUTHORIZATION, joao)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpo("PEDIDO", pintorId, "null", "Recife", "PE")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipo").value("PEDIDO"))
                .andExpect(jsonPath("$.categoria.slug").value("pintor"))
                .andExpect(jsonPath("$.preco").doesNotExist());

        mockMvc.perform(put("/anuncios/{id}", id).header(AUTHORIZATION, maria)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpo("OFERTA", eletricistaId, "1", "Salvador", "BA")))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.detail").value("Você não tem permissão para alterar este anúncio."));
    }

    @Test
    void excluirPeloAutorOuAdmin() throws Exception {
        String joao = joao();
        String maria = maria();
        String primeiro = criarEObterId(joao, corpo("OFERTA", eletricistaId, "100", "Salvador", "BA"));
        String segundo = criarEObterId(joao, corpo("OFERTA", eletricistaId, "100", "Salvador", "BA"));

        mockMvc.perform(delete("/anuncios/{id}", primeiro)).andExpect(status().isUnauthorized());
        mockMvc.perform(delete("/anuncios/{id}", primeiro).header(AUTHORIZATION, maria))
                .andExpect(status().isForbidden());
        mockMvc.perform(delete("/anuncios/{id}", primeiro).header(AUTHORIZATION, joao))
                .andExpect(status().isNoContent());

        promoverParaAdmin.executar(new Email("maria@unex.edu.br"));
        String mariaAdmin = login("maria@unex.edu.br");
        mockMvc.perform(delete("/anuncios/{id}", segundo).header(AUTHORIZATION, mariaAdmin))
                .andExpect(status().isNoContent());

        assertEquals(0, anuncios.count());
    }

    @Test
    void autorDesativaEReativaAnuncio() throws Exception {
        String joao = joao();
        String maria = maria();
        String id = criarEObterId(joao, corpo("OFERTA", eletricistaId, "100", "Salvador", "BA"));

        mockMvc.perform(patch("/anuncios/{id}/desativar", id)).andExpect(status().isUnauthorized());
        mockMvc.perform(patch("/anuncios/{id}/desativar", id).header(AUTHORIZATION, maria))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.detail").value("Você não tem permissão para alterar este anúncio."));

        mockMvc.perform(patch("/anuncios/{id}/desativar", id).header(AUTHORIZATION, joao))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ativo").value(false));
        mockMvc.perform(get("/anuncios")).andExpect(jsonPath("$.totalItens").value(0));
        mockMvc.perform(get("/anuncios/{id}", id)).andExpect(status().isNotFound());
        mockMvc.perform(get("/anuncios/{id}", id).header(AUTHORIZATION, joao))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ativo").value(false));
        mockMvc.perform(get("/usuarios/me/anuncios").header(AUTHORIZATION, joao))
                .andExpect(jsonPath("$.totalItens").value(1));
        mockMvc.perform(patch("/anuncios/{id}/ativar", id).header(AUTHORIZATION, maria))
                .andExpect(status().isNotFound());

        mockMvc.perform(patch("/anuncios/{id}/ativar", id).header(AUTHORIZATION, joao))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ativo").value(true));
        mockMvc.perform(patch("/anuncios/{id}/ativar", id).header(AUTHORIZATION, joao))
                .andExpect(status().isOk());
        mockMvc.perform(get("/anuncios")).andExpect(jsonPath("$.totalItens").value(1));
    }

    @Test
    void meusAnunciosListaSoOsDoUsuarioLogado() throws Exception {
        String joao = joao();
        String maria = maria();
        criarEObterId(joao, corpo("OFERTA", eletricistaId, "100", "Salvador", "BA"));
        criarEObterId(maria, corpo("OFERTA", eletricistaId, "100", "Salvador", "BA"));

        mockMvc.perform(get("/usuarios/me/anuncios").header(AUTHORIZATION, joao))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalItens").value(1));
        mockMvc.perform(get("/usuarios/me/anuncios")).andExpect(status().isUnauthorized());
    }

    @Test
    void excluirUsuarioRemoveSeusAnuncios() throws Exception {
        criarEObterId(joao(), corpo("OFERTA", eletricistaId, "100", "Salvador", "BA"));

        usuarios.deleteAll();

        assertEquals(0, anuncios.count());
    }
}
