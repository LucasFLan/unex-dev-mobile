package com.unex.chamaAi.anuncio.application.usecases;

import com.unex.chamaAi.anuncio.domain.entities.Anuncio;
import com.unex.chamaAi.anuncio.domain.exceptions.AcessoNegadoAnuncioException;
import com.unex.chamaAi.anuncio.domain.exceptions.AnuncioNaoEncontradoException;
import com.unex.chamaAi.anuncio.domain.repositories.AnuncioRepository;

import java.util.UUID;

public class ExcluirAnuncio {

    private final AnuncioRepository anuncios;

    public ExcluirAnuncio(AnuncioRepository anuncios) {
        this.anuncios = anuncios;
    }

    /** O autor exclui o próprio anúncio; um admin pode excluir qualquer um (moderação). */
    public void executar(UUID anuncioId, UUID solicitanteId, boolean solicitanteEhAdmin) {
        Anuncio anuncio = anuncios.buscarPorId(anuncioId)
                .filter(encontrado -> solicitanteEhAdmin || encontrado.visivelPara(solicitanteId))
                .orElseThrow(AnuncioNaoEncontradoException::new);
        if (!solicitanteEhAdmin && !anuncio.pertenceA(solicitanteId)) {
            throw new AcessoNegadoAnuncioException();
        }
        anuncios.excluir(anuncio.getId());
    }
}
