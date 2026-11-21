package com.unex.chamaAi.proposta.domain.exceptions;

import com.unex.chamaAi.shared.domain.exceptions.AcessoNegadoException;

public class AcessoNegadoPropostaException extends AcessoNegadoException {

    public AcessoNegadoPropostaException(String mensagem) {
        super(mensagem);
    }
}
