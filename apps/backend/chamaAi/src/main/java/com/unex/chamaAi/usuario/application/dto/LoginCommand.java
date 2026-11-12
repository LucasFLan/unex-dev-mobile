package com.unex.chamaAi.usuario.application.dto;

public record LoginCommand(String email, String senha) {

    @Override
    public String toString() {
        return "LoginCommand[****]";
    }
}
