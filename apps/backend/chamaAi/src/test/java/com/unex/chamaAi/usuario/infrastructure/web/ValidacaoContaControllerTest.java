package com.unex.chamaAi.usuario.infrastructure.web;

import com.unex.chamaAi.TestcontainersConfiguration;
import com.unex.chamaAi.anuncio.infrastructure.persistence.CategoriaSpringDataRepository;
import com.unex.chamaAi.usuario.application.usecases.PromoverUsuarioParaAdmin;
import com.unex.chamaAi.usuario.domain.valueobjects.Email;
import com.unex.chamaAi.usuario.infrastructure.persistence.UsuarioSpringDataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
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
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class ValidacaoContaControllerTest {

    private static final ObjectMapper JSON = new ObjectMapper();
    private static final String SENHA = "Senha123!";
    private static final byte[] JPEG = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0, 0, 0x10, 'J', 'F', 'I', 'F'};
    private static final byte[] PNG = {(byte) 0x89, 'P', 'N', 'G', 0x0D, 0x0A, 0x1A, 0x0A, 0, 0};

    @TempDir
    static Path pastaUploads;

    @DynamicPropertySource
    static void pastaDeUploads(DynamicPropertyRegistry registry) {
        registry.add("app.validacao.diretorio", () -> pastaUploads.toString());
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioSpringDataRepository usuarios;

    @Autowired
    private CategoriaSpringDataRepository categorias;

    @Autowired
    private PromoverUsuarioParaAdmin promoverParaAdmin;

    private String joao;
    private String admin;

    @BeforeEach
    void setUp() throws Exception {
        usuarios.deleteAll();
        joao = cadastrarELogar("529.982.247-25", "joao@unex.edu.br");
        cadastrarELogar("111.444.777-35", "admin@unex.edu.br");
        promoverParaAdmin.executar(new Email("admin@unex.edu.br"));
        admin = login("admin@unex.edu.br"); // O papel vai no token: loga de novo depois de promover.
    }

    private String cadastrarELogar(String cpf, String email) throws Exception {
        mockMvc.perform(post("/usuarios").contentType(MediaType.APPLICATION_JSON).content("""
                        {"nome": "João da Silva", "dataNascimento": "2000-01-01", "cpf": "%s", "email": "%s",
                         "senha": "%s", "telefone": "71999999999"}
                        """.formatted(cpf, email, SENHA)))
                .andExpect(status().isCreated());
        return login(email);
    }

    private String login(String email) throws Exception {
        String resposta = mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"%s\", \"senha\": \"%s\"}".formatted(email, SENHA)))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        return "Bearer " + JSON.readTree(resposta).get("accessToken").asString();
    }

    private ResultActions enviar(String token, byte[] documento, byte[] selfie) throws Exception {
        var requisicao = multipart("/usuarios/me/validacao").header(AUTHORIZATION, token);
        if (documento != null) {
            requisicao.file(new MockMultipartFile("documento", "rg.jpg", "image/jpeg", documento));
        }
        if (selfie != null) {
            requisicao.file(new MockMultipartFile("selfie", "selfie.png", "image/png", selfie));
        }
        return mockMvc.perform(requisicao);
    }

    private ResultActions criarAnuncio(String token) throws Exception {
        String categoriaId = categorias.findAll().getFirst().getId().toString();
        return mockMvc.perform(post("/anuncios").header(AUTHORIZATION, token).contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"tipo": "OFERTA", "categoriaId": "%s", "titulo": "Eletricista", "descricao": "Serviços",
                         "preco": null, "cidade": "Salvador", "uf": "BA"}""".formatted(categoriaId)));
    }

    private String primeiroDaFila() throws Exception {
        String fila = mockMvc.perform(get("/admin/validacoes").header(AUTHORIZATION, admin))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        return JSON.readTree(fila).get("itens").get(0).get("id").asString();
    }

    @Test
    void fluxoCompletoDeAprovacao() throws Exception {
        mockMvc.perform(get("/usuarios/me").header(AUTHORIZATION, joao))
                .andExpect(jsonPath("$.statusValidacao").value("PENDENTE"));
        mockMvc.perform(get("/usuarios/me/validacao").header(AUTHORIZATION, joao))
                .andExpect(jsonPath("$.situacao").value("NAO_ENVIADO"))
                .andExpect(jsonPath("$.podeEnviar").value(true));
        criarAnuncio(joao).andExpect(status().isForbidden())
                .andExpect(jsonPath("$.detail").value(
                        "Valide sua conta para continuar: envie a foto do documento e uma selfie em Validar conta."));

        enviar(joao, JPEG, PNG).andExpect(status().isCreated())
                .andExpect(jsonPath("$.situacao").value("EM_ANALISE"));
        enviar(joao, JPEG, PNG).andExpect(status().isConflict());

        mockMvc.perform(get("/admin/validacoes").header(AUTHORIZATION, joao)).andExpect(status().isForbidden());
        mockMvc.perform(get("/admin/validacoes").header(AUTHORIZATION, admin))
                .andExpect(jsonPath("$.itens", hasSize(1)))
                .andExpect(jsonPath("$.itens[0].usuario.email").value("joao@unex.edu.br"))
                .andExpect(jsonPath("$.itens[0].usuario.cpf").value("52998224725"));
        String envioId = primeiroDaFila();

        // As imagens ficam na pasta local, uma subpasta por envio.
        assertThat(Files.readAllBytes(pastaUploads.resolve(envioId).resolve("documento.jpg")))
                .isEqualTo(JPEG);
        assertThat(pastaUploads.resolve(envioId).resolve("selfie.png")).exists();

        mockMvc.perform(get("/admin/validacoes/{id}/arquivos/DOCUMENTO", envioId).header(AUTHORIZATION, admin))
                .andExpect(status().isOk())
                .andExpect(content().contentType("image/jpeg"))
                .andExpect(header().string("Cache-Control", "no-store"))
                .andExpect(content().bytes(JPEG));
        mockMvc.perform(get("/admin/validacoes/{id}/arquivos/SELFIE", envioId).header(AUTHORIZATION, admin))
                .andExpect(content().contentType("image/png"));
        mockMvc.perform(get("/admin/validacoes/{id}/arquivos/DOCUMENTO", envioId).header(AUTHORIZATION, joao))
                .andExpect(status().isForbidden());

        mockMvc.perform(patch("/admin/validacoes/{id}/aprovar", envioId).header(AUTHORIZATION, admin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APROVADO"));
        mockMvc.perform(patch("/admin/validacoes/{id}/aprovar", envioId).header(AUTHORIZATION, admin))
                .andExpect(status().isUnprocessableContent());

        mockMvc.perform(get("/usuarios/me/validacao").header(AUTHORIZATION, joao))
                .andExpect(jsonPath("$.situacao").value("APROVADO"));
        mockMvc.perform(get("/usuarios/me/notificacoes").header(AUTHORIZATION, joao))
                .andExpect(jsonPath("$.itens[0].tipo").value("VALIDACAO_APROVADA"));
        criarAnuncio(joao).andExpect(status().isCreated());
        enviar(joao, JPEG, PNG).andExpect(status().isUnprocessableContent());
    }

    @Test
    void reprovacaoComMotivoEReenvio() throws Exception {
        enviar(joao, JPEG, PNG).andExpect(status().isCreated());
        String envioId = primeiroDaFila();

        mockMvc.perform(patch("/admin/validacoes/{id}/reprovar", envioId).header(AUTHORIZATION, admin)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"motivo\": \"\"}"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(patch("/admin/validacoes/{id}/reprovar", envioId).header(AUTHORIZATION, admin)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"motivo\": \"Documento ilegível\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REPROVADO"));

        mockMvc.perform(get("/usuarios/me/validacao").header(AUTHORIZATION, joao))
                .andExpect(jsonPath("$.situacao").value("REPROVADO"))
                .andExpect(jsonPath("$.motivoReprovacao").value("Documento ilegível"))
                .andExpect(jsonPath("$.podeEnviar").value(true));
        mockMvc.perform(get("/usuarios/me/notificacoes").header(AUTHORIZATION, joao))
                .andExpect(jsonPath("$.itens[0].tipo").value("VALIDACAO_REPROVADA"));
        criarAnuncio(joao).andExpect(status().isForbidden());

        enviar(joao, PNG, JPEG).andExpect(status().isCreated());
        mockMvc.perform(get("/admin/validacoes").header(AUTHORIZATION, admin))
                .andExpect(jsonPath("$.itens", hasSize(1)));
        mockMvc.perform(get("/admin/validacoes").param("status", "REPROVADO").header(AUTHORIZATION, admin))
                .andExpect(jsonPath("$.itens[0].motivoReprovacao").value("Documento ilegível"));
    }

    @Test
    void uploadInvalido() throws Exception {
        enviar(joao, JPEG, null).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Envie a selfie com o documento."));
        enviar(joao, "%PDF-1.7 documento".getBytes(), PNG).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Envie a foto do documento em JPG, PNG ou WEBP."));
        mockMvc.perform(get("/usuarios/me/validacao").header(AUTHORIZATION, joao))
                .andExpect(jsonPath("$.situacao").value("NAO_ENVIADO"));
        mockMvc.perform(multipart("/usuarios/me/validacao")
                        .file(new MockMultipartFile("documento", "rg.jpg", "image/jpeg", JPEG))
                        .file(new MockMultipartFile("selfie", "s.png", "image/png", PNG)))
                .andExpect(status().isUnauthorized());
    }
}
