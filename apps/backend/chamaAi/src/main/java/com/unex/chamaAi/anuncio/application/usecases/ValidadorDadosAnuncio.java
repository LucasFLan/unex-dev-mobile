package com.unex.chamaAi.anuncio.application.usecases;

import com.unex.chamaAi.anuncio.application.dto.DadosAnuncioCommand;
import com.unex.chamaAi.anuncio.domain.exceptions.CategoriaInvalidaException;
import com.unex.chamaAi.anuncio.domain.repositories.CategoriaRepository;
import com.unex.chamaAi.anuncio.domain.valueobjects.Localizacao;
import com.unex.chamaAi.anuncio.domain.valueobjects.Preco;
import com.unex.chamaAi.anuncio.domain.valueobjects.TipoAnuncio;
import com.unex.chamaAi.shared.domain.exceptions.DadoInvalidoException;

import java.util.UUID;

/** Converte o comando em value objects e confere se a categoria existe. */
class ValidadorDadosAnuncio {

    record DadosValidados(TipoAnuncio tipo, UUID categoriaId, String titulo, String descricao,
                          Preco preco, Localizacao localizacao) {
    }

    private final CategoriaRepository categorias;

    ValidadorDadosAnuncio(CategoriaRepository categorias) {
        this.categorias = categorias;
    }

    DadosValidados validar(DadosAnuncioCommand comando) {
        if (comando.categoriaId() == null) {
            throw new DadoInvalidoException("A categoria é obrigatória.");
        }
        TipoAnuncio tipo = TipoAnuncio.de(comando.tipo());
        Preco preco = Preco.deOuNulo(comando.preco());
        Localizacao localizacao = new Localizacao(comando.cidade(), comando.uf());
        categorias.buscarPorId(comando.categoriaId()).orElseThrow(CategoriaInvalidaException::new);
        return new DadosValidados(tipo, comando.categoriaId(), comando.titulo(), comando.descricao(), preco,
                localizacao);
    }
}
