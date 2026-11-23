package com.unex.chamaAi.anuncio.application.dto;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Dados usados para criar ou editar um anúncio. preco nulo = "a combinar". cep nulo = anúncio só com cidade e UF
 * (sem mapa nem distância); os demais campos do endereço são opcionais. raioAtendimentoKm só vale para OFERTA.
 */
public record DadosAnuncioCommand(String tipo, UUID categoriaId, String titulo, String descricao,
                                  BigDecimal preco, String cidade, String uf, String cep, String logradouro,
                                  String numero, String complemento, String bairro, Integer raioAtendimentoKm) {

    /** Sem endereço: só cidade e UF. */
    public DadosAnuncioCommand(String tipo, UUID categoriaId, String titulo, String descricao, BigDecimal preco,
                               String cidade, String uf) {
        this(tipo, categoriaId, titulo, descricao, preco, cidade, uf, null, null, null, null, null, null);
    }
}
