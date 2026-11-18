package com.unex.chamaAi.anuncio.domain.repositories;

import com.unex.chamaAi.anuncio.domain.valueobjects.TipoAnuncio;

import java.util.UUID;

/** Filtros opcionais da busca pública. Campo nulo = não filtra. */
public record FiltroAnuncios(TipoAnuncio tipo, UUID categoriaId, String cidade, String uf) {

    public static FiltroAnuncios vazio() {
        return new FiltroAnuncios(null, null, null, null);
    }
}
