package com.unex.chamaAi.usuario.domain.valueobjects;

public record Cpf(String valor) {

    public Cpf {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("O CPF é obrigatório.");
        }

        valor = valor.replaceAll("\\D", "");

        if (!valido(valor)) {
            throw new IllegalArgumentException("CPF inválido.");
        }
    }

    public String formatado() {
        return valor.replaceFirst("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
    }

    private static boolean valido(String cpf) {
        if (cpf.length() != 11 || cpf.chars().distinct().count() == 1) {
            return false;
        }
        return digitoVerificador(cpf, 9) == cpf.charAt(9) - '0'
                && digitoVerificador(cpf, 10) == cpf.charAt(10) - '0';
    }

    private static int digitoVerificador(String cpf, int quantidadeDigitos) {
        int soma = 0;
        for (int i = 0; i < quantidadeDigitos; i++) {
            soma += (cpf.charAt(i) - '0') * (quantidadeDigitos + 1 - i);
        }
        int resto = (soma * 10) % 11;
        return resto == 10 ? 0 : resto;
    }
}
