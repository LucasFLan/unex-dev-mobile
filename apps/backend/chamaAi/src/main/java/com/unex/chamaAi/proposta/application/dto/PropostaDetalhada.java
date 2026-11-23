package com.unex.chamaAi.proposta.application.dto;

import com.unex.chamaAi.anuncio.domain.valueobjects.Preco;
import com.unex.chamaAi.proposta.domain.entities.Proposta;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Proposta com o anúncio e as duas pessoas envolvidas. valor nulo = "a combinar". O telefone de cada lado só
 * vem preenchido depois que a proposta é aceita. fotos: ids das fotos anexadas, na ordem do envio.
 */
public record PropostaDetalhada(UUID id, AnuncioResumo anuncio, ParticipanteResumo remetente,
                                ParticipanteResumo destinatario, BigDecimal valor, String mensagem, String status,
                                Instant criadoEm, Instant respondidoEm, List<UUID> fotos) {

    public record AnuncioResumo(UUID id, String titulo, String tipo) {
    }

    /** telefone nulo enquanto o contato não foi liberado (ou se a pessoa não informou). */
    public record ParticipanteResumo(UUID id, String nome, String telefone) {
    }

    public static PropostaDetalhada de(Proposta proposta, AnuncioResumo anuncio, ParticipanteResumo remetente,
                                       ParticipanteResumo destinatario, List<UUID> fotos) {
        return new PropostaDetalhada(proposta.getId(), anuncio, remetente, destinatario,
                proposta.getValor().map(Preco::valor).orElse(null), proposta.getMensagem(),
                proposta.getStatus().name(), proposta.getCriadoEm(), proposta.getRespondidoEm().orElse(null),
                List.copyOf(fotos));
    }
}
