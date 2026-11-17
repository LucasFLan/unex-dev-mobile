package com.unex.chamaAi.usuario.application.usecases;

import com.unex.chamaAi.usuario.domain.entities.Usuario;
import com.unex.chamaAi.usuario.domain.exceptions.UsuarioNaoEncontradoException;
import com.unex.chamaAi.usuario.domain.repositories.UsuarioRepository;

import java.util.UUID;

public class ReprovarUsuario {

    private final UsuarioRepository usuarios;

    public ReprovarUsuario(UsuarioRepository usuarios) {
        this.usuarios = usuarios;
    }

    public void executar(UUID usuarioId) {
        Usuario usuario = usuarios.buscarPorId(usuarioId)
                .orElseThrow(UsuarioNaoEncontradoException::new);
        usuario.reprovar();
        usuarios.salvar(usuario);
    }
}