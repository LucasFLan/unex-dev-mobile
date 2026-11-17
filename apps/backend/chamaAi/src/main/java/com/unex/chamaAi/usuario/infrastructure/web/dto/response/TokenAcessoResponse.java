package com.unex.chamaAi.usuario.infrastructure.web.dto.response;

import com.unex.chamaAi.usuario.application.dto.TokenAcesso;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Token de acesso emitido no login.")
public record TokenAcessoResponse(
        @Schema(description = "JWT a enviar em `Authorization: Bearer <accessToken>`.",
                example = "eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJjaGFtYWFpIiwic3ViIjoiM2Y...")
        String accessToken,

        @Schema(description = "Tipo do token. Sempre `Bearer`.", example = "Bearer")
        String tokenType,

        @Schema(description = "Validade do token, em segundos.", example = "3600")
        long expiresIn) {

    public static TokenAcessoResponse de(TokenAcesso token) {
        return new TokenAcessoResponse(token.accessToken(), token.tokenType(), token.expiresIn());
    }

    @Override
    public String toString() {
        return "TokenAcessoResponse[****]";
    }
}
