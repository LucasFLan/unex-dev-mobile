package com.unex.chamaAi.anuncio.domain.repositories;

import com.unex.chamaAi.anuncio.domain.entities.Categoria;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoriaRepository {

    /** Ordenadas por nome. */
    List<Categoria> listarTodas();

    Optional<Categoria> buscarPorId(UUID id);
}
