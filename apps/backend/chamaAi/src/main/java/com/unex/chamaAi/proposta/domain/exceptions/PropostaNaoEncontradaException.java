package com.unex.chamaAi.proposta.domain.exceptions;

import com.unex.chamaAi.shared.domain.exceptions.RecursoNaoEncontradoException;

public class PropostaNaoEncontradaException extends RecursoNaoEncontradoException {

    public PropostaNaoEncontradaException() {
        super("Proposta não encontrada.");
    }
}
