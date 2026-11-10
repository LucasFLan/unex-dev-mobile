package com.unex.chamaAi.shared.domain.exceptions;

public abstract class ConflitoException extends DominioException {

    protected ConflitoException(String mensagem) {
        super(mensagem);
    }
}
