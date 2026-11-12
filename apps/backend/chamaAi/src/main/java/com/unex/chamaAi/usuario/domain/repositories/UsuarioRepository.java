package com.unex.chamaAi.usuario.domain.repositories;

import com.unex.chamaAi.usuario.domain.entities.Usuario;
import com.unex.chamaAi.usuario.domain.valueobjects.Cpf;
import com.unex.chamaAi.usuario.domain.valueobjects.Email;

import java.util.Optional;
import java.util.UUID;

public interface UsuarioRepository {

    boolean existePorCpf(Cpf cpf);

    boolean existePorEmail(Email email);

    Optional<Usuario> buscarPorId(UUID id);

    Optional<Usuario> buscarPorCpf(String cpf);

    Optional<Usuario> buscarPorEmail(Email email);

    void salvar(Usuario usuario);
}
