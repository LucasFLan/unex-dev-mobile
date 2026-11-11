package com.unex.chamaAi.usuario.infrastructure.persistence;

import com.unex.chamaAi.usuario.domain.entities.Usuario;
import com.unex.chamaAi.usuario.domain.exceptions.CpfJaCadastradoException;
import com.unex.chamaAi.usuario.domain.exceptions.EmailJaCadastradoException;
import com.unex.chamaAi.usuario.domain.repositories.UsuarioRepository;
import com.unex.chamaAi.usuario.domain.valueobjects.Cpf;
import com.unex.chamaAi.usuario.domain.valueobjects.Email;
import com.unex.chamaAi.usuario.domain.valueobjects.Papel;
import com.unex.chamaAi.usuario.domain.valueobjects.SenhaHash;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class UsuarioRepositoryJpa implements UsuarioRepository {

    private static final String CONSTRAINT_CPF = "uk_usuarios_cpf";
    private static final String CONSTRAINT_EMAIL = "uk_usuarios_email";

    private final UsuarioSpringDataRepository springData;

    public UsuarioRepositoryJpa(UsuarioSpringDataRepository springData) {
        this.springData = springData;
    }

    @Override
    public boolean existePorCpf(Cpf cpf) {
        return springData.existsByCpf(cpf.valor());
    }

    @Override
    public boolean existePorEmail(Email email) {
        return springData.existsByEmail(email.valor());
    }

    @Override
    public Optional<Usuario> buscarPorId(UUID id) {
        return springData.findById(id).map(UsuarioRepositoryJpa::paraDominio);
    }

    @Override
    public Optional<Usuario> buscarPorCpf(String cpf) {
        return springData.findByCpf(new Cpf(cpf).valor()).map(UsuarioRepositoryJpa::paraDominio);
    }

    @Override
    public Optional<Usuario> buscarPorEmail(Email email) {
        return springData.findByEmail(email.valor()).map(UsuarioRepositoryJpa::paraDominio);
    }

    @Override
    public void salvar(Usuario usuario) {
        try {
            springData.saveAndFlush(paraEntidade(usuario));
        } catch (DataIntegrityViolationException e) {
            String causa = e.getMostSpecificCause().getMessage();
            if (causa.contains(CONSTRAINT_CPF)) {
                throw new CpfJaCadastradoException();
            }
            if (causa.contains(CONSTRAINT_EMAIL)) {
                throw new EmailJaCadastradoException();
            }
            throw e;
        }
    }

    private static UsuarioJpaEntity paraEntidade(Usuario usuario) {
        return new UsuarioJpaEntity(usuario.getId(), usuario.getNome(), usuario.getDataNascimento(),
                usuario.getCpf().valor(), usuario.getEmail().valor(), usuario.getSenha().valor(),
                usuario.getTelefone(), usuario.isVerificado(), usuario.getPapel().name());
    }

    private static Usuario paraDominio(UsuarioJpaEntity entidade) {
        return Usuario.reconstituir(entidade.getId(), entidade.getNome(), entidade.getDataNascimento(),
                new Cpf(entidade.getCpf()), new Email(entidade.getEmail()), new SenhaHash(entidade.getSenhaHash()),
                entidade.getTelefone(), entidade.isVerificado(), Papel.valueOf(entidade.getPapel()));
    }
}
