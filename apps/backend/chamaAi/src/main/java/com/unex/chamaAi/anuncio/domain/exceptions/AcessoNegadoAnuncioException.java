package com.unex.chamaAi.anuncio.domain.exceptions;

import com.unex.chamaAi.shared.domain.exceptions.AcessoNegadoException;

public class AcessoNegadoAnuncioException extends AcessoNegadoException {

    public AcessoNegadoAnuncioException() {
        super("Você não tem permissão para alterar este anúncio.");
    }
}
