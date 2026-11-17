package com.unex.chamaAi.anuncio.application.usecases;

import com.unex.chamaAi.anuncio.application.dto.CategoriaResumo;
import com.unex.chamaAi.anuncio.domain.repositories.CategoriaRepository;

import java.util.List;

public class ListarCategorias {

    private final CategoriaRepository categorias;

    public ListarCategorias(CategoriaRepository categorias) {
        this.categorias = categorias;
    }

    public List<CategoriaResumo> executar() {
        return categorias.listarTodas().stream().map(CategoriaResumo::de).toList();
    }
}
