package com.unex.chamaAi.usuario.domain.valueobjects;

import com.unex.chamaAi.shared.domain.exceptions.DadoInvalidoException;

import java.util.Arrays;
import java.util.Objects;

/**
 * Imagem enviada para validação. Aceita JPEG, PNG ou WEBP de até {@link #TAMANHO_MAXIMO_BYTES}. O tipo é
 * conferido pelos primeiros bytes do arquivo, não pela extensão ou pelo cabeçalho enviado.
 */
public record ArquivoValidacao(TipoArquivoValidacao tipo, String contentType, byte[] conteudo) {

    public static final int TAMANHO_MAXIMO_BYTES = 5 * 1024 * 1024;

    public ArquivoValidacao {
        Objects.requireNonNull(tipo, "O tipo do arquivo é obrigatório.");
        String nome = tipo == TipoArquivoValidacao.DOCUMENTO ? "a foto do documento" : "a selfie com o documento";
        if (conteudo == null || conteudo.length == 0) {
            throw new DadoInvalidoException("Envie " + nome + ".");
        }
        if (conteudo.length > TAMANHO_MAXIMO_BYTES) {
            throw new DadoInvalidoException("Cada imagem pode ter no máximo 5 MB.");
        }
        contentType = detectarTipo(conteudo);
        if (contentType == null) {
            throw new DadoInvalidoException("Envie " + nome + " em JPG, PNG ou WEBP.");
        }
    }

    /** Cria a partir do upload: o content type é sempre recalculado a partir dos bytes. */
    public static ArquivoValidacao de(TipoArquivoValidacao tipo, byte[] conteudo) {
        return new ArquivoValidacao(tipo, null, conteudo);
    }

    private static String detectarTipo(byte[] bytes) {
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
        return o instanceof ArquivoValidacao outro && tipo == outro.tipo
                && Objects.equals(contentType, outro.contentType) && Arrays.equals(conteudo, outro.conteudo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tipo, contentType, Arrays.hashCode(conteudo));
    }

    @Override
    public String toString() {
        return "ArquivoValidacao[" + tipo + ", " + contentType + ", " + conteudo.length + " bytes]";
    }
}
