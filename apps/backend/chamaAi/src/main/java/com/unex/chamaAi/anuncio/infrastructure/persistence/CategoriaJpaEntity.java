package com.unex.chamaAi.anuncio.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "categorias")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CategoriaJpaEntity {

    @Id
    private UUID id;

    @Column(nullable = false, length = 60)
    private String slug;

    @Column(nullable = false, length = 80)
    private String nome;
}
