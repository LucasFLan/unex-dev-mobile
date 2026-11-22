package com.unex.chamaAi.usuario.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CodigoRecuperacaoSenhaSpringDataRepository
        extends JpaRepository<CodigoRecuperacaoSenhaJpaEntity, UUID> {

    Optional<CodigoRecuperacaoSenhaJpaEntity> findFirstByUsuarioIdOrderByCriadoEmDesc(UUID usuarioId);
}
