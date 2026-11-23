package com.unex.chamaAi.shared.domain.exceptions;

/** Um serviço de fora (ex.: consulta de CEP) não respondeu. Vira 503: dá para tentar de novo depois. */
public class ServicoExternoIndisponivelException extends RuntimeException {

    public ServicoExternoIndisponivelException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
