package com.unex.chamaAi.usuario.application.usecases;

import com.unex.chamaAi.usuario.application.dto.CadastrarUsuarioCommand;
import com.unex.chamaAi.usuario.application.dto.UsuarioCadastrado;
import com.unex.chamaAi.shared.domain.exceptions.DadoInvalidoException;
import com.unex.chamaAi.usuario.domain.entities.Usuario;
import com.unex.chamaAi.usuario.domain.exceptions.CpfJaCadastradoException;
import com.unex.chamaAi.usuario.domain.exceptions.EmailJaCadastradoException;
import com.unex.chamaAi.usuario.domain.repositories.UsuarioRepository;
import com.unex.chamaAi.usuario.domain.services.CriptografadorSenha;
import com.unex.chamaAi.usuario.domain.valueobjects.Cpf;
import com.unex.chamaAi.usuario.domain.valueobjects.Email;
import com.unex.chamaAi.usuario.domain.valueobjects.Senha;
import com.unex.chamaAi.usuario.domain.valueobjects.SenhaHash;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CadastrarUsuarioTest {

    private static final String CPF = "529.982.247-25";
    private static final String OUTRO_CPF = "111.444.777-35";
    private static final String EMAIL = "joao@unex.edu.br";
    private static final String OUTRO_EMAIL = "maria@unex.edu.br";
    private static final String SENHA = "Senha123!";
    private static final LocalDate NASCIMENTO = LocalDate.of(2000, 1, 1);

    private final UsuarioRepositoryEmMemoria usuarios = new UsuarioRepositoryEmMemoria();
    private final CadastrarUsuario cadastrarUsuario =
            new CadastrarUsuario(usuarios, new CriptografadorSenhaFake());

    private CadastrarUsuarioCommand comando(String cpf, String email) {
        return new CadastrarUsuarioCommand("João", NASCIMENTO, cpf, email, SENHA, "71999999999");
    }

    @Test
    void cadastraUsuarioNaoVerificado() {
        UsuarioCadastrado resultado = cadastrarUsuario.executar(comando(CPF, EMAIL));

        assertEquals(1, usuarios.salvos.size());
        Usuario salvo = usuarios.salvos.getFirst();
        assertEquals(salvo.getId(), resultado.id());
        assertEquals("João", resultado.nome());
        assertEquals(EMAIL, resultado.email());
        assertFalse(salvo.isVerificado());
    }

    @Test
    void senhaEhSalvaCriptografada() {
        cadastrarUsuario.executar(comando(CPF, EMAIL));

        assertEquals("hash:" + SENHA, usuarios.salvos.getFirst().getSenha().valor());
    }

    @Test
    void cpfJaCadastradoLancaExcecao() {
        cadastrarUsuario.executar(comando(CPF, EMAIL));

        assertThrows(CpfJaCadastradoException.class,
                () -> cadastrarUsuario.executar(comando("52998224725", OUTRO_EMAIL)));
        assertEquals(1, usuarios.salvos.size());
    }

    @Test
    void emailJaCadastradoLancaExcecao() {
        cadastrarUsuario.executar(comando(CPF, EMAIL));

        assertThrows(EmailJaCadastradoException.class,
                () -> cadastrarUsuario.executar(comando(OUTRO_CPF, "JOAO@unex.edu.br")));
        assertEquals(1, usuarios.salvos.size());
    }

    @Test
    void dadosInvalidosLancamExcecaoENaoSalvam() {
        List<CadastrarUsuarioCommand> invalidos = List.of(
                comando("123", EMAIL),
                comando(CPF, "joao"),
                new CadastrarUsuarioCommand("João", NASCIMENTO, CPF, EMAIL, "fraca", null),
                new CadastrarUsuarioCommand(" ", NASCIMENTO, CPF, EMAIL, SENHA, null),
                new CadastrarUsuarioCommand("João", null, CPF, EMAIL, SENHA, null),
                new CadastrarUsuarioCommand("João", LocalDate.now().plusDays(1), CPF, EMAIL, SENHA, null));

        invalidos.forEach(invalido ->
                assertThrows(DadoInvalidoException.class, () -> cadastrarUsuario.executar(invalido)));
        assertTrue(usuarios.salvos.isEmpty());
    }

    @Test
    void senhaNaoApareceNoToStringDoComando() {
        assertFalse(comando(CPF, EMAIL).toString().contains(SENHA));
    }

    private static class UsuarioRepositoryEmMemoria implements UsuarioRepository {

        private final List<Usuario> salvos = new ArrayList<>();

        @Override
        public boolean existePorCpf(Cpf cpf) {
            return salvos.stream().anyMatch(usuario -> usuario.getCpf().equals(cpf));
        }

        @Override
        public boolean existePorEmail(Email email) {
            return salvos.stream().anyMatch(usuario -> usuario.getEmail().equals(email));
        }

        @Override
        public Optional<Usuario> buscarPorCpf(String cpf) {
            return salvos.stream().filter(usuario -> usuario.getCpf().valor().equals(cpf)).findFirst();
        }

        @Override
        public void salvar(Usuario usuario) {
            salvos.add(usuario);
        }
    }

    private static class CriptografadorSenhaFake implements CriptografadorSenha {

        @Override
        public SenhaHash criptografar(Senha senha) {
            return new SenhaHash("hash:" + senha.valor());
        }

        @Override
        public boolean confere(Senha senha, SenhaHash hash) {
            return hash.valor().equals("hash:" + senha.valor());
        }
    }
}
