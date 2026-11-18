package com.unex.chamaAi.anuncio.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CategoriaSpringDataRepository extends JpaRepository<CategoriaJpaEntity, UUID> {

    List<CategoriaJpaEntity> findAllByOrderByNomeAsc();
}
