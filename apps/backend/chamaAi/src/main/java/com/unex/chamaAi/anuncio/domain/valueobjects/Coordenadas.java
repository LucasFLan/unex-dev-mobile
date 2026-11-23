package com.unex.chamaAi.anuncio.domain.valueobjects;

import com.unex.chamaAi.shared.domain.exceptions.DadoInvalidoException;

/** Ponto no mapa (graus decimais, WGS84). */
public record Coordenadas(double latitude, double longitude) {

    /** Duas casas decimais ≈ 1,1 km: dá para mostrar a região sem revelar o endereço. */
    private static final double PASSO_APROXIMADO = 100.0;

    public Coordenadas {
        if (!Double.isFinite(latitude) || latitude < -90 || latitude > 90
                || !Double.isFinite(longitude) || longitude < -180 || longitude > 180) {
            throw new DadoInvalidoException("Coordenadas inválidas.");
        }
    }

    /** Mesmo ponto arredondado para a região (o que o público vê). */
    public Coordenadas aproximada() {
        return new Coordenadas(Math.round(latitude * PASSO_APROXIMADO) / PASSO_APROXIMADO,
                Math.round(longitude * PASSO_APROXIMADO) / PASSO_APROXIMADO);
    }
}
