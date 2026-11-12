package com.unex.chamaAi.usuario.application.usecases;

import com.unex.chamaAi.usuario.domain.entities.Usuario;
import com.unex.chamaAi.usuario.domain.exceptions.UsuarioNaoEncontradoException;
import com.unex.chamaAi.usuario.domain.repositories.UsuarioRepository;
import com.unex.chamaAi.usuario.domain.valueobjects.Email;

public class PromoverUsuarioParaAdmin {

    private final UsuarioRepository usuarios;

    public PromoverUsuarioParaAdmin(UsuarioRepository usuarios) {
        this.usuarios = usuarios;
    }

    public boolean executar(Email email) {
        Usuario usuario = usuarios.buscarPorEmail(email).orElseThrow(UsuarioNaoEncontradoException::new);
        if (usuario.isAdmin()) {
            return false;
        }
        usuario.promoverParaAdmin();
        usuarios.salvar(usuario);
        return true;
    }
}
