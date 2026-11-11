package com.unex.chamaAi.usuario.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UsuarioSpringDataRepository extends JpaRepository<UsuarioJpaEntity, UUID> {

    boolean existsByCpf(String cpf);

    boolean existsByEmail(String email);

    Optional<UsuarioJpaEntity> findByCpf(String cpf);
}
