package com.unex.chamaAi.usuario.domain.exceptions;

import com.unex.chamaAi.shared.domain.exceptions.ConflitoException;

public class DocumentosEmAnaliseException extends ConflitoException {

    public DocumentosEmAnaliseException() {
        super("Seus documentos já estão em análise. Aguarde a resposta.");
    }
}
