package com.unex.chamaAi.proposta.application.dto;

import java.math.BigDecimal;
import java.util.List;

/** valor nulo = "a combinar". fotos: o conteúdo de cada imagem anexada (pode ser vazia). */
public record EnviarPropostaCommand(BigDecimal valor, String mensagem, List<byte[]> fotos) {

    public EnviarPropostaCommand {
        fotos = fotos == null ? List.of() : List.copyOf(fotos);
    }

    /** Proposta sem fotos. */
    public EnviarPropostaCommand(BigDecimal valor, String mensagem) {
        this(valor, mensagem, List.of());
    }
}
