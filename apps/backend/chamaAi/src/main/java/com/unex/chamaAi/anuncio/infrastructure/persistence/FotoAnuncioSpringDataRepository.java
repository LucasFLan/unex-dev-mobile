package com.unex.chamaAi.anuncio.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FotoAnuncioSpringDataRepository extends JpaRepository<FotoAnuncioJpaEntity, UUID> {

    List<FotoAnuncioJpaEntity> findByAnuncioIdOrderByCriadoEmAscIdAsc(UUID anuncioId);

    List<FotoAnuncioJpaEntity> findByAnuncioIdInOrderByCriadoEmAscIdAsc(Collection<UUID> anuncioIds);

    Optional<FotoAnuncioJpaEntity> findByIdAndAnuncioId(UUID id, UUID anuncioId);
}
