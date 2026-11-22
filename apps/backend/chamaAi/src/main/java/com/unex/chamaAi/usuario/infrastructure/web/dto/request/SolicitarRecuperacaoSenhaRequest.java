package com.unex.chamaAi.usuario.infrastructure.web.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Schema(description = "Pedido de código para redefinir a senha.")
public record SolicitarRecuperacaoSenhaRequest(
        @Schema(description = "Email da conta.", example = "joao@unex.edu.br", format = "email",
                requiredMode = REQUIRED)
        String email) {
}
