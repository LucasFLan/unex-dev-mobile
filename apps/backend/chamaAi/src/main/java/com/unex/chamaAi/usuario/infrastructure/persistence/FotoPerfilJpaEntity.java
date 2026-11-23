package com.unex.chamaAi.usuario.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "fotos_perfil")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class FotoPerfilJpaEntity {

    @Id
    @Column(name = "usuario_id")
    private UUID usuarioId;

    @Column(name = "content_type", nullable = false, length = 50)
    private String contentType;

    /** Caminho relativo à pasta de fotos de perfil (os bytes ficam no disco). */
    @Column(nullable = false, length = 300)
    private String caminho;

    @Column(name = "atualizado_em", nullable = false)
    private Instant atualizadoEm;
}
