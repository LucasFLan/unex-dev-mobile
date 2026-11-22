package com.unex.chamaAi.notificacao.infrastructure.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public interface NotificacaoSpringDataRepository extends JpaRepository<NotificacaoJpaEntity, UUID> {

    Page<NotificacaoJpaEntity> findByUsuarioId(UUID usuarioId, Pageable pageable);

    long countByUsuarioIdAndLidaFalse(UUID usuarioId);

    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update NotificacaoJpaEntity n set n.lida = true where n.usuarioId = :usuarioId and n.lida = false")
    int marcarTodasComoLidas(@Param("usuarioId") UUID usuarioId);
}
