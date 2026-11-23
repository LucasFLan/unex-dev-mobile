package com.unex.chamaAi.proposta.domain.exceptions;

import com.unex.chamaAi.shared.domain.exceptions.ConflitoException;

public class AvaliacaoDuplicadaException extends ConflitoException {

    public AvaliacaoDuplicadaException() {
        super("Você já avaliou este serviço.");
    }
}
