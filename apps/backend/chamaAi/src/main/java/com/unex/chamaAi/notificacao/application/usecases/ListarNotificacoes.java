package com.unex.chamaAi.notificacao.application.usecases;

import com.unex.chamaAi.notificacao.application.dto.NotificacaoDetalhada;
import com.unex.chamaAi.notificacao.domain.repositories.NotificacaoRepository;
import com.unex.chamaAi.shared.domain.paginacao.Pagina;
import com.unex.chamaAi.shared.domain.paginacao.Paginacao;

import java.util.UUID;

public class ListarNotificacoes {

    private final NotificacaoRepository notificacoes;

    public ListarNotificacoes(NotificacaoRepository notificacoes) {
        this.notificacoes = notificacoes;
    }

    public Pagina<NotificacaoDetalhada> executar(UUID usuarioId, Integer pagina, Integer tamanho) {
        return notificacoes.buscarPorUsuario(usuarioId, Paginacao.de(pagina, tamanho))
                .mapear(NotificacaoDetalhada::de);
    }

    public long contarNaoLidas(UUID usuarioId) {
        return notificacoes.contarNaoLidas(usuarioId);
    }
}
