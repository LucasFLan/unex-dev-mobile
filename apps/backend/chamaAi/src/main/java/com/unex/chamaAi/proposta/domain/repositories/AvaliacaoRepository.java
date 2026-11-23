package com.unex.chamaAi.proposta.domain.repositories;

import com.unex.chamaAi.proposta.domain.entities.Avaliacao;
import com.unex.chamaAi.shared.domain.paginacao.Pagina;
import com.unex.chamaAi.shared.domain.paginacao.Paginacao;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public interface AvaliacaoRepository {

    void salvar(Avaliacao avaliacao);

    boolean existePorProposta(UUID propostaId);

    /** Avaliação de cada proposta, quando houver. Propostas sem avaliação ficam de fora do mapa. */
    Map<UUID, Avaliacao> buscarPorPropostas(Collection<UUID> propostaIds);

    /** Avaliações recebidas por um usuário, da mais recente para a mais antiga. */
    Pagina<Avaliacao> buscarPorAvaliado(UUID avaliadoId, Paginacao paginacao);

    /** Média e total das avaliações recebidas. */
    ResumoAvaliacoes resumo(UUID avaliadoId);

    /** media vazia quando não há avaliações. */
    record ResumoAvaliacoes(Double media, long total) {

        public static final ResumoAvaliacoes VAZIO = new ResumoAvaliacoes(null, 0);
    }
}
