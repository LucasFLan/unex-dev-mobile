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

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    void rotaInexistenteRetorna404() throws Exception {
        mockMvc.perform(get("/rota-inexistente")).andExpect(status().isNotFound());
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
}
