package com.unex.chamaAi.usuario.infrastructure.web.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Schema(description = "Motivo da reprovação, mostrado para o usuário.")
public record ReprovarValidacaoRequest(
        @Schema(description = "Explique o que precisa ser corrigido. 5 a 500 caracteres.",
                example = "A foto do documento está desfocada. Envie de novo com boa iluminação.",
                minLength = 5, maxLength = 500, requiredMode = REQUIRED)
        String motivo) {
}
