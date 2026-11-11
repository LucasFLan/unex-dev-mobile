package com.unex.chamaAi.usuario.application.usecases;

import com.unex.chamaAi.usuario.application.dto.PerfilUsuario;
import com.unex.chamaAi.usuario.domain.exceptions.UsuarioNaoEncontradoException;
import com.unex.chamaAi.usuario.domain.repositories.UsuarioRepository;

import java.util.UUID;

public class BuscarPerfilUsuario {

    private final UsuarioRepository usuarios;

    public BuscarPerfilUsuario(UsuarioRepository usuarios) {
        this.usuarios = usuarios;
    }

    public PerfilUsuario executar(UUID id) {
        return usuarios.buscarPorId(id)
                .map(PerfilUsuario::de)
                .orElseThrow(UsuarioNaoEncontradoException::new);
    }
}
