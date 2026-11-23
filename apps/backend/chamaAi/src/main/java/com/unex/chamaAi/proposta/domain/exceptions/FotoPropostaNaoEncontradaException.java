package com.unex.chamaAi.proposta.domain.exceptions;

import com.unex.chamaAi.shared.domain.exceptions.RecursoNaoEncontradoException;

public class FotoPropostaNaoEncontradaException extends RecursoNaoEncontradoException {

    public FotoPropostaNaoEncontradaException() {
        super("Foto não encontrada.");
    }
}
