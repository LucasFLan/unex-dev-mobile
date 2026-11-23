package com.unex.chamaAi.anuncio.application.usecases;

import com.unex.chamaAi.anuncio.application.dto.AnuncioDetalhado;
import com.unex.chamaAi.anuncio.domain.repositories.AnuncioRepository;
import com.unex.chamaAi.anuncio.domain.repositories.CategoriaRepository;
import com.unex.chamaAi.anuncio.domain.repositories.FotoAnuncioRepository;
import com.unex.chamaAi.usuario.domain.repositories.FotoPerfilRepository;
import com.unex.chamaAi.shared.domain.paginacao.Pagina;
import com.unex.chamaAi.shared.domain.paginacao.Paginacao;

import java.util.UUID;

public class ListarMeusAnuncios {

    private final AnuncioRepository anuncios;
    private final DetalhadorAnuncio detalhador;

    public ListarMeusAnuncios(AnuncioRepository anuncios, CategoriaRepository categorias) {
        this(anuncios, categorias, null, null);
    }

    public ListarMeusAnuncios(AnuncioRepository anuncios, CategoriaRepository categorias,
            FotoAnuncioRepository fotos, FotoPerfilRepository fotosPerfil) {
        this.anuncios = anuncios;
        this.detalhador = new DetalhadorAnuncio(categorias, fotos, fotosPerfil);
    }

    public Pagina<AnuncioDetalhado> executar(UUID autorId, Integer pagina, Integer tamanho) {
        return detalhador.detalhar(anuncios.buscarPorAutor(autorId, Paginacao.de(pagina, tamanho)));
    }
}
