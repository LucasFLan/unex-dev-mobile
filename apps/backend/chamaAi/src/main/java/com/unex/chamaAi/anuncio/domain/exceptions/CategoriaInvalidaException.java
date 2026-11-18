package com.unex.chamaAi.anuncio.domain.exceptions;

import com.unex.chamaAi.shared.domain.exceptions.DadoInvalidoException;

public class CategoriaInvalidaException extends DadoInvalidoException {

    public CategoriaInvalidaException() {
        super("Categoria inválida.");
    }
}
