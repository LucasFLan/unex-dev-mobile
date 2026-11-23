package com.unex.chamaAi.notificacao.application.usecases;

import com.unex.chamaAi.notificacao.domain.entities.Notificacao;
import com.unex.chamaAi.notificacao.domain.repositories.NotificacaoRepository;
import com.unex.chamaAi.notificacao.domain.valueobjects.TipoNotificacao;
import com.unex.chamaAi.usuario.application.ports.NotificadorValidacao;

import java.util.UUID;

/** Avisa o usuário do resultado da análise dos documentos. */
public class NotificarEventosDeValidacao implements NotificadorValidacao {

    private final NotificacaoRepository notificacoes;

    public NotificarEventosDeValidacao(NotificacaoRepository notificacoes) {
        this.notificacoes = notificacoes;
    }

    @Override
    public void contaAprovada(UUID usuarioId) {
        notificacoes.salvar(Notificacao.criar(usuarioId, TipoNotificacao.VALIDACAO_APROVADA, "Conta validada!",
                "Seus documentos foram aprovados. Agora você já pode publicar anúncios e enviar propostas.",
                null, null));
    }

    @Override
    public void contaReprovada(UUID usuarioId, String motivo) {
        notificacoes.salvar(Notificacao.criar(usuarioId, TipoNotificacao.VALIDACAO_REPROVADA,
                "Documentos não aprovados",
                "Não conseguimos validar sua conta. Motivo: %s Envie os documentos de novo em Validar conta."
                        .formatted(terminarComPonto(motivo)),
                null, null));
    }

    private static String terminarComPonto(String texto) {
        String limpo = texto.trim();
        return limpo.endsWith(".") || limpo.endsWith("!") || limpo.endsWith("?") ? limpo : limpo + ".";
    }
}
