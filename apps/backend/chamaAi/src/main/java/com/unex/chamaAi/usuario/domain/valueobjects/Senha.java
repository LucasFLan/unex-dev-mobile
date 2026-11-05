package com.unex.chamaAi.usuario.domain.valueobjects;

import java.util.regex.Pattern;

public record Senha(String valor) {

    private static final Pattern REGEX_COMPLEXIDADE =
            Pattern.compile("^(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z\\d]).{8,}$");

    public Senha {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("A senha não pode ser vazia.");
        }

        if (!REGEX_COMPLEXIDADE.matcher(valor).matches()) {
            throw new IllegalArgumentException("A senha deve ter no mínimo 8 caracteres, uma letra maiúscula, um número e um caractere especial.");
        }
    }

    @Override
    public String toString() {
        return "Senha[****]";
    }
}
