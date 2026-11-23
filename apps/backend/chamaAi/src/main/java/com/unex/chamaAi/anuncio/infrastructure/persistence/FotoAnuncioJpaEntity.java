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
@Table(name = "anuncios_fotos")
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

    @Column(name = "criado_em", nullable = false)
    private Instant criadoEm;
}
