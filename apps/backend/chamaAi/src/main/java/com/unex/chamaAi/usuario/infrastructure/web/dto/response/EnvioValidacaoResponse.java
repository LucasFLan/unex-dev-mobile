package com.unex.chamaAi.usuario.infrastructure.web.dto.response;

import com.unex.chamaAi.usuario.application.dto.EnvioParaAnalise;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "Envio de documentos para análise do admin. As imagens ficam em `/arquivos/DOCUMENTO` e `/arquivos/SELFIE`.")
public record EnvioValidacaoResponse(
        @Schema(description = "Id do envio.", example = "8d7c6b5a-4f3e-4d2c-9b1a-0f9e8d7c6b5a")
        UUID id,

        @Schema(description = "Quem enviou.")
        SolicitanteValidacaoResponse usuario,

        @Schema(description = "`PENDENTE`, `APROVADO` ou `REPROVADO`.", example = "PENDENTE",
                allowableValues = {"PENDENTE", "APROVADO", "REPROVADO"})
        String status,

        @Schema(description = "Quando foi enviado (ISO-8601, UTC).", example = "2026-11-22T14:00:00.123456Z")
        Instant enviadoEm,

        @Schema(description = "Quando foi analisado (ISO-8601, UTC).", example = "2026-11-22T16:30:00.654321Z",
                nullable = true)
        Instant analisadoEm,

        @Schema(description = "Motivo da reprovação.", example = "A foto do documento está desfocada.",
                nullable = true)
        String motivoReprovacao) {

    public static EnvioValidacaoResponse de(EnvioParaAnalise envio) {
        return new EnvioValidacaoResponse(envio.id(), SolicitanteValidacaoResponse.de(envio.usuario()), envio.status(),
                envio.enviadoEm(), envio.analisadoEm(), envio.motivoReprovacao());
    }
}
