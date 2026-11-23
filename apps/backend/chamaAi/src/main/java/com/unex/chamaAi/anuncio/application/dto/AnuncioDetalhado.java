package com.unex.chamaAi.anuncio.application.dto;

import com.unex.chamaAi.anuncio.application.ports.AutoresAnuncio.AutorAnuncio;
import com.unex.chamaAi.anuncio.domain.entities.Anuncio;
import com.unex.chamaAi.anuncio.domain.valueobjects.Coordenadas;
import com.unex.chamaAi.anuncio.domain.valueobjects.Endereco;
import com.unex.chamaAi.anuncio.domain.valueobjects.Preco;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * preco nulo = "a combinar". regiao: ponto arredondado (~1 km), o que o público vê; nula sem CEP ou sem ponto.
 * endereco: completo, só para o autor (quem monta a resposta decide). autor: nome e reputação de quem publicou.
 */
public record AnuncioDetalhado(UUID id, String tipo, String titulo, String descricao, BigDecimal preco,
                               CategoriaResumo categoria, String cidade, String uf, String bairro, Coordenadas regiao,
                               Integer raioAtendimentoKm, Endereco endereco, boolean ativo, UUID autorId,
                               AutorAnuncio autor, Instant criadoEm, Instant atualizadoEm) {

    public static AnuncioDetalhado de(Anuncio anuncio, CategoriaResumo categoria, AutorAnuncio autor) {
        Endereco endereco = anuncio.getEndereco().orElse(null);
        Coordenadas regiao = endereco == null || endereco.coordenadas() == null ? null
                : endereco.coordenadas().aproximada();
        return new AnuncioDetalhado(anuncio.getId(), anuncio.getTipo().name(), anuncio.getTitulo(),
                anuncio.getDescricao(), anuncio.getPreco().map(Preco::valor).orElse(null), categoria,
                anuncio.getLocalizacao().cidade(), anuncio.getLocalizacao().uf(),
                endereco == null ? null : endereco.bairro(), regiao, anuncio.getRaioAtendimentoKm().orElse(null),
                endereco, anuncio.isAtivo(), anuncio.getAutorId(), autor, anuncio.getCriadoEm(),
                anuncio.getAtualizadoEm());
    }
}
