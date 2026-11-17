package com.unex.chamaAi.anuncio.application.usecases;

import com.unex.chamaAi.anuncio.application.dto.AnuncioDetalhado;
import com.unex.chamaAi.anuncio.application.dto.DadosAnuncioCommand;
import com.unex.chamaAi.anuncio.domain.entities.Anuncio;
import com.unex.chamaAi.anuncio.domain.repositories.AnuncioRepository;
import com.unex.chamaAi.anuncio.domain.repositories.CategoriaRepository;

import java.util.UUID;

public class CriarAnuncio {

    private final AnuncioRepository anuncios;
    private final ValidadorDadosAnuncio validador;
    private final DetalhadorAnuncio detalhador;

    public CriarAnuncio(AnuncioRepository anuncios, CategoriaRepository categorias) {
        this.anuncios = anuncios;
        this.validador = new ValidadorDadosAnuncio(categorias);
        this.detalhador = new DetalhadorAnuncio(categorias);
    }

    public AnuncioDetalhado executar(UUID autorId, DadosAnuncioCommand comando) {
        var dados = validador.validar(comando);
        Anuncio anuncio = Anuncio.criar(autorId, dados.categoriaId(), dados.tipo(), dados.titulo(),
                dados.descricao(), dados.preco(), dados.localizacao());
        anuncios.salvar(anuncio);
        return detalhador.detalhar(anuncio);
    }
}
