package com.unex.chamaAi.usuario.infrastructure.web.dto.response;

import com.unex.chamaAi.usuario.application.dto.MinhaValidacao;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "Situação da validação da conta do usuário logado.")
public record MinhaValidacaoResponse(
        @Schema(description = """
                `NAO_ENVIADO` (ainda não mandou os documentos), `EM_ANALISE` (aguardando o admin), `APROVADO` \
                (pode publicar anúncios e enviar propostas) ou `REPROVADO` (pode reenviar).""",
                example = "EM_ANALISE", allowableValues = {"NAO_ENVIADO", "EM_ANALISE", "APROVADO", "REPROVADO"})
        String situacao,

        @Schema(description = "Se o usuário pode mandar documentos agora (`NAO_ENVIADO` ou `REPROVADO`).",
                example = "false")
        boolean podeEnviar,

        @Schema(description = "Quando o último envio foi feito (ISO-8601, UTC).", example = "2026-11-22T14:00:00.123456Z",
                nullable = true)
        Instant enviadoEm,

        @Schema(description = "Quando o admin analisou o último envio (ISO-8601, UTC).", example = "2026-11-22T16:30:00.654321Z",
                nullable = true)
        Instant analisadoEm,

        @Schema(description = "Por que os documentos foram reprovados. Só vem quando `REPROVADO`.",
                example = "A foto do documento está desfocada.", nullable = true)
        String motivoReprovacao) {

    public static MinhaValidacaoResponse de(MinhaValidacao validacao) {
        return new MinhaValidacaoResponse(validacao.situacao(), validacao.podeEnviar(), validacao.enviadoEm(),
                validacao.analisadoEm(), validacao.motivoReprovacao());
    }
}
