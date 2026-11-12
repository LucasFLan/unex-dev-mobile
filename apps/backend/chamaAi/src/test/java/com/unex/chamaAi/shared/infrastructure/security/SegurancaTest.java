package com.unex.chamaAi.shared.infrastructure.security;

import com.unex.chamaAi.TestcontainersConfiguration;
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
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.WWW_AUTHENTICATE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class SegurancaTest {

    private static final String EMAIL = "joao@unex.edu.br";
    private static final String SENHA = "Senha123!";
    private static final ObjectMapper JSON = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioSpringDataRepository springData;

    @Autowired
    private PromoverUsuarioParaAdmin promoverParaAdmin;

    @Autowired
    private JwtDecoder jwtDecoder;

    @Autowired
    private JwtEncoder jwtEncoder;

    @BeforeEach
    void cadastrarUsuario() throws Exception {
        springData.deleteAll();
        mockMvc.perform(post("/usuarios").contentType(MediaType.APPLICATION_JSON).content("""
                        {"nome": "João", "dataNascimento": "2000-01-01", "cpf": "529.982.247-25",
                         "email": "%s", "senha": "%s"}
                        """.formatted(EMAIL, SENHA)))
                .andExpect(status().isCreated());
    }

    private ResultActions login(String email, String senha) throws Exception {
        return mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content("""
                {"email": "%s", "senha": "%s"}
                """.formatted(email, senha)));
    }

    private String token() throws Exception {
        String resposta = login(EMAIL, SENHA).andReturn().getResponse().getContentAsString();
        return JSON.readTree(resposta).get("accessToken").asString();
    }

    @Test
    void loginRetornaTokenComIdEPapel() throws Exception {
        login(EMAIL, SENHA)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.expiresIn").value(3600));

        Jwt jwt = jwtDecoder.decode(token());
        assertEquals(springData.findAll().getFirst().getId().toString(), jwt.getSubject());
        assertEquals("USUARIO", jwt.getClaimAsString("papel"));
        assertEquals("chamaAi", jwt.getClaimAsString("iss"));
        assertNotNull(jwt.getExpiresAt());
    }

    @Test
    void loginInvalidoRetorna401ComMensagemGenerica() throws Exception {
        login(EMAIL, "Errada123!")
                .andExpect(status().isUnauthorized())
                .andExpect(header().string(WWW_AUTHENTICATE, "Bearer"))
                .andExpect(jsonPath("$.detail").value("Email ou senha inválidos."));
        login("outro@unex.edu.br", SENHA)
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.detail").value("Email ou senha inválidos."));
    }

    @Test
    void semTokenOuComTokenInvalidoRetorna401EmProblemDetail() throws Exception {
        mockMvc.perform(get("/usuarios/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(header().string(WWW_AUTHENTICATE, "Bearer"))
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.detail").value("Token de acesso ausente, inválido ou expirado."));

        mockMvc.perform(get("/usuarios/me").header(AUTHORIZATION, "Bearer nao.e.jwt"))
                .andExpect(status().isUnauthorized());

        String adulterado = token().replaceFirst(".$", "x");
        mockMvc.perform(get("/usuarios/me").header(AUTHORIZATION, "Bearer " + adulterado))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void tokenExpiradoOuDeOutroEmissorRetorna401() throws Exception {
        Instant passado = Instant.now().minusSeconds(7200);
        String expirado = assinar(JwtClaimsSet.builder().issuer("chamaAi").subject(UUID.randomUUID().toString())
                .issuedAt(passado).expiresAt(passado.plusSeconds(60)).claim("papel", "ADMIN").build());
        String outroEmissor = assinar(JwtClaimsSet.builder().issuer("hacker").subject(UUID.randomUUID().toString())
                .issuedAt(Instant.now()).expiresAt(Instant.now().plusSeconds(600)).claim("papel", "ADMIN").build());

        mockMvc.perform(get("/usuarios/me").header(AUTHORIZATION, "Bearer " + expirado))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(get("/usuarios/me").header(AUTHORIZATION, "Bearer " + outroEmissor))
                .andExpect(status().isUnauthorized());
    }

    private String assinar(JwtClaimsSet claims) {
        return jwtEncoder.encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims))
                .getTokenValue();
    }

    @Test
    void rotasAdminExigemPapelAdmin() throws Exception {
        mockMvc.perform(get("/admin/qualquer-coisa"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/admin/qualquer-coisa").header(AUTHORIZATION, "Bearer " + token()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.detail").value("Você não tem permissão para acessar este recurso."));

        promoverParaAdmin.executar(new Email(EMAIL));
        String tokenAdmin = token();
        assertEquals("ADMIN", jwtDecoder.decode(tokenAdmin).getClaimAsString("papel"));
        // Passa pela segurança; 404 porque ainda não existe nenhuma rota /admin.
        mockMvc.perform(get("/admin/qualquer-coisa").header(AUTHORIZATION, "Bearer " + tokenAdmin))
                .andExpect(status().isNotFound());
    }

    @Test
    void rotasPublicasNaoExigemToken() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/auth/login'].post").exists())
                .andExpect(jsonPath("$.paths['/usuarios/me'].get").exists())
                .andExpect(jsonPath("$.components.securitySchemes.bearerAuth.scheme").value("bearer"));
        mockMvc.perform(get("/swagger-ui/index.html")).andExpect(status().isOk());
    }

    @Test
    void preflightDeCorsNaoExigeToken() throws Exception {
        mockMvc.perform(options("/usuarios/me")
                        .header("Origin", "http://localhost:8081")
                        .header("Access-Control-Request-Method", "GET")
                        .header("Access-Control-Request-Headers", "Authorization"))
                .andExpect(status().isOk())
                .andExpect(header().exists("Access-Control-Allow-Origin"));
    }
}
