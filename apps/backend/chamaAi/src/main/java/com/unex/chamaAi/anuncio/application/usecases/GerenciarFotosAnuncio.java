package com.unex.chamaAi.anuncio.application.usecases;

import com.unex.chamaAi.anuncio.application.dto.AnuncioDetalhado;
import com.unex.chamaAi.anuncio.domain.entities.Anuncio;
import com.unex.chamaAi.anuncio.domain.exceptions.AcessoNegadoAnuncioException;
import com.unex.chamaAi.anuncio.domain.exceptions.AnuncioNaoEncontradoException;
import com.unex.chamaAi.anuncio.domain.exceptions.FotoAnuncioNaoEncontradaException;
import com.unex.chamaAi.anuncio.domain.repositories.AnuncioRepository;
import com.unex.chamaAi.anuncio.domain.repositories.CategoriaRepository;
import com.unex.chamaAi.anuncio.domain.repositories.FotoAnuncioRepository;
import com.unex.chamaAi.shared.domain.exceptions.DadoInvalidoException;
import com.unex.chamaAi.shared.domain.valueobjects.Imagem;
import com.unex.chamaAi.usuario.domain.repositories.FotoPerfilRepository;

import java.time.Clock;
import java.util.List;
import java.util.UUID;

/** Fotos do anúncio: o autor adiciona e remove; quem vê o anúncio vê as fotos. */
public class GerenciarFotosAnuncio {

    public static final int MAXIMO_FOTOS = 6;

    private final AnuncioRepository anuncios;
    private final FotoAnuncioRepository fotos;
    private final DetalhadorAnuncio detalhador;
    private final Clock relogio;

    public GerenciarFotosAnuncio(AnuncioRepository anuncios, CategoriaRepository categorias,
                                 FotoAnuncioRepository fotos, FotoPerfilRepository fotosPerfil, Clock relogio) {
        this.anuncios = anuncios;
        this.fotos = fotos;
        this.detalhador = new DetalhadorAnuncio(categorias, fotos, fotosPerfil);
        this.relogio = relogio;
    }

    public AnuncioDetalhado adicionar(UUID anuncioId, UUID solicitanteId, List<byte[]> arquivos) {
        Anuncio anuncio = doAutor(anuncioId, solicitanteId);
        if (arquivos == null || arquivos.isEmpty()) {
            throw new DadoInvalidoException("Envie ao menos uma foto.");
        }
        int restantes = MAXIMO_FOTOS - fotos.listar(anuncioId).size();
        if (arquivos.size() > restantes) {
            throw new DadoInvalidoException(restantes <= 0
                    ? "O anúncio já tem " + MAXIMO_FOTOS + " fotos. Remova alguma para enviar outra."
                    : "Cada anúncio pode ter no máximo " + MAXIMO_FOTOS + " fotos: dá para enviar mais "
                    + restantes + ".");
        }
        List<Imagem> imagens = arquivos.stream().map(bytes -> Imagem.de(bytes, "a foto do anúncio")).toList();
        fotos.adicionar(anuncioId, imagens, relogio.instant());
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
    public Imagem exibir(UUID anuncioId, UUID fotoId, UUID solicitanteId) {
        anuncios.buscarPorId(anuncioId)
                .filter(anuncio -> anuncio.visivelPara(solicitanteId))
                .orElseThrow(FotoAnuncioNaoEncontradaException::new);
        return fotos.ler(anuncioId, fotoId).orElseThrow(FotoAnuncioNaoEncontradaException::new);
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
