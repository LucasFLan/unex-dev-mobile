package com.unex.chamaAi.usuario.infrastructure.web;

import com.unex.chamaAi.usuario.application.dto.AlterarSenhaCommand;

public record AlterarSenhaRequest(String senhaAtual, String novaSenha) {

    public AlterarSenhaCommand paraComando() {
        return new AlterarSenhaCommand(senhaAtual, novaSenha);
    }

    @Override
    public String toString() {
        return "AlterarSenhaRequest[****]";
    }
}
