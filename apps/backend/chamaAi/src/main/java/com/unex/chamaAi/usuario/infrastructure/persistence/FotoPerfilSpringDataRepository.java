package com.unex.chamaAi.usuario.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FotoPerfilSpringDataRepository extends JpaRepository<FotoPerfilJpaEntity, UUID> {
}
