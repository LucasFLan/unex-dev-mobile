package com.unex.chamaAi.proposta.domain.exceptions;

import com.unex.chamaAi.shared.domain.exceptions.DominioException;

public class PropostaParaProprioAnuncioException extends DominioException {

    public PropostaParaProprioAnuncioException() {
        super("Você não pode enviar proposta para o seu próprio anúncio.");
    }
}
