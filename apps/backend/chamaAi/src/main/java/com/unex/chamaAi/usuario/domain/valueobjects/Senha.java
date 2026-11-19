package com.unex.chamaAi.usuario.domain.valueobjects;

import com.unex.chamaAi.shared.domain.exceptions.DadoInvalidoException;

import java.nio.charset.StandardCharsets;

public record Senha(String valor) {

    private static final int TAMANHO_MINIMO = 8;
    private static final int TAMANHO_MAXIMO_BYTES = 72;

    public Senha {
        if (valor == null || valor.isBlank()) {
            throw new DadoInvalidoException("A senha não pode ser vazia.");
        }

        if (valor.getBytes(StandardCharsets.UTF_8).length > TAMANHO_MAXIMO_BYTES) {
            throw new DadoInvalidoException("A senha não pode ultrapassar " + TAMANHO_MAXIMO_BYTES + " bytes.");
        }

        if (valor.length() < TAMANHO_MINIMO) {
            throw new DadoInvalidoException("A senha deve ter no mínimo " + TAMANHO_MINIMO + " caracteres.");
        }

        if (valor.chars().noneMatch(Character::isUpperCase)) {
            throw new DadoInvalidoException("A senha deve conter pelo menos uma letra maiúscula.");
        }

        if (valor.chars().noneMatch(Character::isDigit)) {
            throw new DadoInvalidoException("A senha deve conter pelo menos um número.");
        }

        if (valor.chars().allMatch(Character::isLetterOrDigit)) {
            throw new DadoInvalidoException("A senha deve conter pelo menos um caractere especial.");
        }
    }

    @Override
    public String toString() {
        return "Senha[*****]";
    }
}