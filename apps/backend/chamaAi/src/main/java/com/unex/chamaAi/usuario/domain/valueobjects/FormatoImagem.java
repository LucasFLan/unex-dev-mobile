package com.unex.chamaAi.usuario.domain.valueobjects;

/**
 * Reconhece o formato de uma imagem pelos primeiros bytes (assinatura), não pela extensão nem pelo cabeçalho
 * enviado pelo cliente. Aceita JPEG, PNG e WEBP.
 */
public final class FormatoImagem {

    private FormatoImagem() {
    }

    /** Content type da imagem, ou {@code null} se não for JPEG, PNG nem WEBP. */
    public static String detectar(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        if (comecaCom(bytes, 0xFF, 0xD8, 0xFF)) {
            return "image/jpeg";
        }
        if (comecaCom(bytes, 0x89, 'P', 'N', 'G', 0x0D, 0x0A, 0x1A, 0x0A)) {
            return "image/png";
        }
        if (bytes.length >= 12 && comecaCom(bytes, 'R', 'I', 'F', 'F')
                && bytes[8] == 'W' && bytes[9] == 'E' && bytes[10] == 'B' && bytes[11] == 'P') {
            return "image/webp";
        }
        return null;
    }

    private static boolean comecaCom(byte[] bytes, int... assinatura) {
        if (bytes.length < assinatura.length) {
            return false;
        }
        for (int i = 0; i < assinatura.length; i++) {
            if ((bytes[i] & 0xFF) != assinatura[i]) {
                return false;
            }
        }
        return true;
    }
}
