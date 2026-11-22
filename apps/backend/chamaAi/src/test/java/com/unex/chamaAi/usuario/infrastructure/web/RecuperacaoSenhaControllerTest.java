package com.unex.chamaAi.usuario.infrastructure.web;

import com.unex.chamaAi.TestcontainersConfiguration;
import com.unex.chamaAi.usuario.application.ports.EnviadorEmail;
import com.unex.chamaAi.usuario.application.ports.GeradorCodigo;
import com.unex.chamaAi.usuario.infrastructure.persistence.UsuarioSpringDataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import({TestcontainersConfiguration.class, RecuperacaoSenhaControllerTest.EmailFalso.class})
class RecuperacaoSenhaControllerTest {

    private static final String EMAIL = "joao@unex.edu.br";
    private static final String SENHA = "Senha123!";
    private static final String NOVA_SENHA = "NovaSenha456@";
    private static final String CODIGO = "482913";

    /** Não envia e-mail de verdade nos testes: guarda o que seria enviado e usa um código fixo. */
    @TestConfiguration(proxyBeanMethods = false)
    static class EmailFalso {

        static final List<String> ENVIADOS = new CopyOnWriteArrayList<>();

        @Bean
        @Primary
        EnviadorEmail enviadorEmailFalso() {
            return (destinatario, assunto, texto) -> ENVIADOS.add(destinatario + "|" + texto);
        }

        @Bean
        @Primary
        GeradorCodigo geradorCodigoFixo() {
            return () -> CODIGO;
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioSpringDataRepository usuarios;

    @BeforeEach
    void setUp() throws Exception {
        usuarios.deleteAll();
        EmailFalso.ENVIADOS.clear();
        mockMvc.perform(post("/usuarios").contentType(MediaType.APPLICATION_JSON).content("""
                        {"nome": "João da Silva", "dataNascimento": "2000-01-01", "cpf": "529.982.247-25",
                         "email": "%s", "senha": "%s", "telefone": "71999999999"}
                        """.formatted(EMAIL, SENHA)))
                .andExpect(status().isCreated());
    }

    private ResultActions postar(String caminho, String corpo) throws Exception {
        return mockMvc.perform(post(caminho).contentType(MediaType.APPLICATION_JSON).content(corpo));
    }

    private ResultActions login(String senha) throws Exception {
        return postar("/auth/login", """
                {"email": "%s", "senha": "%s"}""".formatted(EMAIL, senha));
    }

    @Test
    void fluxoCompletoSemLogin() throws Exception {
        postar("/auth/recuperar-senha", "{\"email\": \"" + EMAIL + "\"}").andExpect(status().isAccepted());

        assertEquals(1, EmailFalso.ENVIADOS.size());
        assertTrue(EmailFalso.ENVIADOS.getFirst().startsWith(EMAIL + "|"));
        assertTrue(EmailFalso.ENVIADOS.getFirst().contains(CODIGO));

        postar("/auth/recuperar-senha/validar", """
                {"email": "%s", "codigo": "%s"}""".formatted(EMAIL, CODIGO)).andExpect(status().isNoContent());
        postar("/auth/recuperar-senha/redefinir", """
                {"email": "%s", "codigo": "%s", "novaSenha": "%s"}""".formatted(EMAIL, CODIGO, NOVA_SENHA))
                .andExpect(status().isNoContent());

        login(SENHA).andExpect(status().isUnauthorized());
        login(NOVA_SENHA).andExpect(status().isOk());

        // O código só vale uma vez.
        postar("/auth/recuperar-senha/redefinir", """
                {"email": "%s", "codigo": "%s", "novaSenha": "OutraSenha789#"}""".formatted(EMAIL, CODIGO))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Código inválido ou expirado. Solicite um novo código."));
    }

    @Test
    void emailDesconhecidoRespondeIgualENaoEnvia() throws Exception {
        postar("/auth/recuperar-senha", "{\"email\": \"ninguem@unex.edu.br\"}").andExpect(status().isAccepted());
        postar("/auth/recuperar-senha", "{\"email\": \"nao-e-email\"}").andExpect(status().isAccepted());

        assertTrue(EmailFalso.ENVIADOS.isEmpty());
    }

    @Test
    void codigoErradoESenhaFraca() throws Exception {
        postar("/auth/recuperar-senha", "{\"email\": \"" + EMAIL + "\"}").andExpect(status().isAccepted());

        postar("/auth/recuperar-senha/validar", """
                {"email": "%s", "codigo": "000000"}""".formatted(EMAIL))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Código inválido ou expirado. Solicite um novo código."));
        postar("/auth/recuperar-senha/redefinir", """
                {"email": "%s", "codigo": "%s", "novaSenha": "fraca"}""".formatted(EMAIL, CODIGO))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("A senha deve ter no mínimo 8 caracteres."));

        login(SENHA).andExpect(status().isOk());
    }

    @Test
    void bloqueiaDepoisDeCincoErros() throws Exception {
        postar("/auth/recuperar-senha", "{\"email\": \"" + EMAIL + "\"}").andExpect(status().isAccepted());
        for (int i = 0; i < 5; i++) {
            postar("/auth/recuperar-senha/validar", """
                    {"email": "%s", "codigo": "111111"}""".formatted(EMAIL)).andExpect(status().isBadRequest());
        }

        postar("/auth/recuperar-senha/validar", """
                {"email": "%s", "codigo": "%s"}""".formatted(EMAIL, CODIGO)).andExpect(status().isBadRequest());
    }
}
