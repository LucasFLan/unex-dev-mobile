package com.unex.chamaAi.anuncio.application.dto;

import com.unex.chamaAi.anuncio.domain.entities.Anuncio;
import com.unex.chamaAi.anuncio.domain.valueobjects.Preco;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/** preco nulo = "a combinar". fotoAutor: caminho da foto de perfil do autor na API, nulo se ele não tem foto. */
public record AnuncioDetalhado(UUID id, String tipo, String titulo, String descricao, BigDecimal preco,
                               CategoriaResumo categoria, String cidade, String uf, boolean ativo,
                               UUID autorId, Instant criadoEm, Instant atualizadoEm, List<FotoResumo> fotos,
                               String fotoAutor) {

    public AnuncioDetalhado {
        fotos = fotos == null ? List.of() : List.copyOf(fotos);
    }

    public static AnuncioDetalhado de(Anuncio anuncio, CategoriaResumo categoria) {
        return de(anuncio, categoria, List.of(), null);
    }

    public static AnuncioDetalhado de(Anuncio anuncio, CategoriaResumo categoria, List<FotoResumo> fotos,
                                      String fotoAutor) {
        return new AnuncioDetalhado(anuncio.getId(), anuncio.getTipo().name(), anuncio.getTitulo(),
                anuncio.getDescricao(), anuncio.getPreco().map(Preco::valor).orElse(null), categoria,
                anuncio.getLocalizacao().cidade(), anuncio.getLocalizacao().uf(), anuncio.isAtivo(),
                anuncio.getAutorId(), anuncio.getCriadoEm(), anuncio.getAtualizadoEm(), fotos, fotoAutor);
    }
}
