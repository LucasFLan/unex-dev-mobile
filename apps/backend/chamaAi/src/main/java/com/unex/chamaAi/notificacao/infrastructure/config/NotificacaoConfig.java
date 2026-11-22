package com.unex.chamaAi.notificacao.infrastructure.config;

import com.unex.chamaAi.notificacao.application.usecases.ListarNotificacoes;
import com.unex.chamaAi.notificacao.application.usecases.MarcarNotificacoesComoLidas;
import com.unex.chamaAi.notificacao.application.usecases.NotificarEventosDeProposta;
import com.unex.chamaAi.notificacao.application.usecases.NotificarEventosDeValidacao;
import com.unex.chamaAi.notificacao.domain.repositories.NotificacaoRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NotificacaoConfig {

    /** Implementa o NotificadorProposta usado pelo módulo de propostas. */
    @Bean
    public NotificarEventosDeProposta notificarEventosDeProposta(NotificacaoRepository notificacoes) {
        return new NotificarEventosDeProposta(notificacoes);
    }

    /** Implementa o NotificadorValidacao usado pela validação de conta. */
    @Bean
    public NotificarEventosDeValidacao notificarEventosDeValidacao(NotificacaoRepository notificacoes) {
        return new NotificarEventosDeValidacao(notificacoes);
    }

    @Bean
    public ListarNotificacoes listarNotificacoes(NotificacaoRepository notificacoes) {
        return new ListarNotificacoes(notificacoes);
    }

    @Bean
    public MarcarNotificacoesComoLidas marcarNotificacoesComoLidas(NotificacaoRepository notificacoes) {
        return new MarcarNotificacoesComoLidas(notificacoes);
    }
}
