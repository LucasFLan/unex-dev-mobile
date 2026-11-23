package com.unex.chamaAi.usuario.application.usecases;

import com.unex.chamaAi.usuario.application.dto.FotoPerfilUrl;
import com.unex.chamaAi.usuario.application.dto.PerfilUsuario;
import com.unex.chamaAi.usuario.domain.repositories.FotoPerfilRepository;
import com.unex.chamaAi.usuario.domain.exceptions.UsuarioNaoEncontradoException;
import com.unex.chamaAi.usuario.domain.repositories.UsuarioRepository;

import java.util.UUID;

public class BuscarPerfilUsuario {

    private final UsuarioRepository usuarios;

    private final FotoPerfilRepository fotos;

    public BuscarPerfilUsuario(UsuarioRepository usuarios) {
        this(usuarios, null);
    }

    public BuscarPerfilUsuario(UsuarioRepository usuarios, FotoPerfilRepository fotos) {
        this.usuarios = usuarios;
        this.fotos = fotos;
    }

    private String fotoDe(UUID usuarioId) {
        return fotos == null ? null : fotos.versao(usuarioId).map(v -> FotoPerfilUrl.de(usuarioId, v)).orElse(null);
    }

    public PerfilUsuario executar(UUID id) {
        return usuarios.buscarPorId(id)
                .map(usuario -> PerfilUsuario.de(usuario, fotoDe(id)))
                .orElseThrow(UsuarioNaoEncontradoException::new);
    }
}
