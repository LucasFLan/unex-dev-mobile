package com.unex.chamaAi.usuario.infrastructure.web;

import com.unex.chamaAi.TestcontainersConfiguration;
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


import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class UsuarioControllerTest {

    private static final String CPF = "529.982.247-25";
    private static final String OUTRO_CPF = "111.444.777-35";
    private static final String EMAIL = "joao@unex.edu.br";
    private static final String OUTRO_EMAIL = "maria@unex.edu.br";
    private static final String SENHA = "Senha123!";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioSpringDataRepository springData;

    @BeforeEach
    void limparBanco() {
        springData.deleteAll();
    }

    private ResultActions cadastrar(String corpo) throws Exception {
        return mockMvc.perform(post("/usuarios").contentType(MediaType.APPLICATION_JSON).content(corpo));
    }

    private String corpo(String cpf, String email, String senha) {
        return """
                {
                  "nome": "João",
                  "dataNascimento": "2000-01-01",
                  "cpf": "%s",
                  "email": "%s",
                  "senha": "%s",
                  "telefone": "71999999999"
                }
                """.formatted(cpf, email, senha);
    }

    @Test
    void cadastraUsuarioERetorna201() throws Exception {
        cadastrar(corpo(CPF, EMAIL, SENHA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nome").value("João"))
                .andExpect(jsonPath("$.email").value(EMAIL))
                .andExpect(jsonPath("$.senha").doesNotExist())
                .andExpect(jsonPath("$.senhaHash").doesNotExist());

        assertEquals(1, springData.count());
        assertTrue(springData.findAll().getFirst().getSenhaHash().startsWith("$2"));
    }

    @Test
    void cpfDuplicadoRetorna409() throws Exception {
        cadastrar(corpo(CPF, EMAIL, SENHA)).andExpect(status().isCreated());

        cadastrar(corpo(CPF, OUTRO_EMAIL, SENHA))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value("CPF já cadastrado."));
    }

    @Test
    void emailDuplicadoRetorna409() throws Exception {
        cadastrar(corpo(CPF, EMAIL, SENHA)).andExpect(status().isCreated());

        cadastrar(corpo(OUTRO_CPF, EMAIL, SENHA))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value("Email já cadastrado."));
    }

    @Test
    void dadoInvalidoRetorna400() throws Exception {
        cadastrar(corpo("123", EMAIL, SENHA))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("CPF inválido."));
        assertEquals(0, springData.count());
    }

    @Test
    void senhaAcimaDoLimiteDoBCryptRetorna400() throws Exception {
        cadastrar(corpo(CPF, EMAIL, "Aa1!" + "x".repeat(69))).andExpect(status().isBadRequest());
    }

    @Test
    void camposAusentesRetornam400() throws Exception {
        cadastrar("{}").andExpect(status().isBadRequest());
    }

    @Test
    void rotaInexistenteRetorna404ParaUsuarioLogado() throws Exception {
        String token = cadastrarELogar();
        mockMvc.perform(get("/rota-inexistente").header(AUTHORIZATION, token)).andExpect(status().isNotFound());
    }

    @Test
    void documentacaoOpenApiContinuaDisponivel() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/usuarios'].post").exists());
    }

    @Test
    void corpoMalformadoOuDataInvalidaRetornam400() throws Exception {
        cadastrar("{").andExpect(status().isBadRequest());
        cadastrar(corpo(CPF, EMAIL, SENHA).replace("2000-01-01", "01/01/2000"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Corpo da requisição inválido."));
    }

    private static final ObjectMapper JSON = new ObjectMapper();
    private static final String NOVA_SENHA = "NovaSenha456@";

    private String login(String email, String senha) throws Exception {
        String resposta = mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email": "%s", "senha": "%s"}
                                """.formatted(email, senha)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return "Bearer " + JSON.readTree(resposta).get("accessToken").asString();
    }

    private String cadastrarELogar() throws Exception {
        cadastrar(corpo(CPF, EMAIL, SENHA)).andExpect(status().isCreated());
        return login(EMAIL, SENHA);
    }

    @Test
    void visualizaPerfilDoUsuarioLogadoSemExporSenha() throws Exception {
        String token = cadastrarELogar();
        String id = springData.findAll().getFirst().getId().toString();

        mockMvc.perform(get("/usuarios/me").header(AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.nome").value("João"))
                .andExpect(jsonPath("$.dataNascimento").value("2000-01-01"))
                .andExpect(jsonPath("$.cpf").value("52998224725"))
                .andExpect(jsonPath("$.email").value(EMAIL))
                .andExpect(jsonPath("$.telefone").value("71999999999"))
                .andExpect(jsonPath("$.verificado").value(false))
                .andExpect(jsonPath("$.papel").value("USUARIO"))
                .andExpect(jsonPath("$.senha").doesNotExist())
                .andExpect(jsonPath("$.senhaHash").doesNotExist());
    }

    @Test
    void perfilDeUsuarioRemovidoRetorna404() throws Exception {
        String token = cadastrarELogar();
        springData.deleteAll();

        mockMvc.perform(get("/usuarios/me").header(AUTHORIZATION, token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Usuário não encontrado."));
    }

    @Test
    void rotasDePerfilExigemToken() throws Exception {
        mockMvc.perform(get("/usuarios/me")).andExpect(status().isUnauthorized());
        mockMvc.perform(put("/usuarios/me").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(patch("/usuarios/me/senha").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void naoExisteMaisRotaDePerfilPorId() throws Exception {
        String token = cadastrarELogar();
        String id = springData.findAll().getFirst().getId().toString();

        mockMvc.perform(get("/usuarios/{id}", id).header(AUTHORIZATION, token)).andExpect(status().isNotFound());
    }

    @Test
    void editaPerfilDoUsuarioLogado() throws Exception {
        String token = cadastrarELogar();

        mockMvc.perform(put("/usuarios/me").header(AUTHORIZATION, token).contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome": "João Silva", "dataNascimento": "1999-05-10", "telefone": "71888888888"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("João Silva"))
                .andExpect(jsonPath("$.dataNascimento").value("1999-05-10"))
                .andExpect(jsonPath("$.telefone").value("71888888888"))
                .andExpect(jsonPath("$.email").value(EMAIL));

        assertEquals("João Silva", springData.findAll().getFirst().getNome());
    }

    @Test
    void editarComNomeVazioRetorna400() throws Exception {
        String token = cadastrarELogar();

        mockMvc.perform(put("/usuarios/me").header(AUTHORIZATION, token).contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome": " ", "dataNascimento": "1999-05-10"}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void alteraSenhaEPassaALogarComANova() throws Exception {
        String token = cadastrarELogar();

        mockMvc.perform(patch("/usuarios/me/senha").header(AUTHORIZATION, token).contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"senhaAtual": "%s", "novaSenha": "%s"}
                                """.formatted(SENHA, NOVA_SENHA)))
                .andExpect(status().isNoContent());

        assertTrue(springData.findAll().getFirst().getSenhaHash().startsWith("$2"));
        login(EMAIL, NOVA_SENHA);
        mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email": "%s", "senha": "%s"}
                                """.formatted(EMAIL, SENHA)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void senhaAtualIncorretaRetorna422() throws Exception {
        String token = cadastrarELogar();

        mockMvc.perform(patch("/usuarios/me/senha").header(AUTHORIZATION, token).contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"senhaAtual": "Errada123!", "novaSenha": "%s"}
                                """.formatted(NOVA_SENHA)))
                .andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.detail").value("Senha atual incorreta."));
    }
}
