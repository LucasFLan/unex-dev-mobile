package com.unex.chamaAi.anuncio.application.dto;

import java.math.BigDecimal;
import java.util.UUID;

/** Dados usados para criar ou editar um anúncio. preco nulo = "a combinar". */
public record DadosAnuncioCommand(String tipo, UUID categoriaId, String titulo, String descricao,
                                  BigDecimal preco, String cidade, String uf) {
}
