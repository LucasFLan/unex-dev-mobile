package com.unex.chamaAi.anuncio.domain.exceptions;

import com.unex.chamaAi.shared.domain.exceptions.RecursoNaoEncontradoException;

public class FotoAnuncioNaoEncontradaException extends RecursoNaoEncontradoException {

    public FotoAnuncioNaoEncontradaException() {
        super("Foto não encontrada.");
    }
}
