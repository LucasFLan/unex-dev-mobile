package com.unex.chamaAi.usuario.application.usecases;

import com.unex.chamaAi.usuario.application.dto.PerfilUsuario;
import com.unex.chamaAi.usuario.domain.entities.Usuario;
import com.unex.chamaAi.usuario.domain.exceptions.FotoPerfilNaoEncontradaException;
import com.unex.chamaAi.usuario.domain.exceptions.UsuarioNaoEncontradoException;
import com.unex.chamaAi.usuario.domain.repositories.FotoPerfilRepository;
import com.unex.chamaAi.usuario.domain.repositories.UsuarioRepository;
import com.unex.chamaAi.usuario.domain.valueobjects.FotoPerfil;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

/** Enviar, trocar, remover e ver a foto de perfil. Cada usuário tem no máximo uma foto. */
public class GerenciarFotoPerfil {

    private final UsuarioRepository usuarios;
    private final FotoPerfilRepository fotos;
    private final Clock relogio;

    public GerenciarFotoPerfil(UsuarioRepository usuarios, FotoPerfilRepository fotos, Clock relogio) {
        this.usuarios = usuarios;
        this.fotos = fotos;
        this.relogio = relogio;
    }

    /** Grava a nova foto (substituindo a anterior) e devolve o perfil atualizado. */
    public PerfilUsuario alterar(UUID usuarioId, byte[] conteudo) {
        Usuario usuario = buscarUsuario(usuarioId);
        FotoPerfil foto = FotoPerfil.de(conteudo);

        fotos.gravar(usuarioId, foto);
        usuario.registrarFoto(Instant.now(relogio));
        usuarios.salvar(usuario);
        return PerfilUsuario.de(usuario);
    }

    /** Tira a foto do perfil. Sem foto, não faz nada. */
    public PerfilUsuario remover(UUID usuarioId) {
        Usuario usuario = buscarUsuario(usuarioId);
        if (usuario.temFoto()) {
            fotos.remover(usuarioId);
            usuario.removerFoto();
            usuarios.salvar(usuario);
        }
        return PerfilUsuario.de(usuario);
    }

    /** Foto de qualquer usuário (aparece nos anúncios e nas propostas). */
    public FotoPerfil buscar(UUID usuarioId) {
        Usuario usuario = buscarUsuario(usuarioId);
        if (!usuario.temFoto()) {
            throw new FotoPerfilNaoEncontradaException();
        }
        return fotos.buscar(usuarioId).orElseThrow(FotoPerfilNaoEncontradaException::new);
    }

    private Usuario buscarUsuario(UUID id) {
        return usuarios.buscarPorId(id).orElseThrow(UsuarioNaoEncontradoException::new);
    }
}
