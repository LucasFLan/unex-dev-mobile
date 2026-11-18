package com.unex.chamaAi.anuncio.application.dto;

import java.util.UUID;

public record ListarAnunciosQuery(String tipo, UUID categoriaId, String cidade, String uf,
                                  Integer pagina, Integer tamanho) {
}
