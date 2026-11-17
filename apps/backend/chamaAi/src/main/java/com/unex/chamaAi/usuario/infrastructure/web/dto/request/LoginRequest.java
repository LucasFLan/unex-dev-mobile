package com.unex.chamaAi.usuario.infrastructure.web.dto.request;

import com.unex.chamaAi.usuario.application.dto.LoginCommand;
import io.swagger.v3.oas.annotations.media.Schema;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Schema(description = "Credenciais de acesso.")
public record LoginRequest(
        @Schema(description = "Email cadastrado.", example = "joao@unex.edu.br", format = "email",
                requiredMode = REQUIRED)
        String email,

        @Schema(description = "Senha da conta.", example = "Senha123!", format = "password", requiredMode = REQUIRED)
        String senha) {

    public LoginCommand paraComando() {
        return new LoginCommand(email, senha);
    }

    @Override
    public String toString() {
        return "LoginRequest[****]";
    }
}
