package com.unex.chamaAi.proposta.application.dto;

import com.unex.chamaAi.proposta.domain.repositories.AvaliacaoRepository.ResumoAvaliacoes;
import com.unex.chamaAi.shared.domain.paginacao.Pagina;

import java.time.Instant;
import java.util.UUID;

/** Perfil público de reputação: quem é, a média, o total e uma página das avaliações recebidas. */
public record AvaliacoesDoUsuario(UUID usuarioId, String nome, Instant fotoAtualizadaEm, ResumoAvaliacoes resumo,
                                  Pagina<AvaliacaoDetalhada> avaliacoes) {
}
