package com.unex.chamaAi.proposta.infrastructure.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface PropostaSpringDataRepository extends JpaRepository<PropostaJpaEntity, UUID> {

    boolean existsByAnuncioIdAndRemetenteIdAndStatusIn(UUID anuncioId, UUID remetenteId, Collection<String> status);

    List<PropostaJpaEntity> findByAnuncioIdAndStatus(UUID anuncioId, String status);

    Page<PropostaJpaEntity> findByDestinatarioId(UUID destinatarioId, Pageable pageable);

    Page<PropostaJpaEntity> findByRemetenteId(UUID remetenteId, Pageable pageable);
}
