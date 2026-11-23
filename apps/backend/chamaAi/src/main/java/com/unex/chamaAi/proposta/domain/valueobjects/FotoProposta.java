package com.unex.chamaAi.proposta.domain.valueobjects;

import com.unex.chamaAi.shared.domain.exceptions.DadoInvalidoException;
import com.unex.chamaAi.usuario.domain.valueobjects.FormatoImagem;

import java.util.Arrays;
import java.util.Objects;

/**
 * Foto anexada a uma proposta (o serviço já feito, o problema a resolver, o material...). JPEG, PNG ou WEBP de
 * até {@link #TAMANHO_MAXIMO_BYTES}; o tipo é conferido pelos bytes. No máximo {@link #MAXIMO_POR_PROPOSTA}.
 */
public record FotoProposta(String contentType, byte[] conteudo) {

    public static final int TAMANHO_MAXIMO_BYTES = 5 * 1024 * 1024;
    public static final int MAXIMO_POR_PROPOSTA = 5;

    public FotoProposta {
        if (conteudo == null || conteudo.length == 0) {
            throw new DadoInvalidoException("Uma das fotos anexadas está vazia.");
        }
        if (conteudo.length > TAMANHO_MAXIMO_BYTES) {
            throw new DadoInvalidoException("Cada foto pode ter no máximo 5 MB.");
        }
        contentType = FormatoImagem.detectar(conteudo);
        if (contentType == null) {
            throw new DadoInvalidoException("Anexe as fotos em JPG, PNG ou WEBP.");
        }
    }

    /** Cria a partir do upload ou do arquivo gravado: o content type sempre vem dos bytes. */
    public static FotoProposta de(byte[] conteudo) {
        return new FotoProposta(null, conteudo);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof FotoProposta outra && Objects.equals(contentType, outra.contentType)
                && Arrays.equals(conteudo, outra.conteudo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contentType, Arrays.hashCode(conteudo));
    }

    @Override
    public String toString() {
        return "FotoProposta[" + contentType + ", " + conteudo.length + " bytes]";
    }
}
