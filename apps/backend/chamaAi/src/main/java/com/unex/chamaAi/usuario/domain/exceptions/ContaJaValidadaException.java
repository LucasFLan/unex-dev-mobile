package com.unex.chamaAi.usuario.domain.exceptions;

import com.unex.chamaAi.shared.domain.exceptions.DominioException;

public class ContaJaValidadaException extends DominioException {

    public ContaJaValidadaException() {
        super("Sua conta já está validada.");
    }
}
