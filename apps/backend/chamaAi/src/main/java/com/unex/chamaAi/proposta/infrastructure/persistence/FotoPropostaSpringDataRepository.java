package com.unex.chamaAi.proposta.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FotoPropostaSpringDataRepository extends JpaRepository<FotoPropostaJpaEntity, UUID> {

    List<FotoPropostaJpaEntity> findByPropostaIdInOrderByPropostaIdAscOrdemAsc(Collection<UUID> propostaIds);

    Optional<FotoPropostaJpaEntity> findByIdAndPropostaId(UUID id, UUID propostaId);
}
