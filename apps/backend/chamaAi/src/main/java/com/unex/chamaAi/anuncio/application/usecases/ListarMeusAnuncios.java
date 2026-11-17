package com.unex.chamaAi.anuncio.application.usecases;

import com.unex.chamaAi.anuncio.application.dto.AnuncioDetalhado;
import com.unex.chamaAi.anuncio.domain.repositories.AnuncioRepository;
import com.unex.chamaAi.anuncio.domain.repositories.CategoriaRepository;
import com.unex.chamaAi.shared.domain.paginacao.Pagina;
import com.unex.chamaAi.shared.domain.paginacao.Paginacao;

import java.util.UUID;

public class ListarMeusAnuncios {

    private final AnuncioRepository anuncios;
    private final DetalhadorAnuncio detalhador;

    public ListarMeusAnuncios(AnuncioRepository anuncios, CategoriaRepository categorias) {
        this.anuncios = anuncios;
        this.detalhador = new DetalhadorAnuncio(categorias);
    }

    public Pagina<AnuncioDetalhado> executar(UUID autorId, Integer pagina, Integer tamanho) {
        return detalhador.detalhar(anuncios.buscarPorAutor(autorId, Paginacao.de(pagina, tamanho)));
    }
}
