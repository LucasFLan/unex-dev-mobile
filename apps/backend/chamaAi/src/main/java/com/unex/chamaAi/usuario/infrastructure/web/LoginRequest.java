package com.unex.chamaAi.usuario.infrastructure.web;

import com.unex.chamaAi.usuario.application.dto.LoginCommand;

public record LoginRequest(String email, String senha) {

    public LoginCommand paraComando() {
        return new LoginCommand(email, senha);
    }

    @Override
    public String toString() {
        return "LoginRequest[****]";
    }
}
