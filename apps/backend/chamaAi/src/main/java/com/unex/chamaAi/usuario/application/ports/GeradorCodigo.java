package com.unex.chamaAi.usuario.application.ports;

/** Gera o código numérico de recuperação de senha. Separado para os testes usarem um código fixo. */
public interface GeradorCodigo {

    String gerar();
}
