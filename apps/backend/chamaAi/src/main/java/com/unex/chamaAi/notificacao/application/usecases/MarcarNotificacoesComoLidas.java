package com.unex.chamaAi.notificacao.application.usecases;

import com.unex.chamaAi.notificacao.domain.entities.Notificacao;
import com.unex.chamaAi.notificacao.domain.exceptions.NotificacaoNaoEncontradaException;
import com.unex.chamaAi.notificacao.domain.repositories.NotificacaoRepository;

import java.util.UUID;

public class MarcarNotificacoesComoLidas {

    private final NotificacaoRepository notificacoes;

    public MarcarNotificacoesComoLidas(NotificacaoRepository notificacoes) {
        this.notificacoes = notificacoes;
    }

    public void todas(UUID usuarioId) {
        notificacoes.marcarTodasComoLidas(usuarioId);
    }

    /** Notificação de outra pessoa "não existe". */
    public void uma(UUID notificacaoId, UUID usuarioId) {
        Notificacao notificacao = notificacoes.buscarPorId(notificacaoId)
                .filter(encontrada -> encontrada.pertenceA(usuarioId))
                .orElseThrow(NotificacaoNaoEncontradaException::new);
        if (!notificacao.isLida()) {
            notificacao.marcarComoLida();
            notificacoes.salvar(notificacao);
        }
    }
}
