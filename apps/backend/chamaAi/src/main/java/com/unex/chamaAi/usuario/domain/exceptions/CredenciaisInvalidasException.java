package com.unex.chamaAi.usuario.domain.exceptions;

import com.unex.chamaAi.shared.domain.exceptions.NaoAutenticadoException;

public class CredenciaisInvalidasException extends NaoAutenticadoException {

    public CredenciaisInvalidasException() {
        super("Email ou senha inválidos.");
    }
}
