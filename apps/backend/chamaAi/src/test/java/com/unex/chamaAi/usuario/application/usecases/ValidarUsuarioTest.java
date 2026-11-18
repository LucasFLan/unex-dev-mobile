package com.unex.chamaAi.usuario.application.usecases;

import com.unex.chamaAi.usuario.domain.entities.Usuario;
import com.unex.chamaAi.usuario.domain.exceptions.StatusValidacaoInvalidoException;
import com.unex.chamaAi.usuario.domain.exceptions.UsuarioNaoEncontradoException;
import com.unex.chamaAi.usuario.domain.valueobjects.Cpf;
import com.unex.chamaAi.usuario.domain.valueobjects.Email;
import com.unex.chamaAi.usuario.domain.valueobjects.SenhaHash;
import com.unex.chamaAi.usuario.domain.valueobjects.StatusValidacao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ValidarUsuarioTest {

    private UsuarioRepositoryEmMemoria repositorio;
    private AprovarUsuario aprovarUsuario;
    private ReprovarUsuario reprovarUsuario;

    @BeforeEach
    void setUp() {
        repositorio = new UsuarioRepositoryEmMemoria();
        aprovarUsuario = new AprovarUsuario(repositorio);
        reprovarUsuario = new ReprovarUsuario(repositorio);
    }

    @Test
    void deveAprovarUsuarioPendente() {
        Usuario usuario = novoUsuario();
        repositorio.salvar(usuario);

        aprovarUsuario.executar(usuario.getId());

        Usuario salvo = repositorio.buscarPorId(usuario.getId()).orElseThrow();
        assertEquals(StatusValidacao.APROVADO, salvo.getStatusValidacao());
    }

    @Test
    void deveReprovarUsuarioPendente() {
        Usuario usuario = novoUsuario();
        repositorio.salvar(usuario);

        reprovarUsuario.executar(usuario.getId());

        Usuario salvo = repositorio.buscarPorId(usuario.getId()).orElseThrow();
        assertEquals(StatusValidacao.REPROVADO, salvo.getStatusValidacao());
    }

    @Test
    void naoDeveAprovarUsuarioJaAprovado() {
        Usuario usuario = novoUsuario();
        usuario.aprovar();
        repositorio.salvar(usuario);

        assertThrows(StatusValidacaoInvalidoException.class,
                () -> aprovarUsuario.executar(usuario.getId()));
    }

    @Test
    void naoDeveReprovarUsuarioJaAprovado() {
        Usuario usuario = novoUsuario();
        usuario.aprovar();
        repositorio.salvar(usuario);

        assertThrows(StatusValidacaoInvalidoException.class,
                () -> reprovarUsuario.executar(usuario.getId()));
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoExistir() {
        UUID idInexistente = UUID.randomUUID();
        assertThrows(UsuarioNaoEncontradoException.class, () -> aprovarUsuario.executar(idInexistente));
        assertThrows(UsuarioNaoEncontradoException.class, () -> reprovarUsuario.executar(idInexistente));
    }

    private static Usuario novoUsuario() {
        return Usuario.criar("Maria Silva", LocalDate.of(1998, 5, 10),
                new Cpf("52998224725"), new Email("maria@unex.edu.br"),
                new SenhaHash("$2a$10$hashDeTesteValidoParaDominio123456"), "71999999999");
    }
}