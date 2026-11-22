package com.unex.chamaAi.usuario.infrastructure.web.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Schema(description = "Código recebido por email.")
public record ValidarCodigoRecuperacaoRequest(
        @Schema(description = "Email da conta.", example = "joao@unex.edu.br", format = "email",
                requiredMode = REQUIRED)
        String email,

        @Schema(description = "Código de 6 dígitos recebido por email.", example = "482913", minLength = 6,
                maxLength = 6, requiredMode = REQUIRED)
        String codigo) {

    @Override
    public String toString() {
        return "ValidarCodigoRecuperacaoRequest[****]";
    }
}
