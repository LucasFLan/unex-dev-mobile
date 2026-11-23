package com.unex.chamaAi.usuario.infrastructure.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EnvioValidacaoSpringDataRepository extends JpaRepository<EnvioValidacaoJpaEntity, UUID> {

    Optional<EnvioValidacaoJpaEntity> findFirstByUsuarioIdOrderByEnviadoEmDesc(UUID usuarioId);

    Page<EnvioValidacaoJpaEntity> findByStatus(String status, Pageable pageable);
}
