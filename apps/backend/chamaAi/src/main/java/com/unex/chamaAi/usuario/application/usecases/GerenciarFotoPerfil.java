package com.unex.chamaAi.usuario.application.usecases;

import com.unex.chamaAi.shared.domain.valueobjects.Imagem;
import com.unex.chamaAi.usuario.application.dto.FotoPerfilUrl;
import com.unex.chamaAi.usuario.application.dto.PerfilUsuario;
import com.unex.chamaAi.usuario.domain.entities.Usuario;
import com.unex.chamaAi.usuario.domain.exceptions.FotoPerfilNaoEncontradaException;
import com.unex.chamaAi.usuario.domain.exceptions.UsuarioNaoEncontradoException;
import com.unex.chamaAi.usuario.domain.repositories.FotoPerfilRepository;
import com.unex.chamaAi.usuario.domain.repositories.UsuarioRepository;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

/** Trocar, remover e exibir a foto de perfil. */
public class GerenciarFotoPerfil {

    private final UsuarioRepository usuarios;
    private final FotoPerfilRepository fotos;
    private final Clock relogio;

    public GerenciarFotoPerfil(UsuarioRepository usuarios, FotoPerfilRepository fotos, Clock relogio) {
        this.usuarios = usuarios;
        this.fotos = fotos;
        this.relogio = relogio;
    }

    public PerfilUsuario enviar(UUID usuarioId, byte[] conteudo) {
        Usuario usuario = usuarios.buscarPorId(usuarioId).orElseThrow(UsuarioNaoEncontradoException::new);
        Imagem imagem = Imagem.de(conteudo, "a foto de perfil");
        Instant agora = relogio.instant();
        fotos.salvar(usuarioId, imagem, agora);
        return PerfilUsuario.de(usuario, FotoPerfilUrl.de(usuarioId, agora));
    }

    public PerfilUsuario remover(UUID usuarioId) {
        Usuario usuario = usuarios.buscarPorId(usuarioId).orElseThrow(UsuarioNaoEncontradoException::new);
        fotos.remover(usuarioId);
        return PerfilUsuario.de(usuario);
    }

    /** Pública: a foto aparece nos anúncios e nas propostas. */
    public Imagem exibir(UUID usuarioId) {
        return fotos.ler(usuarioId).orElseThrow(FotoPerfilNaoEncontradaException::new);
    }
}
