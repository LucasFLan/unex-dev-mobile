package com.unex.chamaAi.usuario.infrastructure.web;

import com.unex.chamaAi.TestcontainersConfiguration;
import com.unex.chamaAi.usuario.infrastructure.persistence.UsuarioSpringDataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import tools.jackson.databind.ObjectMapper;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class FotoPerfilControllerTest {

    private static final ObjectMapper JSON = new ObjectMapper();
    private static final String SENHA = "Senha123!";
    private static final byte[] JPEG = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0, 0, 0x10, 'J', 'F', 'I', 'F'};
    private static final byte[] PNG = {(byte) 0x89, 'P', 'N', 'G', 0x0D, 0x0A, 0x1A, 0x0A, 0, 0};

    @TempDir
    static Path pastaFotos;

    @DynamicPropertySource
    static void pastaDeFotos(DynamicPropertyRegistry registry) {
        registry.add("app.perfil.fotos-diretorio", () -> pastaFotos.toString());
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioSpringDataRepository usuarios;

    private String joao;
    private String joaoId;
    private String maria;

    @BeforeEach
    void setUp() throws Exception {
        usuarios.deleteAll();
        joao = cadastrarELogar("529.982.247-25", "joao@unex.edu.br");
        maria = cadastrarELogar("111.444.777-35", "maria@unex.edu.br");
        joaoId = JSON.readTree(mockMvc.perform(get("/usuarios/me").header(AUTHORIZATION, joao))
                .andReturn().getResponse().getContentAsString()).get("id").asString();
    }

    private String cadastrarELogar(String cpf, String email) throws Exception {
        mockMvc.perform(post("/usuarios").contentType(MediaType.APPLICATION_JSON).content("""
                        {"nome": "Pessoa Teste", "dataNascimento": "2000-01-01", "cpf": "%s", "email": "%s",
                         "senha": "%s"}""".formatted(cpf, email, SENHA)))
                .andExpect(status().isCreated());
        String resposta = mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email": "%s", "senha": "%s"}""".formatted(email, SENHA)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return "Bearer " + JSON.readTree(resposta).get("accessToken").asString();
    }

    private ResultActions enviarFoto(String token, byte[] conteudo) throws Exception {
        return mockMvc.perform(multipart(HttpMethod.PUT, "/usuarios/me/foto")
                .file(new MockMultipartFile("foto", "foto.jpg", "image/jpeg", conteudo))
                .header(AUTHORIZATION, token));
    }

    @Test
    void contaNovaNaoTemFoto() throws Exception {
        mockMvc.perform(get("/usuarios/me").header(AUTHORIZATION, joao))
                .andExpect(jsonPath("$.fotoAtualizadaEm").value(nullValue()));
        mockMvc.perform(get("/usuarios/{id}/foto", joaoId).header(AUTHORIZATION, maria))
                .andExpect(status().isNotFound());
    }

    @Test
    void enviaFotoEOutroUsuarioConsegueVer() throws Exception {
        enviarFoto(joao, JPEG)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fotoAtualizadaEm").value(notNullValue()));

        mockMvc.perform(get("/usuarios/{id}/foto", joaoId).header(AUTHORIZATION, maria))
                .andExpect(status().isOk())
                .andExpect(content().contentType("image/jpeg"))
                .andExpect(content().bytes(JPEG))
                .andExpect(header().string("Cache-Control", containsString("private")))
                .andExpect(header().string("X-Content-Type-Options", "nosniff"));
        assertThat(Files.exists(pastaFotos.resolve(joaoId))).isTrue();
    }

    @Test
    void trocarAFotoSubstituiAAnterior() throws Exception {
        enviarFoto(joao, JPEG).andExpect(status().isOk());
        enviarFoto(joao, PNG).andExpect(status().isOk());

        mockMvc.perform(get("/usuarios/{id}/foto", joaoId).header(AUTHORIZATION, joao))
                .andExpect(status().isOk())
                .andExpect(content().contentType("image/png"))
                .andExpect(content().bytes(PNG));
    }

    @Test
    void removerFotoApagaOArquivo() throws Exception {
        enviarFoto(joao, JPEG).andExpect(status().isOk());

        mockMvc.perform(delete("/usuarios/me/foto").header(AUTHORIZATION, joao))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fotoAtualizadaEm").value(nullValue()));
        mockMvc.perform(get("/usuarios/{id}/foto", joaoId).header(AUTHORIZATION, maria))
                .andExpect(status().isNotFound());
        assertThat(Files.exists(pastaFotos.resolve(joaoId))).isFalse();
    }

    @Test
    void recusaArquivoQueNaoEhImagem() throws Exception {
        enviarFoto(joao, "<script>alert(1)</script>".getBytes())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Envie a foto de perfil em JPG, PNG ou WEBP."));
    }

    @Test
    void fotoExigeLogin() throws Exception {
        enviarFoto(joao, JPEG).andExpect(status().isOk());
        mockMvc.perform(get("/usuarios/{id}/foto", joaoId)).andExpect(status().isUnauthorized());
    }
}
