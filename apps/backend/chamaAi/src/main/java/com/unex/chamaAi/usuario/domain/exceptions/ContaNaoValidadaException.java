package com.unex.chamaAi.usuario.domain.exceptions;

import com.unex.chamaAi.shared.domain.exceptions.AcessoNegadoException;

/** Usuário ainda não aprovado tentando publicar anúncio ou enviar proposta. */
public class ContaNaoValidadaException extends AcessoNegadoException {

    public ContaNaoValidadaException() {
        super("Valide sua conta para continuar: envie a foto do documento e uma selfie em Validar conta.");
    }
}
