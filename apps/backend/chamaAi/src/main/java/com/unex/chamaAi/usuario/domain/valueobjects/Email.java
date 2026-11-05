package com.unex.chamaAi.usuario.domain.valueobjects;

import java.util.regex.Pattern;

public record Email(String valor) {

    private static final Pattern REGEX_EMAIL =
            Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)*\\.[A-Za-z]{2,}$");

    public Email {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("O email é obrigatório.");
        }

        valor = valor.trim().toLowerCase();

        if (!REGEX_EMAIL.matcher(valor).matches()) {
            throw new IllegalArgumentException("Email inválido.");
        }
    }
}
