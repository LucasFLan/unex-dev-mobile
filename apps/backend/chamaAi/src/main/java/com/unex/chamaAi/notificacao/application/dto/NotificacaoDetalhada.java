package com.unex.chamaAi.notificacao.application.dto;

import com.unex.chamaAi.notificacao.domain.entities.Notificacao;

import java.time.Instant;
import java.util.UUID;

/** anuncioId e propostaId podem ser nulos (ex.: o anúncio foi excluído depois). */
public record NotificacaoDetalhada(UUID id, String tipo, String titulo, String mensagem, UUID anuncioId,
                                   UUID propostaId, boolean lida, Instant criadoEm) {

    public static NotificacaoDetalhada de(Notificacao notificacao) {
        return new NotificacaoDetalhada(notificacao.getId(), notificacao.getTipo().name(), notificacao.getTitulo(),
                notificacao.getMensagem(), notificacao.getAnuncioId(), notificacao.getPropostaId(),
                notificacao.isLida(), notificacao.getCriadoEm());
    }
}
