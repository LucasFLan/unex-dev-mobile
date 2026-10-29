package com.unex.chamaAi.usuario.domain.valueobjects;

public record SenhaHash(String valor) {

    public SenhaHash {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("O hash da senha não pode ser vazio.");
        }
    }

    @Override
    public String toString() {
        return "SenhaHash[****]";
    }
}
