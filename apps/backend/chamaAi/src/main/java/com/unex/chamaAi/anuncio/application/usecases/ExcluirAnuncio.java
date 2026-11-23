package com.unex.chamaAi.anuncio.application.usecases;

import com.unex.chamaAi.anuncio.domain.entities.Anuncio;
import com.unex.chamaAi.anuncio.domain.exceptions.AcessoNegadoAnuncioException;
import com.unex.chamaAi.anuncio.domain.exceptions.AnuncioNaoEncontradoException;
import com.unex.chamaAi.anuncio.domain.repositories.AnuncioRepository;
import com.unex.chamaAi.anuncio.domain.repositories.FotoAnuncioRepository;

import java.util.UUID;

public class ExcluirAnuncio {

    private final AnuncioRepository anuncios;

    private final FotoAnuncioRepository fotos;

    public ExcluirAnuncio(AnuncioRepository anuncios) {
        this(anuncios, null);
    }

    public ExcluirAnuncio(AnuncioRepository anuncios, FotoAnuncioRepository fotos) {
        this.anuncios = anuncios;
        this.fotos = fotos;
    }

    /** O autor exclui o próprio anúncio; um admin pode excluir qualquer um (moderação). */
    public void executar(UUID anuncioId, UUID solicitanteId, boolean solicitanteEhAdmin) {
        Anuncio anuncio = anuncios.buscarPorId(anuncioId)
                .filter(encontrado -> solicitanteEhAdmin || encontrado.visivelPara(solicitanteId))
                .orElseThrow(AnuncioNaoEncontradoException::new);
        if (!solicitanteEhAdmin && !anuncio.pertenceA(solicitanteId)) {
            throw new AcessoNegadoAnuncioException();
        }
        if (fotos != null) {
            fotos.removerTodas(anuncio.getId());
        }
        anuncios.excluir(anuncio.getId());
    }
}
