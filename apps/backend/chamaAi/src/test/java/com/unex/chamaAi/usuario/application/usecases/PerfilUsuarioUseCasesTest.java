package com.unex.chamaAi.usuario.application.usecases;

import com.unex.chamaAi.shared.domain.exceptions.DadoInvalidoException;
import com.unex.chamaAi.usuario.application.dto.AlterarSenhaCommand;
import com.unex.chamaAi.usuario.application.dto.EditarPerfilCommand;
import com.unex.chamaAi.usuario.application.dto.PerfilUsuario;
import com.unex.chamaAi.usuario.domain.entities.Usuario;
import com.unex.chamaAi.usuario.domain.exceptions.SenhaAtualIncorretaException;
import com.unex.chamaAi.usuario.domain.exceptions.UsuarioNaoEncontradoException;
import com.unex.chamaAi.usuario.domain.valueobjects.Cpf;
import com.unex.chamaAi.usuario.domain.valueobjects.Email;
import com.unex.chamaAi.usuario.domain.valueobjects.SenhaHash;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PerfilUsuarioUseCasesTest {

    private static final String SENHA = "Senha123!";
    private static final String NOVA_SENHA = "NovaSenha456@";
    private static final LocalDate NASCIMENTO = LocalDate.of(2000, 1, 1);

    private final UsuarioRepositoryEmMemoria usuarios = new UsuarioRepositoryEmMemoria();
    private final CriptografadorSenhaFake criptografador = new CriptografadorSenhaFake();

    private final BuscarPerfilUsuario buscarPerfil = new BuscarPerfilUsuario(usuarios);
    private final EditarPerfilUsuario editarPerfil = new EditarPerfilUsuario(usuarios);
    private final AlterarSenhaUsuario alterarSenha = new AlterarSenhaUsuario(usuarios, criptografador);

    private Usuario usuario;

    @BeforeEach
    void cadastrarUsuario() {
        usuario = Usuario.criar("João", NASCIMENTO, new Cpf("529.982.247-25"), new Email("joao@unex.edu.br"),
                new SenhaHash("hash:" + SENHA), "71999999999");
        usuarios.salvar(usuario);
    }

    @Test
    void buscaPerfilSemExporSenha() {
        PerfilUsuario perfil = buscarPerfil.executar(usuario.getId());

        assertEquals(usuario.getId(), perfil.id());
        assertEquals("João", perfil.nome());
        assertEquals(NASCIMENTO, perfil.dataNascimento());
        assertEquals("52998224725", perfil.cpf());
        assertEquals("joao@unex.edu.br", perfil.email());
        assertEquals("71999999999", perfil.telefone());
        assertFalse(perfil.verificado());
        assertFalse(perfil.toString().contains(SENHA));
    }

    @Test
    void buscarUsuarioInexistenteLancaExcecao() {
        assertThrows(UsuarioNaoEncontradoException.class, () -> buscarPerfil.executar(UUID.randomUUID()));
    }

    @Test
    void editaPerfilESalva() {
        LocalDate novaData = LocalDate.of(1999, 5, 10);

        PerfilUsuario perfil = editarPerfil.executar(usuario.getId(),
                new EditarPerfilCommand("  João Silva ", novaData, "71888888888"));

        assertEquals("João Silva", perfil.nome());
        assertEquals(novaData, perfil.dataNascimento());
        assertEquals("71888888888", perfil.telefone());
        Usuario salvo = usuarios.buscarPorId(usuario.getId()).orElseThrow();
        assertEquals("João Silva", salvo.getNome());
        assertEquals(2, usuarios.quantidadeDeSalvamentos());
    }

    @Test
    void editarComDadoInvalidoNaoSalva() {
        assertThrows(DadoInvalidoException.class, () -> editarPerfil.executar(usuario.getId(),
                new EditarPerfilCommand(" ", NASCIMENTO, null)));
        assertEquals(1, usuarios.quantidadeDeSalvamentos());
    }

    @Test
    void editarUsuarioInexistenteLancaExcecao() {
        assertThrows(UsuarioNaoEncontradoException.class, () -> editarPerfil.executar(UUID.randomUUID(),
                new EditarPerfilCommand("João", NASCIMENTO, null)));
    }

    @Test
    void alteraSenhaQuandoSenhaAtualConfere() {
        alterarSenha.executar(usuario.getId(), new AlterarSenhaCommand(SENHA, NOVA_SENHA));

        assertEquals("hash:" + NOVA_SENHA, usuarios.buscarPorId(usuario.getId()).orElseThrow().getSenha().valor());
    }

    @Test
    void senhaAtualIncorretaNaoAltera() {
        assertThrows(SenhaAtualIncorretaException.class,
                () -> alterarSenha.executar(usuario.getId(), new AlterarSenhaCommand("Errada123!", NOVA_SENHA)));
        assertThrows(SenhaAtualIncorretaException.class,
                () -> alterarSenha.executar(usuario.getId(), new AlterarSenhaCommand("fraca", NOVA_SENHA)));
        assertEquals(1, usuarios.quantidadeDeSalvamentos());
    }

    @Test
    void novaSenhaFracaLancaDadoInvalido() {
        assertThrows(DadoInvalidoException.class,
                () -> alterarSenha.executar(usuario.getId(), new AlterarSenhaCommand(SENHA, "fraca")));
        assertEquals(1, usuarios.quantidadeDeSalvamentos());
    }

    @Test
    void senhaNaoApareceNoToStringDoComando() {
        assertFalse(new AlterarSenhaCommand(SENHA, NOVA_SENHA).toString().contains(SENHA));
    }
}
