package com.unex.chamaAi.usuario.application.dto;

/**
 * @param expiresIn tempo de validade do token, em segundos.
 */
public record TokenAcesso(String accessToken, String tokenType, long expiresIn) {

    @Override
    public String toString() {
        return "TokenAcesso[****]";
    }
}
