package com.unex.chamaAi.proposta.application.dto;

import java.math.BigDecimal;

/** valor nulo = "a combinar". */
public record EnviarPropostaCommand(BigDecimal valor, String mensagem) {
}
