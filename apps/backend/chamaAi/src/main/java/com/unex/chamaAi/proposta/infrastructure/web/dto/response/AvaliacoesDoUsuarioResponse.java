package com.unex.chamaAi.proposta.infrastructure.web.dto.response;

import com.unex.chamaAi.proposta.application.dto.AvaliacoesDoUsuario;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Schema(description = "Reputação de um usuário: média, total e uma página das avaliações recebidas.")
public record AvaliacoesDoUsuarioResponse(
        @Schema(description = "Id do usuário avaliado.") UUID usuarioId,
        @Schema(description = "Nome do usuário.", example = "Maria Souza") String nome,
        @Schema(description = "Versão da foto de perfil; `null` = sem foto.", nullable = true) Instant fotoAtualizadaEm,
        @Schema(description = "Média das notas, com uma casa decimal. `null` sem avaliações.", example = "4.8",
                nullable = true) Double media,
        @Schema(description = "Quantidade de avaliações.", example = "12") long total,
        @Schema(description = "Avaliações desta página.") List<AvaliacaoResponse> itens,
        @Schema(example = "0") int pagina,
        @Schema(example = "20") int tamanho,
        @Schema(example = "12") long totalItens,
        @Schema(example = "1") int totalPaginas) {

    public static AvaliacoesDoUsuarioResponse de(AvaliacoesDoUsuario dados) {
        var pagina = dados.avaliacoes();
        Double media = dados.resumo().media() == null ? null : Math.round(dados.resumo().media() * 10) / 10.0;
        return new AvaliacoesDoUsuarioResponse(dados.usuarioId(), dados.nome(), dados.fotoAtualizadaEm(), media,
                dados.resumo().total(), pagina.itens().stream().map(AvaliacaoResponse::de).toList(), pagina.pagina(),
                pagina.tamanho(), pagina.totalItens(), pagina.totalPaginas());
    }
}
