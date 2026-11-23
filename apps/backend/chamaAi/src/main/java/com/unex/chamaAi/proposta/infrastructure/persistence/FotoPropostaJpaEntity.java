package com.unex.chamaAi.proposta.infrastructure.persistence;

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
@Table(name = "propostas_fotos")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class FotoPropostaJpaEntity {

    @Id
    private UUID id;

    @Column(name = "proposta_id", nullable = false)
    private UUID propostaId;

    @Column(nullable = false)
    private short ordem;

    @Column(name = "content_type", nullable = false, length = 50)
    private String contentType;

    @Column(name = "criado_em", nullable = false)
    private Instant criadoEm;
}
