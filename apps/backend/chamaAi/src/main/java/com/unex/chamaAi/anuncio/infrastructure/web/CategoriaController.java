package com.unex.chamaAi.anuncio.infrastructure.web;

import com.unex.chamaAi.anuncio.infrastructure.web.dto.response.CategoriaResponse;
import com.unex.chamaAi.config.OpenApiConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Tag(name = OpenApiConfig.TAG_CATEGORIAS)
@RequestMapping("/categorias")
public interface CategoriaController {

    @Operation(summary = "Listar categorias",
            description = "Rota pública. Todas as categorias de serviço, em ordem alfabética. "
                    + "Use o `id` no campo `categoriaId` dos anúncios.")
    @SecurityRequirements
    @ApiResponse(responseCode = "200", description = "Lista de categorias.", content = @Content(
            array = @ArraySchema(schema = @Schema(implementation = CategoriaResponse.class)),
            examples = @ExampleObject(name = "categorias", value = """
                    [{"id": "5e1f2a3b-4c5d-4e6f-8a9b-0c1d2e3f4a5b", "slug": "ar-condicionado",
                      "nome": "Ar-condicionado e refrigeração"},
                     {"id": "9c8b7a6d-5e4f-4a3b-2c1d-0e9f8a7b6c5d", "slug": "chaveiro", "nome": "Chaveiro"},
                     {"id": "0b9d3c4e-6f1a-4c2b-8d7e-5a4b3c2d1e0f", "slug": "eletricista", "nome": "Eletricista"}]""")))
    @GetMapping
    List<CategoriaResponse> listar();
}
