package com.unex.chamaAi.usuario.infrastructure.persistence;

import com.unex.chamaAi.TestcontainersConfiguration;
import com.unex.chamaAi.usuario.domain.entities.Usuario;
import com.unex.chamaAi.usuario.domain.exceptions.CpfJaCadastradoException;
import com.unex.chamaAi.usuario.domain.exceptions.EmailJaCadastradoException;
import com.unex.chamaAi.usuario.domain.valueobjects.Cpf;
import com.unex.chamaAi.usuario.domain.valueobjects.Email;
import com.unex.chamaAi.usuario.domain.valueobjects.SenhaHash;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({TestcontainersConfiguration.class, UsuarioRepositoryJpa.class})
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class UsuarioRepositoryJpaTest {

    private static final Cpf CPF = new Cpf("529.982.247-25");
    private static final Cpf OUTRO_CPF = new Cpf("111.444.777-35");
    private static final Email EMAIL = new Email("joao@unex.edu.br");
    private static final Email OUTRO_EMAIL = new Email("maria@unex.edu.br");
    private static final SenhaHash SENHA = new SenhaHash("$2a$10$hash");
    private static final LocalDate NASCIMENTO = LocalDate.of(2000, 1, 1);

    @Autowired
    private UsuarioRepositoryJpa repositorio;

    @Autowired
    private UsuarioSpringDataRepository springData;

    private Usuario novoUsuario(Cpf cpf, Email email) {
        return Usuario.criar("João", NASCIMENTO, cpf, email, SENHA, "71999999999");
    }

    @BeforeEach
    void limparBanco() {
        springData.deleteAll();
    }

    @Test
    void salvarPersisteTodosOsCampos() {
        Usuario usuario = novoUsuario(CPF, EMAIL);

        repositorio.salvar(usuario);

        UsuarioJpaEntity salvo = springData.findById(usuario.getId()).orElseThrow();
        assertEquals("João", salvo.getNome());
        assertEquals(NASCIMENTO, salvo.getDataNascimento());
        assertEquals(CPF.valor(), salvo.getCpf());
        assertEquals(EMAIL.valor(), salvo.getEmail());
        assertEquals(SENHA.valor(), salvo.getSenhaHash());
        assertEquals("71999999999", salvo.getTelefone());
        assertFalse(salvo.isVerificado());
    }

    @Test
    void salvarUsuarioExistenteAtualiza() {
        Usuario usuario = novoUsuario(CPF, EMAIL);
        repositorio.salvar(usuario);

        usuario.alterarNome("João Silva");
        usuario.verificarConta();
        repositorio.salvar(usuario);

        assertEquals(1, springData.count());
        UsuarioJpaEntity salvo = springData.findById(usuario.getId()).orElseThrow();
        assertEquals("João Silva", salvo.getNome());
        assertTrue(salvo.isVerificado());
    }

    @Test
    void existeConsideraCpfEEmailSalvos() {
        repositorio.salvar(novoUsuario(CPF, EMAIL));

        assertTrue(repositorio.existePorCpf(CPF));
        assertTrue(repositorio.existePorEmail(EMAIL));
        assertFalse(repositorio.existePorCpf(OUTRO_CPF));
        assertFalse(repositorio.existePorEmail(OUTRO_EMAIL));
    }

    @Test
    void cpfDuplicadoNoBancoViraExcecaoDeDominio() {
        repositorio.salvar(novoUsuario(CPF, EMAIL));

        assertThrows(CpfJaCadastradoException.class,
                () -> repositorio.salvar(novoUsuario(CPF, OUTRO_EMAIL)));
        assertEquals(1, springData.count());
    }

    @Test
    void emailDuplicadoNoBancoViraExcecaoDeDominio() {
        repositorio.salvar(novoUsuario(CPF, EMAIL));

        assertThrows(EmailJaCadastradoException.class,
                () -> repositorio.salvar(novoUsuario(OUTRO_CPF, EMAIL)));
        assertEquals(1, springData.count());
    }
}
