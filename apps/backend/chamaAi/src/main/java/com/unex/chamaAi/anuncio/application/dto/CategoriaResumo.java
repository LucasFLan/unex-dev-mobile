package com.unex.chamaAi.anuncio.application.dto;

import com.unex.chamaAi.anuncio.domain.entities.Categoria;

import java.util.UUID;

public record CategoriaResumo(UUID id, String slug, String nome) {

    public static CategoriaResumo de(Categoria categoria) {
        return new CategoriaResumo(categoria.id(), categoria.slug(), categoria.nome());
    }
}
