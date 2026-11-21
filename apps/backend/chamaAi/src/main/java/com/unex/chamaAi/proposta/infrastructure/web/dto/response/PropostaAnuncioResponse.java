package com.unex.chamaAi.proposta.infrastructure.web.dto.response;

import com.unex.chamaAi.proposta.application.dto.PropostaDetalhada.AnuncioResumo;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Anúncio ao qual a proposta se refere.")
public record PropostaAnuncioResponse(
        @Schema(description = "Id do anúncio.", example = "7a1c9e2b-4d3f-4b8a-9e6d-2c5f8a1b3d4e")
        UUID id,

        @Schema(description = "Título do anúncio.", example = "Preciso trocar um chuveiro")
        String titulo,

        @Schema(description = "`OFERTA` ou `PEDIDO`.", example = "PEDIDO", allowableValues = {"OFERTA", "PEDIDO"})
        String tipo) {

    public static PropostaAnuncioResponse de(AnuncioResumo anuncio) {
        return anuncio == null ? null : new PropostaAnuncioResponse(anuncio.id(), anuncio.titulo(), anuncio.tipo());
    }
}
