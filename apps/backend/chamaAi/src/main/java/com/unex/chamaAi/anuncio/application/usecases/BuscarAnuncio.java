package com.unex.chamaAi.anuncio.application.usecases;

import com.unex.chamaAi.anuncio.application.ports.AutoresAnuncio;
import com.unex.chamaAi.anuncio.application.dto.AnuncioDetalhado;
import com.unex.chamaAi.anuncio.domain.exceptions.AnuncioNaoEncontradoException;
import com.unex.chamaAi.anuncio.domain.repositories.AnuncioRepository;
import com.unex.chamaAi.anuncio.domain.repositories.CategoriaRepository;

import java.util.UUID;

public class BuscarAnuncio {

    private final AnuncioRepository anuncios;
    private final DetalhadorAnuncio detalhador;

    public BuscarAnuncio(AnuncioRepository anuncios, CategoriaRepository categorias, AutoresAnuncio autores) {
        this.anuncios = anuncios;
        this.detalhador = new DetalhadorAnuncio(categorias, autores);
    }

    /** solicitanteId pode ser nulo (visitante sem login). */
    public AnuncioDetalhado executar(UUID anuncioId, UUID solicitanteId) {
        return anuncios.buscarPorId(anuncioId)
                .filter(anuncio -> anuncio.visivelPara(solicitanteId))
                .map(detalhador::detalhar)
                .orElseThrow(AnuncioNaoEncontradoException::new);
    }
}
