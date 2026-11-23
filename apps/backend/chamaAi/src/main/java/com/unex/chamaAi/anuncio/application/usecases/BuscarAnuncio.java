package com.unex.chamaAi.anuncio.application.usecases;

import com.unex.chamaAi.anuncio.application.dto.AnuncioDetalhado;
import com.unex.chamaAi.anuncio.domain.exceptions.AnuncioNaoEncontradoException;
import com.unex.chamaAi.anuncio.domain.repositories.AnuncioRepository;
import com.unex.chamaAi.anuncio.domain.repositories.CategoriaRepository;
import com.unex.chamaAi.anuncio.domain.repositories.FotoAnuncioRepository;
import com.unex.chamaAi.usuario.domain.repositories.FotoPerfilRepository;

import java.util.UUID;

public class BuscarAnuncio {

    private final AnuncioRepository anuncios;
    private final DetalhadorAnuncio detalhador;

    public BuscarAnuncio(AnuncioRepository anuncios, CategoriaRepository categorias) {
        this(anuncios, categorias, null, null);
    }

    public BuscarAnuncio(AnuncioRepository anuncios, CategoriaRepository categorias,
            FotoAnuncioRepository fotos, FotoPerfilRepository fotosPerfil) {
        this.anuncios = anuncios;
        this.detalhador = new DetalhadorAnuncio(categorias, fotos, fotosPerfil);
    }

    /** solicitanteId pode ser nulo (visitante sem login). */
    public AnuncioDetalhado executar(UUID anuncioId, UUID solicitanteId) {
        return anuncios.buscarPorId(anuncioId)
                .filter(anuncio -> anuncio.visivelPara(solicitanteId))
                .map(detalhador::detalhar)
                .orElseThrow(AnuncioNaoEncontradoException::new);
    }
}
