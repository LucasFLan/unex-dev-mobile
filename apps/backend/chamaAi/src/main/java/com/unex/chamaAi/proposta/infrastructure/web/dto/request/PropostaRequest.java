package com.unex.chamaAi.proposta.infrastructure.web.dto.request;

import com.unex.chamaAi.proposta.application.dto.EnviarPropostaCommand;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED;
import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Schema(description = "Proposta para o autor de um anúncio.")
public record PropostaRequest(
        @Schema(description = "Valor proposto em reais, maior que zero, com até 2 casas decimais. Ausente ou `null` = "
                + "\"a combinar\".", example = "180.00", minimum = "0.01", maximum = "99999999.99",
                requiredMode = NOT_REQUIRED, nullable = true)
        BigDecimal valor,

        @Schema(description = "Mensagem para o autor do anúncio. Espaços nas pontas são removidos.",
                example = "Posso fazer amanhã pela manhã, levo o material.", maxLength = 1000,
                requiredMode = REQUIRED)
        String mensagem) {

    public EnviarPropostaCommand paraComando() {
        return new EnviarPropostaCommand(valor, mensagem);
    }
}
