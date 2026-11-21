package com.unex.chamaAi.notificacao.infrastructure.web.dto.response;

import com.unex.chamaAi.notificacao.application.dto.NotificacaoDetalhada;
import com.unex.chamaAi.shared.domain.paginacao.Pagina;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Página de notificações, da mais recente para a mais antiga.")
public record PaginaNotificacoesResponse(
        @ArraySchema(arraySchema = @Schema(description = "Notificações desta página."))
        List<NotificacaoResponse> itens,

        @Schema(description = "Número da página (começa em 0).", example = "0")
        int pagina,

        @Schema(description = "Itens por página.", example = "20")
        int tamanho,

        @Schema(description = "Total de notificações.", example = "5")
        long totalItens,

        @Schema(description = "Total de páginas.", example = "1")
        int totalPaginas) {

    public static PaginaNotificacoesResponse de(Pagina<NotificacaoDetalhada> pagina) {
        return new PaginaNotificacoesResponse(pagina.itens().stream().map(NotificacaoResponse::de).toList(),
                pagina.pagina(), pagina.tamanho(), pagina.totalItens(), pagina.totalPaginas());
    }
}
