package com.unex.chamaAi.usuario.domain.repositories;

import com.unex.chamaAi.usuario.domain.valueobjects.FotoPerfil;

import java.util.Optional;
import java.util.UUID;

/** Onde as fotos de perfil ficam guardadas: uma por usuário, a nova substitui a anterior. */
public interface FotoPerfilRepository {

    void gravar(UUID usuarioId, FotoPerfil foto);

    Optional<FotoPerfil> buscar(UUID usuarioId);

    void remover(UUID usuarioId);
}
