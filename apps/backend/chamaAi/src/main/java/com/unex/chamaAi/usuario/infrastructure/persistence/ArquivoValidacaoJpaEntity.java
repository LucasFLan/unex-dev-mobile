package com.unex.chamaAi.usuario.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "arquivos_validacao")
@IdClass(ArquivoValidacaoJpaEntity.Chave.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ArquivoValidacaoJpaEntity {

    @Id
    @Column(name = "envio_id")
    private UUID envioId;

    @Id
    @Column(length = 20)
    private String tipo;

    @Column(name = "content_type", nullable = false, length = 50)
    private String contentType;

    /** Caminho relativo à pasta de uploads de validação (os bytes ficam no disco). */
    @Column(nullable = false, length = 300)
    private String caminho;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class Chave implements Serializable {
        private UUID envioId;
        private String tipo;
    }
}
