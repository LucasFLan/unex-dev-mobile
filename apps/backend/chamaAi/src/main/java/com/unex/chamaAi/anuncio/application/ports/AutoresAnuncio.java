package com.unex.chamaAi.anuncio.application.ports;

import java.time.Instant;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

/** Nome, foto e reputação de quem publicou os anúncios, buscados de uma vez para uma página inteira. */
public interface AutoresAnuncio {

    /** Para testes e usos que não precisam do autor. */
    AutoresAnuncio NENHUM = ids -> Map.of();

    Map<UUID, AutorAnuncio> buscar(Collection<UUID> autorIds);

    /** mediaAvaliacoes nula = ainda sem avaliações. */
    record AutorAnuncio(UUID id, String nome, Instant fotoAtualizadaEm, Double mediaAvaliacoes,
                        long totalAvaliacoes) {
    }
}
