package com.unex.chamaAi.anuncio.application.dto;

import com.unex.chamaAi.anuncio.domain.entities.Anuncio;
import com.unex.chamaAi.anuncio.domain.valueobjects.Preco;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/** preco nulo = "a combinar". */
public record AnuncioDetalhado(UUID id, String tipo, String titulo, String descricao, BigDecimal preco,
                               CategoriaResumo categoria, String cidade, String uf, boolean ativo,
                               UUID autorId, Instant criadoEm, Instant atualizadoEm) {

    public static AnuncioDetalhado de(Anuncio anuncio, CategoriaResumo categoria) {
        return new AnuncioDetalhado(anuncio.getId(), anuncio.getTipo().name(), anuncio.getTitulo(),
                anuncio.getDescricao(), anuncio.getPreco().map(Preco::valor).orElse(null), categoria,
                anuncio.getLocalizacao().cidade(), anuncio.getLocalizacao().uf(), anuncio.isAtivo(),
                anuncio.getAutorId(), anuncio.getCriadoEm(), anuncio.getAtualizadoEm());
    }
}
