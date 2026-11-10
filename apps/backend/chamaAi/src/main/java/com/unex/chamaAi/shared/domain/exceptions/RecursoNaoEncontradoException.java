package com.unex.chamaAi.shared.domain.exceptions;

public abstract class RecursoNaoEncontradoException extends DominioException {

    protected RecursoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
