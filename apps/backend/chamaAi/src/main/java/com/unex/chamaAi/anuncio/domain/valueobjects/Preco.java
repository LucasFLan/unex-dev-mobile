package com.unex.chamaAi.anuncio.domain.valueobjects;

import com.unex.chamaAi.shared.domain.exceptions.DadoInvalidoException;

import java.math.BigDecimal;
import java.util.Objects;

/** Valor em reais. A ausência de preço (null no anúncio) significa "a combinar". */
public record Preco(BigDecimal valor) {

    private static final BigDecimal MAXIMO = new BigDecimal("99999999.99");

    public Preco {
        Objects.requireNonNull(valor, "O valor do preço é obrigatório.");
        if (valor.signum() <= 0) {
            throw new DadoInvalidoException("O preço deve ser maior que zero.");
        }
        if (valor.stripTrailingZeros().scale() > 2) {
            throw new DadoInvalidoException("O preço deve ter no máximo duas casas decimais.");
        }
        if (valor.compareTo(MAXIMO) > 0) {
            throw new DadoInvalidoException("O preço deve ser no máximo " + MAXIMO + ".");
        }
        valor = valor.setScale(2);
    }

    /** null vira "a combinar". */
    public static Preco deOuNulo(BigDecimal valor) {
        return valor == null ? null : new Preco(valor);
    }
}
