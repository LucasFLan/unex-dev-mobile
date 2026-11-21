package com.unex.chamaAi.proposta.infrastructure.persistence;

import com.unex.chamaAi.anuncio.domain.valueobjects.Preco;
import com.unex.chamaAi.proposta.domain.entities.Proposta;
import com.unex.chamaAi.proposta.domain.repositories.PropostaRepository;
import com.unex.chamaAi.proposta.domain.valueobjects.StatusProposta;
import com.unex.chamaAi.shared.domain.paginacao.Pagina;
import com.unex.chamaAi.shared.domain.paginacao.Paginacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class PropostaRepositoryJpa implements PropostaRepository {

    /** id como desempate garante ordem estável entre páginas quando criado_em coincide. */
    private static final Sort MAIS_RECENTES = Sort.by(Sort.Order.desc("criadoEm"), Sort.Order.desc("id"));

    private final PropostaSpringDataRepository springData;

    public PropostaRepositoryJpa(PropostaSpringDataRepository springData) {
        this.springData = springData;
    }

    @Override
    public void salvar(Proposta proposta) {
        springData.saveAndFlush(new PropostaJpaEntity(proposta.getId(), proposta.getAnuncioId(),
                proposta.getRemetenteId(), proposta.getDestinatarioId(),
                proposta.getValor().map(Preco::valor).orElse(null), proposta.getMensagem(),
                proposta.getStatus().name(), proposta.getCriadoEm(), proposta.getRespondidoEm().orElse(null)));
    }

    @Override
    public Optional<Proposta> buscarPorId(UUID id) {
        return springData.findById(id).map(PropostaRepositoryJpa::paraDominio);
    }

    @Override
    public List<Proposta> buscarPendentesPorAnuncio(UUID anuncioId) {
        return springData.findByAnuncioIdAndStatus(anuncioId, StatusProposta.PENDENTE.name()).stream()
                .map(PropostaRepositoryJpa::paraDominio).toList();
    }

    @Override
    public boolean existePorAnuncioERemetente(UUID anuncioId, UUID remetenteId) {
        return springData.existsByAnuncioIdAndRemetenteId(anuncioId, remetenteId);
    }

    @Override
    public Pagina<Proposta> buscarPorDestinatario(UUID destinatarioId, Paginacao paginacao) {
        return paraPagina(springData.findByDestinatarioId(destinatarioId, paraPageable(paginacao)));
    }

    @Override
    public Pagina<Proposta> buscarPorRemetente(UUID remetenteId, Paginacao paginacao) {
        return paraPagina(springData.findByRemetenteId(remetenteId, paraPageable(paginacao)));
    }

    private static Pageable paraPageable(Paginacao paginacao) {
        return PageRequest.of(paginacao.pagina(), paginacao.tamanho(), MAIS_RECENTES);
    }

    private static Pagina<Proposta> paraPagina(Page<PropostaJpaEntity> pagina) {
        return new Pagina<>(pagina.getContent().stream().map(PropostaRepositoryJpa::paraDominio).toList(),
                pagina.getNumber(), pagina.getSize(), pagina.getTotalElements(), pagina.getTotalPages());
    }

    private static Proposta paraDominio(PropostaJpaEntity entidade) {
        return Proposta.reconstituir(entidade.getId(), entidade.getAnuncioId(), entidade.getRemetenteId(),
                entidade.getDestinatarioId(), Preco.deOuNulo(entidade.getValor()), entidade.getMensagem(),
                StatusProposta.valueOf(entidade.getStatus()), entidade.getCriadoEm(), entidade.getRespondidoEm());
    }
}
