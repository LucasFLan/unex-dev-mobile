package com.unex.chamaAi.usuario.domain.entities;

import com.unex.chamaAi.shared.domain.exceptions.DadoInvalidoException;
import com.unex.chamaAi.usuario.domain.exceptions.ContaJaVerificadaException;
import com.unex.chamaAi.usuario.domain.exceptions.StatusValidacaoInvalidoException;
import com.unex.chamaAi.usuario.domain.valueobjects.Cpf;
import com.unex.chamaAi.usuario.domain.valueobjects.Email;
import com.unex.chamaAi.usuario.domain.valueobjects.Papel;
import com.unex.chamaAi.usuario.domain.valueobjects.SenhaHash;
import com.unex.chamaAi.usuario.domain.valueobjects.StatusValidacao;
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
    void criarGeraIdNaoVerificadoEPendente() {
        Usuario usuario = novoUsuario();
        assertNotNull(usuario.getId());
        assertFalse(usuario.isVerificado());
        assertEquals(Papel.USUARIO, usuario.getPapel());
        assertFalse(usuario.isAdmin());
        assertEquals(StatusValidacao.PENDENTE, usuario.getStatusValidacao());
        assertFalse(usuario.isAprovado());
    }

    @Test
    void promoverParaAdminAlteraPapelEAprovaStatus() {
        Usuario usuario = novoUsuario();
        usuario.promoverParaAdmin();
        assertEquals(Papel.ADMIN, usuario.getPapel());
        assertTrue(usuario.isAdmin());
        assertEquals(StatusValidacao.APROVADO, usuario.getStatusValidacao());
        assertTrue(usuario.isAprovado());
    }

    @Test
    void aprovarAlteraStatusParaAprovado() {
        Usuario usuario = novoUsuario();
        usuario.aprovar();
        assertEquals(StatusValidacao.APROVADO, usuario.getStatusValidacao());
        assertTrue(usuario.isAprovado());
    }

    @Test
    void aprovarUsuarioJaAprovadoLancaExcecao() {
        Usuario usuario = novoUsuario();
        usuario.aprovar();
        assertThrows(StatusValidacaoInvalidoException.class, usuario::aprovar);
    }

    @Test
    void reprovarAlteraStatusParaReprovado() {
        Usuario usuario = novoUsuario();
        usuario.reprovar();
        assertEquals(StatusValidacao.REPROVADO, usuario.getStatusValidacao());
        assertFalse(usuario.isAprovado());
    }

    @Test
    void reprovarUsuarioJaReprovadoOuAprovadoLancaExcecao() {
        Usuario usuarioReprovado = novoUsuario();
        usuarioReprovado.reprovar();
        assertThrows(StatusValidacaoInvalidoException.class, usuarioReprovado::reprovar);

        Usuario usuarioAprovado = novoUsuario();
        usuarioAprovado.aprovar();
        assertThrows(StatusValidacaoInvalidoException.class, usuarioAprovado::reprovar);
    }

    @Test
    void papelEhObrigatorioAoReconstituir() {
        assertThrows(NullPointerException.class,
                () -> Usuario.reconstituir(UUID.randomUUID(), "João", NASCIMENTO, CPF, EMAIL, SENHA,
                        null, false, null, StatusValidacao.PENDENTE));
    }

    @Test
    void statusValidacaoEhObrigatorioAoReconstituir() {
        assertThrows(NullPointerException.class,
                () -> Usuario.reconstituir(UUID.randomUUID(), "João", NASCIMENTO, CPF, EMAIL, SENHA,
                        null, false, Papel.USUARIO, null));
    }

    @Test
    void reconstituirMantemEstado() {
        UUID id = UUID.randomUUID();
        Usuario usuario = Usuario.reconstituir(id, "João", NASCIMENTO, CPF, EMAIL, SENHA,
                null, true, Papel.ADMIN, StatusValidacao.APROVADO);
        assertEquals(id, usuario.getId());
        assertTrue(usuario.isVerificado());
        assertTrue(usuario.isAdmin());
        assertEquals(StatusValidacao.APROVADO, usuario.getStatusValidacao());
        assertTrue(usuario.isAprovado());
    }

    @Test
    void nomeEmBrancoLancaExcecao() {
        assertThrows(DadoInvalidoException.class,
                () -> Usuario.criar("  ", NASCIMENTO, CPF, EMAIL, SENHA, null));
        Usuario usuario = novoUsuario();
        assertThrows(DadoInvalidoException.class, () -> usuario.alterarNome(null));
    }

    @Test
    void nomeRespeitaTamanhoMaximo() {
        assertDoesNotThrow(() -> Usuario.criar("a".repeat(150), NASCIMENTO, CPF, EMAIL, SENHA, null));
        assertThrows(DadoInvalidoException.class,
                () -> Usuario.criar("a".repeat(151), NASCIMENTO, CPF, EMAIL, SENHA, null));
    }

    @Test
    void telefoneRespeitaTamanhoMaximo() {
        assertDoesNotThrow(() -> Usuario.criar("João", NASCIMENTO, CPF, EMAIL, SENHA, "1".repeat(20)));
        assertThrows(DadoInvalidoException.class,
                () -> Usuario.criar("João", NASCIMENTO, CPF, EMAIL, SENHA, "1".repeat(21)));
        Usuario usuario = novoUsuario();
        assertThrows(DadoInvalidoException.class, () -> usuario.alterarTelefone("1".repeat(21)));
    }

    @Test
    void camposObrigatoriosNulosLancamExcecao() {
        assertThrows(NullPointerException.class, () -> Usuario.criar("João", NASCIMENTO, null, EMAIL, SENHA, null));
        assertThrows(NullPointerException.class, () -> Usuario.criar("João", NASCIMENTO, CPF, null, SENHA, null));
        assertThrows(NullPointerException.class, () -> Usuario.criar("João", NASCIMENTO, CPF, EMAIL, null, null));
        assertThrows(DadoInvalidoException.class, () -> Usuario.criar("João", null, CPF, EMAIL, SENHA, null));
    }

    @Test
    void dataNascimentoNoFuturoLancaExcecao() {
        assertThrows(DadoInvalidoException.class,
                () -> Usuario.criar("João", LocalDate.now().plusDays(1), CPF, EMAIL, SENHA, null));
    }

    @Test
    void verificarContaDuasVezesLancaExcecao() {
        Usuario usuario = novoUsuario();
        usuario.verificarConta();
        assertTrue(usuario.isVerificado());
        assertThrows(ContaJaVerificadaException.class, usuario::verificarConta);
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
        Usuario a = Usuario.reconstituir(id, "João", NASCIMENTO, CPF, EMAIL, SENHA,
                null, false, Papel.USUARIO, StatusValidacao.PENDENTE);
        Usuario b = Usuario.reconstituir(id, "Outro", NASCIMENTO, CPF, EMAIL, SENHA,
                null, true, Papel.USUARIO, StatusValidacao.APROVADO);
        assertEquals(a, b);
        assertNotEquals(a, novoUsuario());
    }
}