package com.unex.chamaAi.usuario.application.dto;

import java.time.Instant;

/**
 * Situação da validação vista pelo próprio usuário.
 *
 * @param situacao NAO_ENVIADO (ainda não mandou documentos), EM_ANALISE, APROVADO ou REPROVADO
 * @param motivoReprovacao preenchido só quando REPROVADO
 */
public record MinhaValidacao(String situacao, Instant enviadoEm, Instant analisadoEm, String motivoReprovacao) {

    public static final String NAO_ENVIADO = "NAO_ENVIADO";
    public static final String EM_ANALISE = "EM_ANALISE";
    public static final String APROVADO = "APROVADO";
    public static final String REPROVADO = "REPROVADO";

    public boolean podeEnviar() {
        return NAO_ENVIADO.equals(situacao) || REPROVADO.equals(situacao);
    }
}
