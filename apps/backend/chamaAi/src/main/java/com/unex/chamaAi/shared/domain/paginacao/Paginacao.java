package com.unex.chamaAi.shared.domain.paginacao;

import com.unex.chamaAi.shared.domain.exceptions.DadoInvalidoException;

/** Página solicitada (começando em 0) e quantidade de itens por página. */
public record Paginacao(int pagina, int tamanho) {

    public static final int TAMANHO_PADRAO = 20;
    public static final int TAMANHO_MAXIMO = 50;

    public Paginacao {
        if (pagina < 0) {
            throw new DadoInvalidoException("A página não pode ser negativa.");
        }
        if (tamanho < 1 || tamanho > TAMANHO_MAXIMO) {
            throw new DadoInvalidoException("O tamanho da página deve estar entre 1 e " + TAMANHO_MAXIMO + ".");
        }
    }

    public static Paginacao de(Integer pagina, Integer tamanho) {
        return new Paginacao(pagina == null ? 0 : pagina, tamanho == null ? TAMANHO_PADRAO : tamanho);
    }
}
