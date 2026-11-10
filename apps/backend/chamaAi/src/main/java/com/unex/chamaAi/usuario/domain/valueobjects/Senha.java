package com.unex.chamaAi.usuario.domain.valueobjects;

import com.unex.chamaAi.shared.domain.exceptions.DadoInvalidoException;

import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public record Senha(String valor) {

    private static final Pattern REGEX_COMPLEXIDADE =
            Pattern.compile("^(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z\\d]).{8,}$");

    private static final int TAMANHO_MAXIMO_BYTES = 72;

    public Senha {
        if (valor == null || valor.isBlank()) {
            throw new DadoInvalidoException("A senha não pode ser vazia.");
        }

        if (valor.getBytes(StandardCharsets.UTF_8).length > TAMANHO_MAXIMO_BYTES) {
            throw new DadoInvalidoException("A senha não pode ultrapassar " + TAMANHO_MAXIMO_BYTES + " bytes.");
        }

        if (!REGEX_COMPLEXIDADE.matcher(valor).matches()) {
            throw new DadoInvalidoException("A senha deve ter no mínimo 8 caracteres, uma letra maiúscula, um número e um caractere especial.");
        }
    }

    @Override
    public String toString() {
        return "Senha[****]";
    }
}
