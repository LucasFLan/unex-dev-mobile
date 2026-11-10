package com.unex.chamaAi.usuario.domain.exceptions;

import com.unex.chamaAi.shared.domain.exceptions.ConflitoException;

public class ContaJaVerificadaException extends ConflitoException {

    public ContaJaVerificadaException() {
        super("Usuário já está verificado.");
    }
}
