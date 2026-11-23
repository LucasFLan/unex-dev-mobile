package com.unex.chamaAi.proposta.infrastructure.web.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED;
import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Schema(description = "Avaliação do serviço concluído.")
public record AvaliacaoRequest(
        @Schema(description = "Nota de 1 a 5 estrelas.", example = "5", minimum = "1", maximum = "5",
                requiredMode = REQUIRED)
        Integer nota,

        @Schema(description = "Comentário opcional. Espaços nas pontas são removidos.",
                example = "Pontual e caprichoso, recomendo.", maxLength = 500, requiredMode = NOT_REQUIRED,
                nullable = true)
        String comentario) {
}
