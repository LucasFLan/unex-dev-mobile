package com.unex.chamaAi.anuncio.application.usecases;

import com.unex.chamaAi.anuncio.application.dto.AnuncioDetalhado;
import com.unex.chamaAi.anuncio.application.ports.AutoresAnuncio;
import com.unex.chamaAi.anuncio.domain.entities.Anuncio;
import com.unex.chamaAi.anuncio.domain.exceptions.AcessoNegadoAnuncioException;
import com.unex.chamaAi.anuncio.domain.exceptions.AnuncioNaoEncontradoException;
import com.unex.chamaAi.anuncio.domain.exceptions.FotoAnuncioNaoEncontradaException;
import com.unex.chamaAi.anuncio.domain.repositories.AnuncioRepository;
import com.unex.chamaAi.anuncio.domain.repositories.CategoriaRepository;
import com.unex.chamaAi.anuncio.domain.repositories.FotoAnuncioRepository;
import com.unex.chamaAi.anuncio.domain.valueobjects.FotoAnuncio;
import com.unex.chamaAi.shared.domain.exceptions.DadoInvalidoException;

import java.util.List;
import java.util.UUID;

/** Fotos do anúncio: o autor adiciona e remove; quem vê o anúncio vê as fotos. */
public class GerenciarFotosAnuncio {

    private final AnuncioRepository anuncios;
    private final FotoAnuncioRepository fotos;
    private final DetalhadorAnuncio detalhador;

    public GerenciarFotosAnuncio(AnuncioRepository anuncios, CategoriaRepository categorias, AutoresAnuncio autores,
                                 FotoAnuncioRepository fotos) {
        this.anuncios = anuncios;
        this.fotos = fotos;
        this.detalhador = new DetalhadorAnuncio(categorias, autores, fotos);
    }

    public AnuncioDetalhado adicionar(UUID anuncioId, UUID solicitanteId, List<byte[]> arquivos) {
        Anuncio anuncio = doAutor(anuncioId, solicitanteId);
        if (arquivos == null || arquivos.isEmpty()) {
            throw new DadoInvalidoException("Envie ao menos uma foto.");
        }
        int restantes = FotoAnuncio.MAXIMO_POR_ANUNCIO - fotos.listarIds(List.of(anuncioId))
                .getOrDefault(anuncioId, List.of()).size();
        if (arquivos.size() > restantes) {
            throw new DadoInvalidoException(restantes <= 0
                    ? "O anúncio já tem " + FotoAnuncio.MAXIMO_POR_ANUNCIO + " fotos. Remova alguma para enviar outra."
                    : "Cada anúncio pode ter no máximo " + FotoAnuncio.MAXIMO_POR_ANUNCIO
                    + " fotos: dá para enviar mais " + restantes + ".");
        }
        fotos.gravar(anuncioId, arquivos.stream().map(FotoAnuncio::de).toList());
        return detalhador.detalhar(anuncio);
    }

    public AnuncioDetalhado remover(UUID anuncioId, UUID fotoId, UUID solicitanteId) {
        Anuncio anuncio = doAutor(anuncioId, solicitanteId);
        if (!fotos.remover(anuncioId, fotoId)) {
            throw new FotoAnuncioNaoEncontradaException();
        }
        return detalhador.detalhar(anuncio);
    }

    /** solicitanteId nulo = visitante sem login: só vê fotos de anúncios ativos. */
    public FotoAnuncio exibir(UUID anuncioId, UUID fotoId, UUID solicitanteId) {
        anuncios.buscarPorId(anuncioId)
                .filter(anuncio -> anuncio.visivelPara(solicitanteId))
                .orElseThrow(FotoAnuncioNaoEncontradaException::new);
        return fotos.buscar(anuncioId, fotoId).orElseThrow(FotoAnuncioNaoEncontradaException::new);
    }

    private Anuncio doAutor(UUID anuncioId, UUID solicitanteId) {
        Anuncio anuncio = anuncios.buscarPorId(anuncioId)
                .filter(encontrado -> encontrado.visivelPara(solicitanteId))
                .orElseThrow(AnuncioNaoEncontradoException::new);
        if (!anuncio.pertenceA(solicitanteId)) {
            throw new AcessoNegadoAnuncioException();
        }
        return anuncio;
    }
}
