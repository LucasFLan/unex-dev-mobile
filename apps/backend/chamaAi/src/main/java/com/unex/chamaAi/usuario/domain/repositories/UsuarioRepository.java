package com.unex.chamaAi.usuario.domain.repositories;

import com.unex.chamaAi.usuario.domain.entities.Usuario;
import com.unex.chamaAi.usuario.domain.valueobjects.Cpf;
import com.unex.chamaAi.usuario.domain.valueobjects.Email;

import java.util.Optional;

public interface UsuarioRepository {

    boolean existePorCpf(Cpf cpf);

    boolean existePorEmail(Email email);

    Optional<Usuario> buscarPorCpf(String cpf);

    void salvar(Usuario usuario);
}
