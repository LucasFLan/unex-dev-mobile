package com.unex.chamaAi.anuncio.domain.exceptions;

import com.unex.chamaAi.shared.domain.exceptions.RecursoNaoEncontradoException;

public class CepNaoEncontradoException extends RecursoNaoEncontradoException {

    public CepNaoEncontradoException() {
        super("CEP não encontrado.");
    }
}
