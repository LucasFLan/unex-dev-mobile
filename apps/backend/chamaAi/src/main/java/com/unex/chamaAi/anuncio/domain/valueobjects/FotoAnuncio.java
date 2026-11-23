package com.unex.chamaAi.anuncio.domain.valueobjects;

import com.unex.chamaAi.shared.domain.exceptions.DadoInvalidoException;
import com.unex.chamaAi.usuario.domain.valueobjects.FormatoImagem;

import java.util.Arrays;
import java.util.Objects;

/**
 * Foto do anúncio (o serviço, trabalhos já feitos, o problema a resolver...). JPEG, PNG ou WEBP de até
 * {@link #TAMANHO_MAXIMO_BYTES}; o tipo é conferido pelos bytes. No máximo {@link #MAXIMO_POR_ANUNCIO}.
 */
public record FotoAnuncio(String contentType, byte[] conteudo) {

    public static final int TAMANHO_MAXIMO_BYTES = 5 * 1024 * 1024;
    public static final int MAXIMO_POR_ANUNCIO = 6;

    public FotoAnuncio {
        if (conteudo == null || conteudo.length == 0) {
            throw new DadoInvalidoException("Uma das fotos está vazia.");
        }
        if (conteudo.length > TAMANHO_MAXIMO_BYTES) {
            throw new DadoInvalidoException("Cada foto pode ter no máximo 5 MB.");
        }
        contentType = FormatoImagem.detectar(conteudo);
        if (contentType == null) {
            throw new DadoInvalidoException("Envie as fotos em JPG, PNG ou WEBP.");
        }
    }

    /** Cria a partir do upload ou do arquivo gravado: o content type sempre vem dos bytes. */
    public static FotoAnuncio de(byte[] conteudo) {
        return new FotoAnuncio(null, conteudo);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof FotoAnuncio outra && Objects.equals(contentType, outra.contentType)
                && Arrays.equals(conteudo, outra.conteudo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contentType, Arrays.hashCode(conteudo));
    }

    @Override
    public String toString() {
        return "FotoAnuncio[" + contentType + ", " + conteudo.length + " bytes]";
    }
}
