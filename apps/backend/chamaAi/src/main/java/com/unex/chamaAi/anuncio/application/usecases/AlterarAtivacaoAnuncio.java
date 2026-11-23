package com.unex.chamaAi.anuncio.application.usecases;

import com.unex.chamaAi.anuncio.application.ports.AutoresAnuncio;
import com.unex.chamaAi.anuncio.application.dto.AnuncioDetalhado;
import com.unex.chamaAi.anuncio.domain.entities.Anuncio;
import com.unex.chamaAi.anuncio.domain.exceptions.AcessoNegadoAnuncioException;
import com.unex.chamaAi.anuncio.domain.exceptions.AnuncioNaoEncontradoException;
import com.unex.chamaAi.anuncio.domain.repositories.AnuncioRepository;
import com.unex.chamaAi.anuncio.domain.repositories.CategoriaRepository;

import java.util.UUID;

public class AlterarAtivacaoAnuncio {

    private final AnuncioRepository anuncios;
    private final DetalhadorAnuncio detalhador;

    public AlterarAtivacaoAnuncio(AnuncioRepository anuncios, CategoriaRepository categorias, AutoresAnuncio autores) {
        this.anuncios = anuncios;
        this.detalhador = new DetalhadorAnuncio(categorias, autores);
    }

    /**
     * Só o autor ativa ou desativa. Para quem não vê o anúncio (inativo de outro autor), ele "não existe".
     * Ativar um anúncio já ativo (ou desativar um inativo) não é erro.
     */
    public AnuncioDetalhado executar(UUID anuncioId, UUID solicitanteId, boolean ativo) {
        Anuncio anuncio = anuncios.buscarPorId(anuncioId)
                .filter(encontrado -> encontrado.visivelPara(solicitanteId))
                .orElseThrow(AnuncioNaoEncontradoException::new);
        if (!anuncio.pertenceA(solicitanteId)) {
            throw new AcessoNegadoAnuncioException();
        }

        if (ativo) {
            anuncio.ativar();
        } else {
            anuncio.desativar();
        }
        anuncios.salvar(anuncio);
        return detalhador.detalhar(anuncio);
    }
}
