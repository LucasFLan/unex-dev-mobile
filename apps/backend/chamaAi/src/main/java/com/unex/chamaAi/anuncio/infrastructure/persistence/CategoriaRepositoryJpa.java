package com.unex.chamaAi.anuncio.infrastructure.persistence;

import com.unex.chamaAi.anuncio.domain.entities.Categoria;
import com.unex.chamaAi.anuncio.domain.repositories.CategoriaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class CategoriaRepositoryJpa implements CategoriaRepository {

    private final CategoriaSpringDataRepository springData;

    public CategoriaRepositoryJpa(CategoriaSpringDataRepository springData) {
        this.springData = springData;
    }

    @Override
    public List<Categoria> listarTodas() {
        return springData.findAllByOrderByNomeAsc().stream().map(CategoriaRepositoryJpa::paraDominio).toList();
    }

    @Override
    public Optional<Categoria> buscarPorId(UUID id) {
        return springData.findById(id).map(CategoriaRepositoryJpa::paraDominio);
    }

    private static Categoria paraDominio(CategoriaJpaEntity entidade) {
        return new Categoria(entidade.getId(), entidade.getSlug(), entidade.getNome());
    }
}
