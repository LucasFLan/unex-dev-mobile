package com.unex.chamaAi.anuncio.infrastructure.web.dto.response;

import com.unex.chamaAi.anuncio.application.dto.CategoriaResumo;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Categoria de serviço.")
public record CategoriaResponse(
        @Schema(description = "Id da categoria, usado em `categoriaId`.", example = "0b9d3c4e-6f1a-4c2b-8d7e-5a4b3c2d1e0f")
        UUID id,

        @Schema(description = "Identificador legível e estável.", example = "eletricista")
        String slug,

        @Schema(description = "Nome para exibição.", example = "Eletricista")
        String nome) {

    public static CategoriaResponse de(CategoriaResumo categoria) {
        return categoria == null ? null : new CategoriaResponse(categoria.id(), categoria.slug(), categoria.nome());
    }
}
