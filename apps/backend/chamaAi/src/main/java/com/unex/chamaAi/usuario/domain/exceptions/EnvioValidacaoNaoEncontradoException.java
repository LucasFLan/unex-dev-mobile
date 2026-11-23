package com.unex.chamaAi.usuario.domain.exceptions;

import com.unex.chamaAi.shared.domain.exceptions.RecursoNaoEncontradoException;

public class EnvioValidacaoNaoEncontradoException extends RecursoNaoEncontradoException {

    public EnvioValidacaoNaoEncontradoException() {
        super("Envio de validação não encontrado.");
    }
}
