package com.unex.chamaAi.shared.domain.valueobjects;

import com.unex.chamaAi.shared.domain.exceptions.DadoInvalidoException;

import java.util.Arrays;
import java.util.Objects;

/**
 * Foto enviada por um usuário (perfil, anúncio). Aceita JPEG, PNG ou WEBP de até {@link #TAMANHO_MAXIMO_BYTES}.
 * O tipo é conferido pelos primeiros bytes do arquivo, não pela extensão ou pelo cabeçalho enviado.
 */
public record Imagem(String contentType, byte[] conteudo) {

    public static final int TAMANHO_MAXIMO_BYTES = 5 * 1024 * 1024;

    public Imagem {
        Objects.requireNonNull(contentType, "O tipo da imagem é obrigatório.");
        Objects.requireNonNull(conteudo, "O conteúdo da imagem é obrigatório.");
    }

    /**
     * Valida o upload e descobre o tipo pelos bytes.
     *
     * @param descricao como a imagem é chamada nas mensagens de erro (ex.: "a foto de perfil")
     */
    public static Imagem de(byte[] conteudo, String descricao) {
        if (conteudo == null || conteudo.length == 0) {
            throw new DadoInvalidoException("Envie " + descricao + ".");
        }
        if (conteudo.length > TAMANHO_MAXIMO_BYTES) {
            throw new DadoInvalidoException("Cada imagem pode ter no máximo 5 MB.");
        }
        String tipo = detectarTipo(conteudo);
        if (tipo == null) {
            throw new DadoInvalidoException("Envie " + descricao + " em JPG, PNG ou WEBP.");
        }
        return new Imagem(tipo, conteudo);
    }

    /** Extensão do arquivo gravado em disco. */
    public String extensao() {
        return switch (contentType) {
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            default -> ".jpg";
        };
    }

    static String detectarTipo(byte[] bytes) {
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

    @Override
    public boolean equals(Object o) {
        return o instanceof Imagem outra && contentType.equals(outra.contentType)
                && Arrays.equals(conteudo, outra.conteudo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contentType, Arrays.hashCode(conteudo));
    }

    @Override
    public String toString() {
        return "Imagem[" + contentType + ", " + conteudo.length + " bytes]";
    }
}
