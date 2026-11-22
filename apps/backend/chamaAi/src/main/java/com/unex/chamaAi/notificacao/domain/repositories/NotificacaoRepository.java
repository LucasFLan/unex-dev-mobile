package com.unex.chamaAi.notificacao.domain.repositories;

import com.unex.chamaAi.notificacao.domain.entities.Notificacao;
import com.unex.chamaAi.shared.domain.paginacao.Pagina;
import com.unex.chamaAi.shared.domain.paginacao.Paginacao;

import java.util.Optional;
import java.util.UUID;

public interface NotificacaoRepository {

    void salvar(Notificacao notificacao);

    Optional<Notificacao> buscarPorId(UUID id);

    /** Notificações do usuário, da mais recente para a mais antiga. */
    Pagina<Notificacao> buscarPorUsuario(UUID usuarioId, Paginacao paginacao);

    long contarNaoLidas(UUID usuarioId);

    /** Marca todas as notificações do usuário como lidas. */
    void marcarTodasComoLidas(UUID usuarioId);
}
