package com.unex.chamaAi.anuncio.domain.valueobjects;

import com.unex.chamaAi.shared.domain.exceptions.DadoInvalidoException;

import java.util.Locale;
import java.util.Set;

public record Localizacao(String cidade, String uf) {

    private static final int TAMANHO_MAXIMO_CIDADE = 100;

    public static final Set<String> UFS = Set.of(
            "AC", "AL", "AP", "AM", "BA", "CE", "DF", "ES", "GO", "MA", "MT", "MS", "MG", "PA",
            "PB", "PR", "PE", "PI", "RJ", "RN", "RS", "RO", "RR", "SC", "SP", "SE", "TO");

    public Localizacao {
        cidade = validarCidade(cidade);
        uf = validarUf(uf);
    }

    public static String validarUf(String uf) {
        if (uf == null || uf.isBlank()) {
            throw new DadoInvalidoException("A UF é obrigatória.");
        }
        String normalizada = uf.trim().toUpperCase(Locale.ROOT);
        if (!UFS.contains(normalizada)) {
            throw new DadoInvalidoException("UF inválida.");
        }
        return normalizada;
    }

    private static String validarCidade(String cidade) {
        if (cidade == null || cidade.isBlank()) {
            throw new DadoInvalidoException("A cidade é obrigatória.");
        }
        String semEspacos = cidade.trim();
        if (semEspacos.length() > TAMANHO_MAXIMO_CIDADE) {
            throw new DadoInvalidoException("A cidade deve ter no máximo " + TAMANHO_MAXIMO_CIDADE + " caracteres.");
        }
        return semEspacos;
    }
}
