package com.unex.chamaAi.proposta.infrastructure.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface AvaliacaoSpringDataRepository extends JpaRepository<AvaliacaoJpaEntity, UUID> {

    boolean existsByPropostaId(UUID propostaId);

    List<AvaliacaoJpaEntity> findByPropostaIdIn(Collection<UUID> propostaIds);

    Page<AvaliacaoJpaEntity> findByAvaliadoIdOrderByCriadoEmDesc(UUID avaliadoId, Pageable pageable);

    /** [média, total] das notas recebidas. */
    @Query("select avg(a.nota), count(a) from AvaliacaoJpaEntity a where a.avaliadoId = :avaliadoId")
    List<Object[]> resumo(UUID avaliadoId);
}
