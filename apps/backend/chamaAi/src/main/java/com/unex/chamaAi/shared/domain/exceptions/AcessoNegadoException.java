package com.unex.chamaAi.shared.domain.exceptions;

public abstract class AcessoNegadoException extends DominioException {

    protected AcessoNegadoException(String mensagem) {
        super(mensagem);
    }
}
