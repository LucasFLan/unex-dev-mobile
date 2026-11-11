package com.unex.chamaAi.usuario.application.usecases;

import com.unex.chamaAi.usuario.domain.entities.Usuario;
import com.unex.chamaAi.usuario.domain.repositories.UsuarioRepository;
import com.unex.chamaAi.usuario.domain.valueobjects.Cpf;
import com.unex.chamaAi.usuario.domain.valueobjects.Email;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

class UsuarioRepositoryEmMemoria implements UsuarioRepository {

    private final Map<UUID, Usuario> usuarios = new LinkedHashMap<>();
    private int quantidadeDeSalvamentos;

    List<Usuario> salvos() {
        return new ArrayList<>(usuarios.values());
    }

    int quantidadeDeSalvamentos() {
        return quantidadeDeSalvamentos;
    }

    @Override
    public boolean existePorCpf(Cpf cpf) {
        return usuarios.values().stream().anyMatch(usuario -> usuario.getCpf().equals(cpf));
    }

    @Override
    public boolean existePorEmail(Email email) {
        return usuarios.values().stream().anyMatch(usuario -> usuario.getEmail().equals(email));
    }

    @Override
    public Optional<Usuario> buscarPorId(UUID id) {
        return Optional.ofNullable(usuarios.get(id));
    }

    @Override
    public Optional<Usuario> buscarPorCpf(String cpf) {
        return usuarios.values().stream().filter(usuario -> usuario.getCpf().valor().equals(cpf)).findFirst();
    }

    @Override
    public void salvar(Usuario usuario) {
        usuarios.put(usuario.getId(), usuario);
        quantidadeDeSalvamentos++;
    }
}
