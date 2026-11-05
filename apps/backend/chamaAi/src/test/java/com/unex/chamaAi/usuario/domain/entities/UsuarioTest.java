package com.unex.chamaAi.usuario.domain.entities;

import com.unex.chamaAi.usuario.domain.valueobjects.Cpf;
import com.unex.chamaAi.usuario.domain.valueobjects.Email;
import com.unex.chamaAi.usuario.domain.valueobjects.SenhaHash;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioTest {

    private static final Cpf CPF = new Cpf("529.982.247-25");
    private static final Email EMAIL = new Email("joao@unex.edu.br");
    private static final SenhaHash SENHA = new SenhaHash("$2a$10$hash");
    private static final LocalDate NASCIMENTO = LocalDate.of(2000, 1, 1);

    private Usuario novoUsuario() {
        return Usuario.criar("João", NASCIMENTO, CPF, EMAIL, SENHA, "71999999999");
    }

    @Test
    void criarGeraIdENaoVerificado() {
        Usuario usuario = novoUsuario();
        assertNotNull(usuario.getId());
        assertFalse(usuario.isVerificado());
    }

    @Test
    void reconstituirMantemEstado() {
        UUID id = UUID.randomUUID();
        Usuario usuario = Usuario.reconstituir(id, "João", NASCIMENTO, CPF, EMAIL, SENHA, null, true);
        assertEquals(id, usuario.getId());
        assertTrue(usuario.isVerificado());
    }

    @Test
    void nomeEmBrancoLancaExcecao() {
        assertThrows(IllegalArgumentException.class,
                () -> Usuario.criar("  ", NASCIMENTO, CPF, EMAIL, SENHA, null));
        Usuario usuario = novoUsuario();
        assertThrows(IllegalArgumentException.class, () -> usuario.alterarNome(null));
    }

    @Test
    void camposObrigatoriosNulosLancamExcecao() {
        assertThrows(NullPointerException.class, () -> Usuario.criar("João", NASCIMENTO, null, EMAIL, SENHA, null));
        assertThrows(NullPointerException.class, () -> Usuario.criar("João", NASCIMENTO, CPF, null, SENHA, null));
        assertThrows(NullPointerException.class, () -> Usuario.criar("João", NASCIMENTO, CPF, EMAIL, null, null));
        assertThrows(NullPointerException.class, () -> Usuario.criar("João", null, CPF, EMAIL, SENHA, null));
    }

    @Test
    void dataNascimentoNoFuturoLancaExcecao() {
        assertThrows(IllegalArgumentException.class,
                () -> Usuario.criar("João", LocalDate.now().plusDays(1), CPF, EMAIL, SENHA, null));
    }

    @Test
    void verificarContaDuasVezesLancaExcecao() {
        Usuario usuario = novoUsuario();
        usuario.verificarConta();
        assertTrue(usuario.isVerificado());
        assertThrows(IllegalStateException.class, usuario::verificarConta);
    }

    @Test
    void atualizarEmailRemoveVerificacao() {
        Usuario usuario = novoUsuario();
        usuario.verificarConta();
        usuario.atualizarEmail(new Email("novo@unex.edu.br"));
        assertEquals("novo@unex.edu.br", usuario.getEmail().valor());
        assertFalse(usuario.isVerificado());
    }

    @Test
    void atualizarParaMesmoEmailMantemVerificacao() {
        Usuario usuario = novoUsuario();
        usuario.verificarConta();
        usuario.atualizarEmail(new Email("JOAO@unex.edu.br"));
        assertTrue(usuario.isVerificado());
    }

    @Test
    void igualdadePorId() {
        UUID id = UUID.randomUUID();
        Usuario a = Usuario.reconstituir(id, "João", NASCIMENTO, CPF, EMAIL, SENHA, null, false);
        Usuario b = Usuario.reconstituir(id, "Outro", NASCIMENTO, CPF, EMAIL, SENHA, null, true);
        assertEquals(a, b);
        assertNotEquals(a, novoUsuario());
    }
}
