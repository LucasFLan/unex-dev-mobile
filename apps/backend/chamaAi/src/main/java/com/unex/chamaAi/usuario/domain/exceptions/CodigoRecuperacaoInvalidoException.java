package com.unex.chamaAi.usuario.domain.exceptions;

import com.unex.chamaAi.shared.domain.exceptions.DadoInvalidoException;

/** Mesma mensagem para código errado, expirado, já usado ou e-mail sem código: não revela qual foi. */
public class CodigoRecuperacaoInvalidoException extends DadoInvalidoException {

    public CodigoRecuperacaoInvalidoException() {
        super("Código inválido ou expirado. Solicite um novo código.");
    }
}
