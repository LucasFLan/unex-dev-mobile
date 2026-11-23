package com.unex.chamaAi.anuncio.infrastructure.persistence;

import com.unex.chamaAi.anuncio.domain.entities.Anuncio;
import com.unex.chamaAi.anuncio.domain.repositories.AnuncioRepository;
import com.unex.chamaAi.anuncio.domain.repositories.FiltroAnuncios;
import com.unex.chamaAi.anuncio.domain.valueobjects.Coordenadas;
import com.unex.chamaAi.anuncio.domain.valueobjects.Endereco;
import com.unex.chamaAi.anuncio.domain.valueobjects.Localizacao;
import com.unex.chamaAi.anuncio.domain.valueobjects.Preco;
import com.unex.chamaAi.anuncio.domain.valueobjects.TipoAnuncio;
import com.unex.chamaAi.shared.domain.paginacao.Pagina;
import com.unex.chamaAi.shared.domain.paginacao.Paginacao;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@Repository
public class AnuncioRepositoryJpa implements AnuncioRepository {

    /** id como desempate garante ordem estável entre páginas quando criado_em coincide. */
    private static final Sort MAIS_RECENTES = Sort.by(Sort.Order.desc("criadoEm"), Sort.Order.desc("id"));

    private final AnuncioSpringDataRepository springData;

    public AnuncioRepositoryJpa(AnuncioSpringDataRepository springData) {
        this.springData = springData;
    }

    @Override
    public void salvar(Anuncio anuncio) {
        springData.saveAndFlush(paraEntidade(anuncio));
    }

    @Override
    public Optional<Anuncio> buscarPorId(UUID id) {
        return springData.findById(id).map(AnuncioRepositoryJpa::paraDominio);
    }

    @Override
    public Pagina<Anuncio> buscarAtivos(FiltroAnuncios filtro, Paginacao paginacao) {
        return paraPagina(springData.findAll(especificacao(filtro), paraPageable(paginacao)));
    }

    @Override
    public Pagina<Anuncio> buscarPorAutor(UUID autorId, Paginacao paginacao) {
        return paraPagina(springData.findByAutorId(autorId, paraPageable(paginacao)));
    }

    @Override
    public void excluir(UUID id) {
        springData.deleteById(id);
    }

    private static Specification<AnuncioJpaEntity> especificacao(FiltroAnuncios filtro) {
        return (raiz, consulta, cb) -> {
            List<Predicate> condicoes = new ArrayList<>();
            condicoes.add(cb.isTrue(raiz.get("ativo")));
            if (filtro.tipo() != null) {
                condicoes.add(cb.equal(raiz.get("tipo"), filtro.tipo().name()));
            }
            if (filtro.categoriaId() != null) {
                condicoes.add(cb.equal(raiz.get("categoriaId"), filtro.categoriaId()));
            }
            if (filtro.uf() != null) {
                condicoes.add(cb.equal(raiz.get("uf"), filtro.uf()));
            }
            if (filtro.cidade() != null) {
                condicoes.add(cb.equal(cb.lower(raiz.get("cidade")), filtro.cidade().toLowerCase(Locale.ROOT)));
            }
            return cb.and(condicoes.toArray(Predicate[]::new));
        };
    }

    private static Pageable paraPageable(Paginacao paginacao) {
        return PageRequest.of(paginacao.pagina(), paginacao.tamanho(), MAIS_RECENTES);
    }

    private static Pagina<Anuncio> paraPagina(Page<AnuncioJpaEntity> pagina) {
        return new Pagina<>(pagina.getContent().stream().map(AnuncioRepositoryJpa::paraDominio).toList(),
                pagina.getNumber(), pagina.getSize(), pagina.getTotalElements(), pagina.getTotalPages());
    }

    private static AnuncioJpaEntity paraEntidade(Anuncio anuncio) {
        Endereco endereco = anuncio.getEndereco().orElse(null);
        Coordenadas ponto = endereco == null ? null : endereco.coordenadas();
        return new AnuncioJpaEntity(anuncio.getId(), anuncio.getAutorId(), anuncio.getCategoriaId(),
                anuncio.getTipo().name(), anuncio.getTitulo(), anuncio.getDescricao(),
                anuncio.getPreco().map(Preco::valor).orElse(null), anuncio.getLocalizacao().cidade(),
                anuncio.getLocalizacao().uf(),
                endereco == null ? null : endereco.cep(), endereco == null ? null : endereco.logradouro(),
                endereco == null ? null : endereco.numero(), endereco == null ? null : endereco.complemento(),
                endereco == null ? null : endereco.bairro(),
                ponto == null ? null : ponto.latitude(), ponto == null ? null : ponto.longitude(),
                anuncio.getRaioAtendimentoKm().map(Integer::shortValue).orElse(null),
                anuncio.isAtivo(), anuncio.getCriadoEm(), anuncio.getAtualizadoEm());
    }

    private static Anuncio paraDominio(AnuncioJpaEntity entidade) {
        return Anuncio.reconstituir(entidade.getId(), entidade.getAutorId(), entidade.getCategoriaId(),
                TipoAnuncio.valueOf(entidade.getTipo()), entidade.getTitulo(), entidade.getDescricao(),
                Preco.deOuNulo(entidade.getPreco()), new Localizacao(entidade.getCidade(), entidade.getUf()),
                endereco(entidade), entidade.getRaioAtendimentoKm() == null ? null : entidade.getRaioAtendimentoKm().intValue(),
                entidade.isAtivo(), entidade.getCriadoEm(), entidade.getAtualizadoEm());
    }

    private static Endereco endereco(AnuncioJpaEntity entidade) {
        if (entidade.getCep() == null) {
            return null;
        }
        Coordenadas ponto = entidade.getLatitude() == null || entidade.getLongitude() == null ? null
                : new Coordenadas(entidade.getLatitude(), entidade.getLongitude());
        return new Endereco(entidade.getCep(), entidade.getLogradouro(), entidade.getNumero(),
                entidade.getComplemento(), entidade.getBairro(), ponto);
    }
}
