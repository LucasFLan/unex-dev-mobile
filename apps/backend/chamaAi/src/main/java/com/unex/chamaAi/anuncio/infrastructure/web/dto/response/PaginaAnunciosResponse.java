package com.unex.chamaAi.anuncio.infrastructure.web.dto.response;

import com.unex.chamaAi.anuncio.application.dto.AnuncioDetalhado;
import com.unex.chamaAi.shared.domain.paginacao.Pagina;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Página de anúncios, do mais recente para o mais antigo.")
public record PaginaAnunciosResponse(
        @ArraySchema(arraySchema = @Schema(description = "Anúncios desta página."))
        List<AnuncioResponse> itens,

        @Schema(description = "Número da página (começa em 0).", example = "0")
        int pagina,

        @Schema(description = "Itens por página.", example = "20")
        int tamanho,

        @Schema(description = "Total de anúncios que atendem ao filtro.", example = "42")
        long totalItens,

        @Schema(description = "Total de páginas.", example = "3")
        int totalPaginas) {

    public static PaginaAnunciosResponse de(Pagina<AnuncioDetalhado> pagina) {
        return new PaginaAnunciosResponse(pagina.itens().stream().map(AnuncioResponse::de).toList(),
                pagina.pagina(), pagina.tamanho(), pagina.totalItens(), pagina.totalPaginas());
    }
}
