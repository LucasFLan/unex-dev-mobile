package com.unex.chamaAi.anuncio.application.usecases;

import com.unex.chamaAi.anuncio.application.dto.AnuncioDetalhado;
import com.unex.chamaAi.anuncio.application.dto.ListarAnunciosQuery;
import com.unex.chamaAi.anuncio.domain.repositories.AnuncioRepository;
import com.unex.chamaAi.anuncio.domain.repositories.CategoriaRepository;
import com.unex.chamaAi.anuncio.domain.repositories.FotoAnuncioRepository;
import com.unex.chamaAi.usuario.domain.repositories.FotoPerfilRepository;
import com.unex.chamaAi.anuncio.domain.repositories.FiltroAnuncios;
import com.unex.chamaAi.anuncio.domain.valueobjects.Localizacao;
import com.unex.chamaAi.anuncio.domain.valueobjects.TipoAnuncio;
import com.unex.chamaAi.shared.domain.paginacao.Pagina;
import com.unex.chamaAi.shared.domain.paginacao.Paginacao;

public class ListarAnuncios {

    private final AnuncioRepository anuncios;
    private final DetalhadorAnuncio detalhador;

    public ListarAnuncios(AnuncioRepository anuncios, CategoriaRepository categorias) {
        this(anuncios, categorias, null, null);
    }

    public ListarAnuncios(AnuncioRepository anuncios, CategoriaRepository categorias,
            FotoAnuncioRepository fotos, FotoPerfilRepository fotosPerfil) {
        this.anuncios = anuncios;
        this.detalhador = new DetalhadorAnuncio(categorias, fotos, fotosPerfil);
    }

    public Pagina<AnuncioDetalhado> executar(ListarAnunciosQuery consulta) {
        FiltroAnuncios filtro = new FiltroAnuncios(
                vazio(consulta.tipo()) ? null : TipoAnuncio.de(consulta.tipo()),
                consulta.categoriaId(),
                vazio(consulta.cidade()) ? null : consulta.cidade().trim(),
                vazio(consulta.uf()) ? null : Localizacao.validarUf(consulta.uf()));
        Paginacao paginacao = Paginacao.de(consulta.pagina(), consulta.tamanho());
        return detalhador.detalhar(anuncios.buscarAtivos(filtro, paginacao));
    }

    private static boolean vazio(String valor) {
        return valor == null || valor.isBlank();
    }
}
