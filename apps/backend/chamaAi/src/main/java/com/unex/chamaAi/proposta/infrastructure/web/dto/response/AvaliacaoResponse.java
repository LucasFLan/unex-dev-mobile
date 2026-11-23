package com.unex.chamaAi.proposta.infrastructure.web.dto.response;

import com.unex.chamaAi.proposta.application.dto.AvaliacaoDetalhada;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "Avaliação de um serviço concluído.")
public record AvaliacaoResponse(
        @Schema(description = "Id da avaliação.") UUID id,
        @Schema(description = "Proposta avaliada. `null` se o anúncio foi apagado.", nullable = true) UUID propostaId,
        @Schema(description = "Nota de 1 a 5.", example = "5") int nota,
        @Schema(description = "Comentário.", example = "Pontual e caprichoso.", nullable = true) String comentario,
        @Schema(description = "Título do serviço avaliado.", example = "Trocar chuveiro") String servicoTitulo,
        @Schema(description = "Categoria do serviço.", example = "Eletricista", nullable = true) String servicoCategoria,
        @Schema(description = "Quem avaliou. `null` se a pessoa apagou a conta.", nullable = true) Autor autor,
        @Schema(description = "Data (ISO-8601, UTC).") Instant criadoEm) {

    public record Autor(UUID id, String nome,
                        @Schema(description = "Versão da foto de perfil; `null` = sem foto.", nullable = true)
                        Instant fotoAtualizadaEm) {
    }

    public static AvaliacaoResponse de(AvaliacaoDetalhada avaliacao) {
        return new AvaliacaoResponse(avaliacao.id(), avaliacao.propostaId(), avaliacao.nota(), avaliacao.comentario(),
                avaliacao.servicoTitulo(), avaliacao.servicoCategoria(),
                avaliacao.autor() == null ? null : new Autor(avaliacao.autor().id(), avaliacao.autor().nome(),
                        avaliacao.autor().fotoAtualizadaEm()),
                avaliacao.criadoEm());
    }
}
