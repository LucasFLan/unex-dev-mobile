package com.unex.chamaAi.anuncio.application.dto;

import com.unex.chamaAi.anuncio.domain.valueobjects.FotoAnuncio;

import java.util.UUID;

/** url: caminho público da foto na API. */
public record FotoResumo(UUID id, String url) {

    public static FotoResumo de(FotoAnuncio foto) {
        return new FotoResumo(foto.id(), "/anuncios/" + foto.anuncioId() + "/fotos/" + foto.id());
    }
}
