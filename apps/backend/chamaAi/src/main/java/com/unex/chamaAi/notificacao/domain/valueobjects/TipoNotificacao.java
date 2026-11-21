package com.unex.chamaAi.notificacao.domain.valueobjects;

public enum TipoNotificacao {
    /** Alguém enviou uma proposta para um anúncio do usuário. */
    PROPOSTA_RECEBIDA,
    /** O autor do anúncio aceitou a proposta do usuário. */
    PROPOSTA_ACEITA,
    /** O autor do anúncio recusou a proposta do usuário. */
    PROPOSTA_RECUSADA,
    /** O autor do pedido aceitou a proposta de outra pessoa. */
    PROPOSTA_NAO_SELECIONADA
}
