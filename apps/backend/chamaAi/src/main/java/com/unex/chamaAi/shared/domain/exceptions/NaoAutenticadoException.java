package com.unex.chamaAi.shared.domain.exceptions;

public abstract class NaoAutenticadoException extends DominioException {

    protected NaoAutenticadoException(String mensagem) {
        super(mensagem);
    }
}
