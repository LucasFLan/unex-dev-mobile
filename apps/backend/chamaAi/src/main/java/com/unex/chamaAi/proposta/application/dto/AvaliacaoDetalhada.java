package com.unex.chamaAi.proposta.application.dto;

import com.unex.chamaAi.proposta.domain.entities.Avaliacao;
import com.unex.chamaAi.usuario.domain.entities.Usuario;

import java.time.Instant;
import java.util.UUID;

/** Avaliação com quem avaliou. autor nulo = a pessoa apagou a conta. */
public record AvaliacaoDetalhada(UUID id, UUID propostaId, int nota, String comentario, String servicoTitulo,
                                 String servicoCategoria, Autor autor, Instant criadoEm) {

    public record Autor(UUID id, String nome, Instant fotoAtualizadaEm) {
    }

    public static AvaliacaoDetalhada de(Avaliacao avaliacao, Usuario autor) {
        return new AvaliacaoDetalhada(avaliacao.getId(), avaliacao.getPropostaId().orElse(null), avaliacao.getNota(),
                avaliacao.getComentario().orElse(null), avaliacao.getServicoTitulo(),
                avaliacao.getServicoCategoria().orElse(null),
                autor == null ? null : new Autor(autor.getId(), autor.getNome(), autor.getFotoAtualizadaEm()),
                avaliacao.getCriadoEm());
    }
}
