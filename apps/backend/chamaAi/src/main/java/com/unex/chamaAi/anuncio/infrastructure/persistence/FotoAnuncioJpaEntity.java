package com.unex.chamaAi.anuncio.infrastructure.persistence;

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
@Table(name = "fotos_anuncio")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class FotoAnuncioJpaEntity {

    @Id
    private UUID id;

    @Column(name = "anuncio_id", nullable = false)
    private UUID anuncioId;

    @Column(name = "content_type", nullable = false, length = 50)
    private String contentType;

    /** Caminho relativo à pasta de fotos de anúncios (os bytes ficam no disco). */
    @Column(nullable = false, length = 300)
    private String caminho;

    @Column(name = "criado_em", nullable = false)
    private Instant criadoEm;
}
