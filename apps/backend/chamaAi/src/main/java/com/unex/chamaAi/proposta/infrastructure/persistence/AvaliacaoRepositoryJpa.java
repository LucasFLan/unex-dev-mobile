package com.unex.chamaAi.proposta.infrastructure.persistence;

import com.unex.chamaAi.proposta.domain.entities.Avaliacao;
import com.unex.chamaAi.proposta.domain.exceptions.AvaliacaoDuplicadaException;
import com.unex.chamaAi.proposta.domain.repositories.AvaliacaoRepository;
import com.unex.chamaAi.shared.domain.paginacao.Pagina;
import com.unex.chamaAi.shared.domain.paginacao.Paginacao;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
public class AvaliacaoRepositoryJpa implements AvaliacaoRepository {

    private static final String CONSTRAINT_PROPOSTA = "uk_avaliacoes_proposta";

    private final AvaliacaoSpringDataRepository springData;

    public AvaliacaoRepositoryJpa(AvaliacaoSpringDataRepository springData) {
        this.springData = springData;
    }

    @Override
    public void salvar(Avaliacao avaliacao) {
        try {
            springData.saveAndFlush(new AvaliacaoJpaEntity(avaliacao.getId(), avaliacao.getPropostaId().orElse(null),
                    avaliacao.getAvaliadorId().orElse(null), avaliacao.getAvaliadoId(), (short) avaliacao.getNota(),
                    avaliacao.getComentario().orElse(null), avaliacao.getServicoTitulo(),
                    avaliacao.getServicoCategoria().orElse(null), avaliacao.getCriadoEm()));
        } catch (DataIntegrityViolationException e) {
            // Duas avaliações ao mesmo tempo para a mesma proposta: a segunda esbarra na constraint.
            if (String.valueOf(e.getMostSpecificCause().getMessage()).contains(CONSTRAINT_PROPOSTA)) {
                throw new AvaliacaoDuplicadaException();
            }
            throw e;
        }
    }

    @Override
    public boolean existePorProposta(UUID propostaId) {
        return springData.existsByPropostaId(propostaId);
    }

    @Override
    public Map<UUID, Avaliacao> buscarPorPropostas(Collection<UUID> propostaIds) {
        return springData.findByPropostaIdIn(propostaIds).stream()
                .map(AvaliacaoRepositoryJpa::paraDominio)
                .collect(Collectors.toMap(avaliacao -> avaliacao.getPropostaId().orElseThrow(), Function.identity()));
    }

    @Override
    public Pagina<Avaliacao> buscarPorAvaliado(UUID avaliadoId, Paginacao paginacao) {
        Page<AvaliacaoJpaEntity> pagina = springData.findByAvaliadoIdOrderByCriadoEmDesc(avaliadoId,
                PageRequest.of(paginacao.pagina(), paginacao.tamanho()));
        return new Pagina<>(pagina.getContent().stream().map(AvaliacaoRepositoryJpa::paraDominio).toList(),
                pagina.getNumber(), pagina.getSize(), pagina.getTotalElements(), pagina.getTotalPages());
    }

    @Override
    public ResumoAvaliacoes resumo(UUID avaliadoId) {
        List<Object[]> linhas = springData.resumo(avaliadoId);
        if (linhas.isEmpty() || linhas.getFirst()[1] == null || ((Number) linhas.getFirst()[1]).longValue() == 0) {
            return ResumoAvaliacoes.VAZIO;
        }
        Object[] linha = linhas.getFirst();
        return new ResumoAvaliacoes(((Number) linha[0]).doubleValue(), ((Number) linha[1]).longValue());
    }

    private static Avaliacao paraDominio(AvaliacaoJpaEntity entidade) {
        return Avaliacao.reconstituir(entidade.getId(), entidade.getPropostaId(), entidade.getAvaliadorId(),
                entidade.getAvaliadoId(), entidade.getNota(), entidade.getComentario(), entidade.getServicoTitulo(),
                entidade.getServicoCategoria(), entidade.getCriadoEm());
    }
}
