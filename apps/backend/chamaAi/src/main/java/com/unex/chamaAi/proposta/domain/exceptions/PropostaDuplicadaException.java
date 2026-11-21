package com.unex.chamaAi.proposta.domain.exceptions;

import com.unex.chamaAi.shared.domain.exceptions.ConflitoException;

public class PropostaDuplicadaException extends ConflitoException {

    public PropostaDuplicadaException() {
        super("Você já enviou uma proposta para este anúncio.");
    }
}
