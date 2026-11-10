package com.unex.chamaAi.usuario.domain.exceptions;

import com.unex.chamaAi.shared.domain.exceptions.ConflitoException;

public class CpfJaCadastradoException extends ConflitoException {

    public CpfJaCadastradoException() {
        super("CPF já cadastrado.");
    }
}
