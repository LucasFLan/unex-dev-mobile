package com.unex.chamaAi.anuncio.infrastructure.web;

import com.unex.chamaAi.anuncio.application.usecases.ListarCategorias;
import com.unex.chamaAi.anuncio.infrastructure.web.dto.response.CategoriaResponse;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CategoriaControllerImpl implements CategoriaController {

    private final ListarCategorias listarCategorias;

    public CategoriaControllerImpl(ListarCategorias listarCategorias) {
        this.listarCategorias = listarCategorias;
    }

    @Override
    public List<CategoriaResponse> listar() {
        return listarCategorias.executar().stream().map(CategoriaResponse::de).toList();
    }
}
