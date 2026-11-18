package com.unex.chamaAi.anuncio.domain.entities;

import java.util.Objects;
import java.util.UUID;

public record Categoria(UUID id, String slug, String nome) {

    public Categoria {
        Objects.requireNonNull(id, "O id da categoria é obrigatório.");
        Objects.requireNonNull(slug, "O slug da categoria é obrigatório.");
        Objects.requireNonNull(nome, "O nome da categoria é obrigatório.");
    }
}
