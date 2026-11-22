package com.unex.chamaAi.notificacao.domain.exceptions;

import com.unex.chamaAi.shared.domain.exceptions.RecursoNaoEncontradoException;

public class NotificacaoNaoEncontradaException extends RecursoNaoEncontradoException {

    public NotificacaoNaoEncontradaException() {
        super("Notificação não encontrada.");
    }
}
