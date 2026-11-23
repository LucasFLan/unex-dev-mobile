package com.unex.chamaAi.usuario.domain.valueobjects;

import com.unex.chamaAi.shared.domain.exceptions.DadoInvalidoException;

import java.util.Arrays;
import java.util.Objects;

/**
 * Foto de perfil: JPEG, PNG ou WEBP de até {@link #TAMANHO_MAXIMO_BYTES}. O tipo é conferido pelos bytes.
 * O app reduz a foto no aparelho antes de enviar, então ela costuma ter bem menos que o limite.
 */
public record FotoPerfil(String contentType, byte[] conteudo) {

    public static final int TAMANHO_MAXIMO_BYTES = 5 * 1024 * 1024;

    public FotoPerfil {
        if (conteudo == null || conteudo.length == 0) {
            throw new DadoInvalidoException("Envie a foto de perfil.");
        }
        if (conteudo.length > TAMANHO_MAXIMO_BYTES) {
            throw new DadoInvalidoException("A foto de perfil pode ter no máximo 5 MB.");
        }
        contentType = FormatoImagem.detectar(conteudo);
        if (contentType == null) {
            throw new DadoInvalidoException("Envie a foto de perfil em JPG, PNG ou WEBP.");
        }
    }

    /** Cria a partir do upload ou do arquivo gravado: o content type sempre vem dos bytes. */
    public static FotoPerfil de(byte[] conteudo) {
        return new FotoPerfil(null, conteudo);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof FotoPerfil outra && Objects.equals(contentType, outra.contentType)
                && Arrays.equals(conteudo, outra.conteudo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contentType, Arrays.hashCode(conteudo));
    }

    @Override
    public String toString() {
        return "FotoPerfil[" + contentType + ", " + conteudo.length + " bytes]";
    }
}
