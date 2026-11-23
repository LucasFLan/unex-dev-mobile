package com.unex.chamaAi.usuario.infrastructure.web.dto.response;

import com.unex.chamaAi.shared.domain.paginacao.Pagina;
import com.unex.chamaAi.usuario.application.dto.EnvioParaAnalise;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Página de envios de validação. Pendentes: do mais antigo ao mais novo (ordem da fila).")
public record PaginaEnviosValidacaoResponse(
        @ArraySchema(arraySchema = @Schema(description = "Envios desta página."))
        List<EnvioValidacaoResponse> itens,

        @Schema(description = "Número da página (começa em 0).", example = "0")
        int pagina,

        @Schema(description = "Itens por página.", example = "20")
        int tamanho,

        @Schema(description = "Total de envios com esse status.", example = "3")
        long totalItens,

        @Schema(description = "Total de páginas.", example = "1")
        int totalPaginas) {

    public static PaginaEnviosValidacaoResponse de(Pagina<EnvioParaAnalise> pagina) {
        return new PaginaEnviosValidacaoResponse(pagina.itens().stream().map(EnvioValidacaoResponse::de).toList(),
                pagina.pagina(), pagina.tamanho(), pagina.totalItens(), pagina.totalPaginas());
    }
}
