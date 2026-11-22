package com.unex.chamaAi.usuario.domain.repositories;

import com.unex.chamaAi.usuario.domain.entities.CodigoRecuperacaoSenha;

import java.util.Optional;
import java.util.UUID;

public interface CodigoRecuperacaoSenhaRepository {

    /** Insere ou atualiza. */
    void salvar(CodigoRecuperacaoSenha codigo);

    /** O código mais recente do usuário (usado ou não). */
    Optional<CodigoRecuperacaoSenha> buscarMaisRecente(UUID usuarioId);
}
