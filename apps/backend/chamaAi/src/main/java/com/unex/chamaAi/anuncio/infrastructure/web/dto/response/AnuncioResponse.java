package com.unex.chamaAi.anuncio.infrastructure.web.dto.response;

import com.unex.chamaAi.anuncio.application.dto.AnuncioDetalhado;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Schema(description = "Anúncio com a categoria detalhada.")
public record AnuncioResponse(
        @Schema(description = "Id do anúncio.", example = "7a1c9e2b-4d3f-4b8a-9e6d-2c5f8a1b3d4e")
        UUID id,

        @Schema(description = "`OFERTA` ou `PEDIDO`.", example = "OFERTA", allowableValues = {"OFERTA", "PEDIDO"})
        String tipo,

        @Schema(description = "Título.", example = "Eletricista residencial")
        String titulo,

        @Schema(description = "Descrição.", example = "Instalação de chuveiros, tomadas e quadros de luz.")
        String descricao,

        @Schema(description = "Valor em reais. `null` = \"a combinar\".", example = "150.00", nullable = true)
        BigDecimal preco,

        @Schema(description = "Categoria do anúncio.")
        CategoriaResponse categoria,

        @Schema(description = "Cidade.", example = "Salvador")
        String cidade,

        @Schema(description = "UF, sempre em maiúsculas.", example = "BA")
        String uf,

        @Schema(description = "Se aparece na listagem pública. Inativos só são vistos pelo autor.", example = "true")
        boolean ativo,

        @Schema(description = "Id do usuário que criou o anúncio.", example = "3f2b6c1e-8a4d-4e0b-9c7a-1d2e3f4a5b6c")
        UUID autorId,

        @Schema(description = "Data de criação (ISO-8601, UTC).", example = "2026-11-16T21:30:00.123456Z")
        Instant criadoEm,

        @Schema(description = "Data da última edição (ISO-8601, UTC).", example = "2026-11-16T21:30:00.123456Z")
        Instant atualizadoEm,

        @Schema(description = "Fotos do anúncio, na ordem em que foram enviadas (até 6).")
        List<FotoAnuncioResponse> fotos,

        @Schema(description = "Caminho da foto de perfil do autor na API. `null` = autor sem foto.",
                example = "/usuarios/3f2b6c1e-8a4d-4e0b-9c7a-1d2e3f4a5b6c/foto?v=1795454362205", nullable = true)
        String fotoAutor) {

    public static AnuncioResponse de(AnuncioDetalhado anuncio) {
        return new AnuncioResponse(anuncio.id(), anuncio.tipo(), anuncio.titulo(), anuncio.descricao(), anuncio.preco(),
                CategoriaResponse.de(anuncio.categoria()), anuncio.cidade(), anuncio.uf(), anuncio.ativo(),
                anuncio.autorId(), anuncio.criadoEm(), anuncio.atualizadoEm(),
                anuncio.fotos().stream().map(FotoAnuncioResponse::de).toList(), anuncio.fotoAutor());
    }
}
