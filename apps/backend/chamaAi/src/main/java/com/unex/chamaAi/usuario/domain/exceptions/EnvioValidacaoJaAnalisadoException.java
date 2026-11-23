package com.unex.chamaAi.usuario.domain.exceptions;

import com.unex.chamaAi.shared.domain.exceptions.DominioException;

public class EnvioValidacaoJaAnalisadoException extends DominioException {

    public EnvioValidacaoJaAnalisadoException() {
        super("Este envio já foi analisado.");
    }
}
