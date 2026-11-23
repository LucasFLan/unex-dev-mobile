package com.unex.chamaAi.proposta.application.usecases;

import com.unex.chamaAi.proposta.domain.entities.Avaliacao;
import com.unex.chamaAi.proposta.domain.repositories.AvaliacaoRepository;
import com.unex.chamaAi.shared.domain.paginacao.Pagina;
import com.unex.chamaAi.shared.domain.paginacao.Paginacao;

import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/** Avaliações em memória, para os testes de caso de uso. */
class AvaliacoesEmMemoria implements AvaliacaoRepository {

    final Map<UUID, Avaliacao> dados = new LinkedHashMap<>();

    @Override
    public void salvar(Avaliacao avaliacao) {
        dados.put(avaliacao.getId(), avaliacao);
    }

    @Override
    public boolean existePorProposta(UUID propostaId) {
        return dados.values().stream().anyMatch(a -> a.getPropostaId().map(propostaId::equals).orElse(false));
    }

    @Override
    public Map<UUID, Avaliacao> buscarPorPropostas(Collection<UUID> propostaIds) {
        return dados.values().stream()
                .filter(a -> a.getPropostaId().map(propostaIds::contains).orElse(false))
                .collect(Collectors.toMap(a -> a.getPropostaId().orElseThrow(), Function.identity()));
    }

    @Override
    public Pagina<Avaliacao> buscarPorAvaliado(UUID avaliadoId, Paginacao paginacao) {
        List<Avaliacao> todas = dados.values().stream().filter(a -> a.getAvaliadoId().equals(avaliadoId))
                .sorted(Comparator.comparing(Avaliacao::getCriadoEm).reversed()).toList();
        int inicio = Math.min(paginacao.pagina() * paginacao.tamanho(), todas.size());
        int fim = Math.min(inicio + paginacao.tamanho(), todas.size());
        int totalPaginas = (int) Math.ceil(todas.size() / (double) paginacao.tamanho());
        return new Pagina<>(todas.subList(inicio, fim), paginacao.pagina(), paginacao.tamanho(), todas.size(),
                totalPaginas);
    }

    @Override
    public ResumoAvaliacoes resumo(UUID avaliadoId) {
        var notas = dados.values().stream().filter(a -> a.getAvaliadoId().equals(avaliadoId))
                .mapToInt(Avaliacao::getNota).summaryStatistics();
        return notas.getCount() == 0 ? ResumoAvaliacoes.VAZIO : new ResumoAvaliacoes(notas.getAverage(), notas.getCount());
    }
}
