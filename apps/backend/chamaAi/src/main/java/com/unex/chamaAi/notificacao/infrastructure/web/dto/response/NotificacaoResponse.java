package com.unex.chamaAi.notificacao.infrastructure.web.dto.response;

import com.unex.chamaAi.notificacao.application.dto.NotificacaoDetalhada;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "Aviso para o usuário logado.")
public record NotificacaoResponse(
        @Schema(description = "Id da notificação.", example = "5b6c7d8e-9f0a-4b1c-8d2e-3f4a5b6c7d8e")
        UUID id,

        @Schema(description = "Tipo da notificação.", example = "PROPOSTA_RECEBIDA",
                allowableValues = {"PROPOSTA_RECEBIDA", "PROPOSTA_ACEITA", "PROPOSTA_RECUSADA",
                        "PROPOSTA_NAO_SELECIONADA"})
        String tipo,

        @Schema(description = "Título curto.", example = "Nova proposta recebida")
        String titulo,

        @Schema(description = "Texto pronto para exibir.",
                example = "Maria Souza enviou uma proposta de R$ 180,00 para \"Preciso trocar um chuveiro\": "
                        + "Posso fazer amanhã pela manhã, levo o material.")
        String mensagem,

        @Schema(description = "Anúncio relacionado. `null` se ele foi excluído.",
                example = "7a1c9e2b-4d3f-4b8a-9e6d-2c5f8a1b3d4e", nullable = true)
        UUID anuncioId,

        @Schema(description = "Proposta relacionada, quando houver.", example = "c4d5e6f7-1a2b-4c3d-8e9f-0a1b2c3d4e5f",
                nullable = true)
        UUID propostaId,

        @Schema(description = "Se o usuário já viu a notificação.", example = "false")
        boolean lida,

        @Schema(description = "Data de criação (ISO-8601, UTC).", example = "2026-11-20T21:30:00.123456Z")
        Instant criadoEm) {

    public static NotificacaoResponse de(NotificacaoDetalhada notificacao) {
        return new NotificacaoResponse(notificacao.id(), notificacao.tipo(), notificacao.titulo(),
                notificacao.mensagem(), notificacao.anuncioId(), notificacao.propostaId(), notificacao.lida(),
                notificacao.criadoEm());
    }
}
