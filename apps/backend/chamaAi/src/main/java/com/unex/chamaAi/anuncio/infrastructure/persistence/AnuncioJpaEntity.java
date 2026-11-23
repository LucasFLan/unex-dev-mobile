package com.unex.chamaAi.anuncio.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "anuncios")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class AnuncioJpaEntity {

    @Id
    private UUID id;

    @Column(name = "autor_id", nullable = false)
    private UUID autorId;

    @Column(name = "categoria_id", nullable = false)
    private UUID categoriaId;

    @Column(nullable = false, length = 20)
    private String tipo;

    @Column(nullable = false, length = 120)
    private String titulo;

    @Column(nullable = false)
    private String descricao;

    @Column(precision = 10, scale = 2)
    private BigDecimal preco;

    @Column(nullable = false, length = 100)
    private String cidade;

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(nullable = false, length = 2)
    private String uf;

    @Column(length = 8)
    private String cep;

    @Column(length = 150)
    private String logradouro;

    @Column(length = 20)
    private String numero;

    @Column(length = 100)
    private String complemento;

    @Column(length = 100)
    private String bairro;

    private Double latitude;

    private Double longitude;

    @Column(name = "raio_atendimento_km")
    private Short raioAtendimentoKm;

    @Column(nullable = false)
    private boolean ativo;

    @Column(name = "criado_em", nullable = false)
    private Instant criadoEm;

    @Column(name = "atualizado_em", nullable = false)
    private Instant atualizadoEm;
}
