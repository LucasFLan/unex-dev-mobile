package com.unex.chamaAi.usuario.application.dto;

import java.time.Instant;
import java.util.UUID;

/**
 * Caminho público da foto de perfil na API. O parâmetro "v" muda a cada troca de foto, então o navegador pode
 * guardar a imagem em cache sem mostrar uma foto antiga.
 */
public final class FotoPerfilUrl {

    private FotoPerfilUrl() {
    }

    public static String de(UUID usuarioId, Instant versao) {
        return versao == null ? null : "/usuarios/" + usuarioId + "/foto?v=" + versao.toEpochMilli();
    }
}
