package com.unex.chamaAi.usuario.application.usecases;

import com.unex.chamaAi.usuario.domain.entities.Usuario;
import com.unex.chamaAi.usuario.domain.exceptions.UsuarioNaoEncontradoException;
import com.unex.chamaAi.usuario.domain.repositories.UsuarioRepository;
import com.unex.chamaAi.usuario.domain.valueobjects.Cpf;

public class BuscarUsuarioPorCpf {
    private final UsuarioRepository usuarios;

    public BuscarUsuarioPorCpf(UsuarioRepository usuarios) {
        this.usuarios = usuarios;
    }

    public Usuario executar(Cpf cpf) {
        boolean usuarioExiste = usuarios.existePorCpf(cpf);
        if (!usuarioExiste) {
            throw new UsuarioNaoEncontradoException("Usuario nao encontrado");
        }

        return usuarios.buscarPorCpf(cpf.valor()).orElseThrow(() -> new UsuarioNaoEncontradoException("Usuario nao encontrado"));
    }
}
