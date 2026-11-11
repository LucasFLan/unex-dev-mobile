package com.unex.chamaAi.usuario.application.dto;

public record AlterarSenhaCommand(String senhaAtual, String novaSenha) {

    @Override
    public String toString() {
        return "AlterarSenhaCommand[****]";
    }
}
