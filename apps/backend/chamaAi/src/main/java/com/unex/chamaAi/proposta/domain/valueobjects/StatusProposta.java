package com.unex.chamaAi.proposta.domain.valueobjects;

public enum StatusProposta {
    /** Aguardando a resposta do autor do anúncio. */
    PENDENTE,
    /** O autor do anúncio aceitou: os contatos são liberados. */
    ACEITA,
    /** O autor do anúncio recusou. */
    RECUSADA,
    /** O autor aceitou outra proposta para o mesmo pedido. */
    NAO_SELECIONADA,
    /** Quem enviou retirou a proposta. */
    CANCELADA
}
