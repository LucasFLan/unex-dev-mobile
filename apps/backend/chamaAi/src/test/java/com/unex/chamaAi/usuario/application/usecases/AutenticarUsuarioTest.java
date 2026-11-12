package com.unex.chamaAi.usuario.application.usecases;

import com.unex.chamaAi.usuario.application.dto.LoginCommand;
import com.unex.chamaAi.usuario.application.dto.TokenAcesso;
import com.unex.chamaAi.usuario.application.ports.EmissorToken;
import com.unex.chamaAi.usuario.domain.entities.Usuario;
import com.unex.chamaAi.usuario.domain.exceptions.CredenciaisInvalidasException;
import com.unex.chamaAi.usuario.domain.valueobjects.Cpf;
import com.unex.chamaAi.usuario.domain.valueobjects.Email;
import com.unex.chamaAi.usuario.domain.valueobjects.SenhaHash;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AutenticarUsuarioTest {

    private static final String EMAIL = "joao@unex.edu.br";
    private static final String SENHA = "Senha123!";

    private final UsuarioRepositoryEmMemoria usuarios = new UsuarioRepositoryEmMemoria();
    private final List<Usuario> tokensEmitidos = new ArrayList<>();
    private final EmissorToken emissor = usuario -> {
        tokensEmitidos.add(usuario);
        return new TokenAcesso("token-" + usuario.getId(), "Bearer", 3600);
    };
    private final AutenticarUsuario autenticar =
            new AutenticarUsuario(usuarios, new CriptografadorSenhaFake(), emissor);

    private Usuario usuario;

    @BeforeEach
    void cadastrar() {
        usuario = Usuario.criar("João", LocalDate.of(2000, 1, 1), new Cpf("529.982.247-25"), new Email(EMAIL),
                new SenhaHash("hash:" + SENHA), null);
        usuarios.salvar(usuario);
    }

    @Test
    void credenciaisCorretasEmitemToken() {
        TokenAcesso token = autenticar.executar(new LoginCommand(" JOAO@unex.edu.br ", SENHA));

        assertEquals("token-" + usuario.getId(), token.accessToken());
        assertEquals(List.of(usuario), tokensEmitidos);
    }

    @Test
    void credenciaisInvalidasNuncaEmitemToken() {
        List<LoginCommand> invalidos = List.of(
                new LoginCommand(EMAIL, "Errada123!"),
                new LoginCommand("outro@unex.edu.br", SENHA),
                new LoginCommand("nao-e-email", SENHA),
                new LoginCommand(EMAIL, "fraca"),
                new LoginCommand(null, null));

        invalidos.forEach(invalido ->
                assertThrows(CredenciaisInvalidasException.class, () -> autenticar.executar(invalido)));
        assertTrue(tokensEmitidos.isEmpty());
    }

    @Test
    void mensagemNaoRevelaSeOEmailExiste() {
        String senhaErrada = assertThrows(CredenciaisInvalidasException.class,
                () -> autenticar.executar(new LoginCommand(EMAIL, "Errada123!"))).getMessage();
        String emailInexistente = assertThrows(CredenciaisInvalidasException.class,
                () -> autenticar.executar(new LoginCommand("outro@unex.edu.br", SENHA))).getMessage();

        assertEquals(senhaErrada, emailInexistente);
    }

    @Test
    void senhaNaoApareceNoToString() {
        assertFalse(new LoginCommand(EMAIL, SENHA).toString().contains(SENHA));
        assertFalse(new TokenAcesso("segredo", "Bearer", 1).toString().contains("segredo"));
    }
}
