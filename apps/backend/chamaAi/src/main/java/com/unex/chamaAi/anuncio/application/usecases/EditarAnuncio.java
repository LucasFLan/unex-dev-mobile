package com.unex.chamaAi.anuncio.application.usecases;

import com.unex.chamaAi.anuncio.application.dto.AnuncioDetalhado;
import com.unex.chamaAi.anuncio.application.dto.DadosAnuncioCommand;
import com.unex.chamaAi.anuncio.domain.entities.Anuncio;
import com.unex.chamaAi.anuncio.domain.exceptions.AcessoNegadoAnuncioException;
import com.unex.chamaAi.anuncio.domain.exceptions.AnuncioNaoEncontradoException;
import com.unex.chamaAi.anuncio.domain.repositories.AnuncioRepository;
import com.unex.chamaAi.anuncio.domain.repositories.CategoriaRepository;
import com.unex.chamaAi.anuncio.domain.repositories.FotoAnuncioRepository;
import com.unex.chamaAi.usuario.domain.repositories.FotoPerfilRepository;

import java.util.UUID;

public class EditarAnuncio {

    private final AnuncioRepository anuncios;
    private final ValidadorDadosAnuncio validador;
    private final DetalhadorAnuncio detalhador;

    public EditarAnuncio(AnuncioRepository anuncios, CategoriaRepository categorias) {
        this(anuncios, categorias, null, null);
    }

    public EditarAnuncio(AnuncioRepository anuncios, CategoriaRepository categorias,
            FotoAnuncioRepository fotos, FotoPerfilRepository fotosPerfil) {
        this.anuncios = anuncios;
        this.validador = new ValidadorDadosAnuncio(categorias);
        this.detalhador = new DetalhadorAnuncio(categorias, fotos, fotosPerfil);
    }

    /** Só o autor edita. Para quem não vê o anúncio (inativo de outro autor), ele "não existe". */
    public AnuncioDetalhado executar(UUID anuncioId, UUID solicitanteId, DadosAnuncioCommand comando) {
        Anuncio anuncio = anuncios.buscarPorId(anuncioId)
                .filter(encontrado -> encontrado.visivelPara(solicitanteId))
                .orElseThrow(AnuncioNaoEncontradoException::new);
        if (!anuncio.pertenceA(solicitanteId)) {
            throw new AcessoNegadoAnuncioException();
        }

        var dados = validador.validar(comando);
        anuncio.editar(dados.categoriaId(), dados.tipo(), dados.titulo(), dados.descricao(), dados.preco(),
                dados.localizacao());
        anuncios.salvar(anuncio);
        return detalhador.detalhar(anuncio);
    }
}
