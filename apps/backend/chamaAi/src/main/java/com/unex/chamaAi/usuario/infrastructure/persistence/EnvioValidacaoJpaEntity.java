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
@Table(name = "envios_validacao")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class EnvioValidacaoJpaEntity {

    @Id
    private UUID id;

    @Column(name = "usuario_id", nullable = false)
    private UUID usuarioId;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(name = "motivo_reprovacao", length = 500)
    private String motivoReprovacao;

    @Column(name = "enviado_em", nullable = false)
    private Instant enviadoEm;

    @Column(name = "analisado_em")
    private Instant analisadoEm;

    @Column(name = "analisado_por")
    private UUID analisadoPor;
}
