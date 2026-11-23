package com.unex.chamaAi.proposta.domain.exceptions;

import com.unex.chamaAi.shared.domain.exceptions.DominioException;

public class AvaliacaoIndisponivelException extends DominioException {

    public AvaliacaoIndisponivelException() {
        super("Marque o serviço como concluído antes de avaliar.");
    }
}
