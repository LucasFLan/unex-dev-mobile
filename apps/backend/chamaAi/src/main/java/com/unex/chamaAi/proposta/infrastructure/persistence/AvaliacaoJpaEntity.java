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
@Table(name = "avaliacoes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class AvaliacaoJpaEntity {

    @Id
    private UUID id;

    @Column(name = "proposta_id")
    private UUID propostaId;

    @Column(name = "avaliador_id")
    private UUID avaliadorId;

    @Column(name = "avaliado_id", nullable = false)
    private UUID avaliadoId;

    @Column(nullable = false)
    private short nota;

    @Column(length = 500)
    private String comentario;

    @Column(name = "servico_titulo", nullable = false, length = 120)
    private String servicoTitulo;

    @Column(name = "servico_categoria", length = 100)
    private String servicoCategoria;

    @Column(name = "criado_em", nullable = false)
    private Instant criadoEm;
}
