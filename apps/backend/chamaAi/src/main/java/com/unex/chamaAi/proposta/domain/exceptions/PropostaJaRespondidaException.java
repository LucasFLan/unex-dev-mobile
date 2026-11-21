package com.unex.chamaAi.proposta.domain.exceptions;

import com.unex.chamaAi.shared.domain.exceptions.DominioException;

public class PropostaJaRespondidaException extends DominioException {

    public PropostaJaRespondidaException() {
        super("Esta proposta já foi respondida.");
    }
}
