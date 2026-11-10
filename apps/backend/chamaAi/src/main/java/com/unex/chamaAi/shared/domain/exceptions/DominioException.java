package com.unex.chamaAi.shared.domain.exceptions;

public abstract class DominioException extends RuntimeException {

    protected DominioException(String mensagem) {
        super(mensagem);
    }
}
