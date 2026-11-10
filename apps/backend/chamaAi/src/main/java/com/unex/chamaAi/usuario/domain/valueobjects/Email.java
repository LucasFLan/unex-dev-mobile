package com.unex.chamaAi.usuario.domain.valueobjects;

import com.unex.chamaAi.shared.domain.exceptions.DadoInvalidoException;

import java.util.regex.Pattern;

public record Email(String valor) {

    private static final Pattern REGEX_EMAIL =
            Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)*\\.[A-Za-z]{2,}$");

    private static final int TAMANHO_MAXIMO = 254;

    public Email {
        if (valor == null || valor.isBlank()) {
            throw new DadoInvalidoException("O email é obrigatório.");
        }

        valor = valor.trim().toLowerCase();

        if (valor.length() > TAMANHO_MAXIMO || !REGEX_EMAIL.matcher(valor).matches()) {
            throw new DadoInvalidoException("Email inválido.");
        }
    }
}
