package com.unex.chamaAi.notificacao.infrastructure.web.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Quantidade de notificações não lidas.")
public record ContagemNotificacoesResponse(
        @Schema(description = "Total de notificações ainda não lidas.", example = "2")
        long naoLidas) {
}
