package com.unex.chamaAi.proposta.infrastructure.web.dto.response;

import com.unex.chamaAi.proposta.application.dto.PropostaDetalhada;
import com.unex.chamaAi.shared.domain.paginacao.Pagina;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Página de propostas, da mais recente para a mais antiga.")
public record PaginaPropostasResponse(
        @ArraySchema(arraySchema = @Schema(description = "Propostas desta página."))
        List<PropostaResponse> itens,

        @Schema(description = "Número da página (começa em 0).", example = "0")
        int pagina,

        @Schema(description = "Itens por página.", example = "20")
        int tamanho,

        @Schema(description = "Total de propostas.", example = "3")
        long totalItens,

        @Schema(description = "Total de páginas.", example = "1")
        int totalPaginas) {

    public static PaginaPropostasResponse de(Pagina<PropostaDetalhada> pagina) {
        return new PaginaPropostasResponse(pagina.itens().stream().map(PropostaResponse::de).toList(),
                pagina.pagina(), pagina.tamanho(), pagina.totalItens(), pagina.totalPaginas());
    }
}
