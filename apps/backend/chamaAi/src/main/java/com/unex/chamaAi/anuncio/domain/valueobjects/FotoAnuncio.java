package com.unex.chamaAi.anuncio.domain.valueobjects;

import java.time.Instant;
import java.util.UUID;

/** Foto de um anúncio (os bytes ficam no armazenamento, fora do domínio). */
public record FotoAnuncio(UUID id, UUID anuncioId, Instant criadaEm) {
}
