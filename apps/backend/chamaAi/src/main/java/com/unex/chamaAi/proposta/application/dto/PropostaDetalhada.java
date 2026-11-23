package com.unex.chamaAi.proposta.application.dto;

import com.unex.chamaAi.anuncio.domain.valueobjects.Preco;
import com.unex.chamaAi.proposta.domain.entities.Avaliacao;
import com.unex.chamaAi.proposta.domain.entities.Proposta;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Proposta com o anúncio e as duas pessoas envolvidas. valor nulo = "a combinar". O telefone de cada lado só
 * vem preenchido depois que a proposta é aceita. fotos: ids das fotos anexadas, na ordem do envio.
 * avaliacao: a nota que quem contratou deu, depois de concluído (nula enquanto não houver).
 */
public record PropostaDetalhada(UUID id, AnuncioResumo anuncio, ParticipanteResumo remetente,
                                ParticipanteResumo destinatario, BigDecimal valor, String mensagem, String status,
                                Instant criadoEm, Instant respondidoEm, Instant concluidoEm, List<UUID> fotos,
                                AvaliacaoResumo avaliacao, EnderecoServico endereco) {

    public record AnuncioResumo(UUID id, String titulo, String tipo) {
    }

    /** telefone nulo enquanto o contato não foi liberado (ou se a pessoa não informou). */
    public record ParticipanteResumo(UUID id, String nome, String telefone) {
    }

    public record AvaliacaoResumo(int nota, String comentario, Instant criadoEm) {

        public static AvaliacaoResumo de(Avaliacao avaliacao) {
            return avaliacao == null ? null : new AvaliacaoResumo(avaliacao.getNota(),
                    avaliacao.getComentario().orElse(null), avaliacao.getCriadoEm());
        }
    }

    /** Endereço completo do anúncio: só vai para as duas pessoas depois que a proposta é aceita. */
    public record EnderecoServico(String cep, String logradouro, String numero, String complemento, String bairro,
                                  String cidade, String uf, Double latitude, Double longitude) {
    }

    public static PropostaDetalhada de(Proposta proposta, AnuncioResumo anuncio, ParticipanteResumo remetente,
                                       ParticipanteResumo destinatario, List<UUID> fotos, Avaliacao avaliacao,
                                       EnderecoServico endereco) {
        return new PropostaDetalhada(proposta.getId(), anuncio, remetente, destinatario,
                proposta.getValor().map(Preco::valor).orElse(null), proposta.getMensagem(),
                proposta.getStatus().name(), proposta.getCriadoEm(), proposta.getRespondidoEm().orElse(null),
                proposta.getConcluidoEm().orElse(null), List.copyOf(fotos), AvaliacaoResumo.de(avaliacao),
                proposta.contatoLiberado() ? endereco : null);
    }
}
