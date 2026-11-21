package com.unex.chamaAi.proposta.infrastructure.web.dto.response;

import com.unex.chamaAi.proposta.application.dto.PropostaDetalhada;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Schema(description = "Proposta enviada para o autor de um anúncio.")
public record PropostaResponse(
        @Schema(description = "Id da proposta.", example = "c4d5e6f7-1a2b-4c3d-8e9f-0a1b2c3d4e5f")
        UUID id,

        @Schema(description = "Anúncio da proposta.")
        PropostaAnuncioResponse anuncio,

        @Schema(description = "Quem enviou a proposta.")
        PropostaParticipanteResponse remetente,

        @Schema(description = "Quem recebeu (autor do anúncio).")
        PropostaParticipanteResponse destinatario,

        @Schema(description = "Valor proposto em reais. `null` = \"a combinar\".", example = "180.00", nullable = true)
        BigDecimal valor,

        @Schema(description = "Mensagem para o autor do anúncio.",
                example = "Posso fazer amanhã pela manhã, levo o material.")
        String mensagem,

        @Schema(description = """
                `PENDENTE` (aguardando resposta), `ACEITA`, `RECUSADA`, `NAO_SELECIONADA` (o autor do pedido aceitou \
                outra proposta) ou `CANCELADA` (quem enviou retirou).""", example = "PENDENTE",
                allowableValues = {"PENDENTE", "ACEITA", "RECUSADA", "NAO_SELECIONADA", "CANCELADA"})
        String status,

        @Schema(description = "Data de envio (ISO-8601, UTC).", example = "2026-11-20T21:30:00.123456Z")
        Instant criadoEm,

        @Schema(description = "Data em que saiu de PENDENTE (ISO-8601, UTC). `null` enquanto pendente.",
                example = "2026-11-21T09:15:00.654321Z", nullable = true)
        Instant respondidoEm) {

    public static PropostaResponse de(PropostaDetalhada proposta) {
        return new PropostaResponse(proposta.id(), PropostaAnuncioResponse.de(proposta.anuncio()),
                PropostaParticipanteResponse.de(proposta.remetente()),
                PropostaParticipanteResponse.de(proposta.destinatario()), proposta.valor(), proposta.mensagem(),
                proposta.status(), proposta.criadoEm(), proposta.respondidoEm());
    }
}
