package com.unex.chamaAi.anuncio.domain.exceptions;

import com.unex.chamaAi.shared.domain.exceptions.RecursoNaoEncontradoException;

public class AnuncioNaoEncontradoException extends RecursoNaoEncontradoException {

    public AnuncioNaoEncontradoException() {
        super("Anúncio não encontrado.");
    }
}
