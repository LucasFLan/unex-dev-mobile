package com.unex.chamaAi.usuario.infrastructure.security;

import com.unex.chamaAi.usuario.application.ports.GeradorCodigo;

import java.security.SecureRandom;

/** Código de 6 dígitos com gerador criptograficamente seguro. */
public class GeradorCodigoAleatorio implements GeradorCodigo {

    private final SecureRandom aleatorio = new SecureRandom();

    @Override
    public String gerar() {
        return "%06d".formatted(aleatorio.nextInt(1_000_000));
    }
}
