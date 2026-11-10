package com.unex.chamaAi.usuario.domain.exceptions;

import com.unex.chamaAi.shared.domain.exceptions.ConflitoException;

public class EmailJaCadastradoException extends ConflitoException {

    public EmailJaCadastradoException() {
        super("Email já cadastrado.");
    }
}
