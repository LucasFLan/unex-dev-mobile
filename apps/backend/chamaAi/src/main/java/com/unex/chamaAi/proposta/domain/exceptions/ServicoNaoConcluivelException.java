package com.unex.chamaAi.proposta.domain.exceptions;

import com.unex.chamaAi.shared.domain.exceptions.DominioException;

public class ServicoNaoConcluivelException extends DominioException {

    public ServicoNaoConcluivelException() {
        super("Só um serviço com proposta aceita pode ser marcado como concluído.");
    }
}
