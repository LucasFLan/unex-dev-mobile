package com.unex.chamaAi.anuncio.domain.valueobjects;

import com.unex.chamaAi.shared.domain.exceptions.DadoInvalidoException;

import java.util.Locale;

/** OFERTA: prestador oferece um serviço. PEDIDO: cliente procura alguém para um serviço. */
public enum TipoAnuncio {
    OFERTA,
    PEDIDO;

    public static TipoAnuncio de(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new DadoInvalidoException("O tipo do anúncio é obrigatório.");
        }
        try {
            return valueOf(valor.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new DadoInvalidoException("Tipo de anúncio inválido. Use OFERTA ou PEDIDO.");
        }
    }
}
