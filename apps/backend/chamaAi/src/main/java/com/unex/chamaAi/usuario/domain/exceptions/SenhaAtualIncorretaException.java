package com.unex.chamaAi.usuario.domain.exceptions;

import com.unex.chamaAi.shared.domain.exceptions.DominioException;

public class SenhaAtualIncorretaException extends DominioException {

    public SenhaAtualIncorretaException() {
        super("Senha atual incorreta.");
    }
}
