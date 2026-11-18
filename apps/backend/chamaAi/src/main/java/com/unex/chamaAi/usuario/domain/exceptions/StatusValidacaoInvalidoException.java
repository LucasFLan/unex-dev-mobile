package com.unex.chamaAi.usuario.domain.exceptions;

import com.unex.chamaAi.shared.domain.exceptions.ConflitoException;

public class StatusValidacaoInvalidoException extends ConflitoException {

    public StatusValidacaoInvalidoException(String mensagem) {
        super(mensagem);
    }
}