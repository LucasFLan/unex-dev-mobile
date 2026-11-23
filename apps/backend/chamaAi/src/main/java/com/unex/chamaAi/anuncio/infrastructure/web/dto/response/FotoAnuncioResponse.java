package com.unex.chamaAi.anuncio.infrastructure.web.dto.response;

import com.unex.chamaAi.anuncio.application.dto.FotoResumo;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Foto do anúncio.")
public record FotoAnuncioResponse(
        @Schema(description = "Id da foto (usado para remover).", example = "5c8e2a1d-3b4f-4e6a-8d7c-9b0a1e2f3d4c")
        UUID id,

        @Schema(description = "Caminho da imagem na API.",
                example = "/anuncios/7a1c9e2b-4d3f-4b8a-9e6d-2c5f8a1b3d4e/fotos/5c8e2a1d-3b4f-4e6a-8d7c-9b0a1e2f3d4c")
        String url) {

    public static FotoAnuncioResponse de(FotoResumo foto) {
        return new FotoAnuncioResponse(foto.id(), foto.url());
    }
}
