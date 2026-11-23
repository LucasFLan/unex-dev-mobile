package com.unex.chamaAi.usuario.domain.exceptions;

import com.unex.chamaAi.shared.domain.exceptions.RecursoNaoEncontradoException;

public class FotoPerfilNaoEncontradaException extends RecursoNaoEncontradoException {

    public FotoPerfilNaoEncontradaException() {
        super("Este usuário não tem foto de perfil.");
    }
}
