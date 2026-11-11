package com.unex.chamaAi.usuario.application.usecases;

import com.unex.chamaAi.usuario.domain.entities.Usuario;
import com.unex.chamaAi.usuario.domain.exceptions.UsuarioNaoEncontradoException;
import com.unex.chamaAi.usuario.domain.valueobjects.Cpf;
import com.unex.chamaAi.usuario.domain.valueobjects.Email;
import com.unex.chamaAi.usuario.domain.valueobjects.SenhaHash;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class PromoverUsuarioParaAdminTest {

    private static final Email EMAIL = new Email("joao@unex.edu.br");

    private final UsuarioRepositoryEmMemoria usuarios = new UsuarioRepositoryEmMemoria();
    private final PromoverUsuarioParaAdmin promover = new PromoverUsuarioParaAdmin(usuarios);

    @Test
    void promoveUsuarioUmaUnicaVez() {
        Usuario usuario = Usuario.criar("João", LocalDate.of(2000, 1, 1), new Cpf("529.982.247-25"), EMAIL,
                new SenhaHash("hash"), null);
        usuarios.salvar(usuario);

        assertTrue(promover.executar(EMAIL));
        assertTrue(usuarios.buscarPorId(usuario.getId()).orElseThrow().isAdmin());

        assertFalse(promover.executar(EMAIL));
        assertEquals(2, usuarios.quantidadeDeSalvamentos());
    }

    @Test
    void emailNaoCadastradoLancaExcecao() {
        assertThrows(UsuarioNaoEncontradoException.class, () -> promover.executar(EMAIL));
    }
}
