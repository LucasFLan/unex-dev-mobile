package com.unex.chamaAi.usuario.infrastructure.storage;

import com.unex.chamaAi.usuario.domain.valueobjects.ArquivoValidacao;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.UUID;

/**
 * Guarda as imagens de validação numa pasta local ({@code app.validacao.diretorio}), uma subpasta por envio:
 * {@code <diretorio>/<envioId>/documento.jpg}. O banco guarda só o caminho relativo.
 */
@Component
public class ArmazenamentoLocalArquivosValidacao {

    private final Path raiz;

    public ArmazenamentoLocalArquivosValidacao(@Value("${app.validacao.diretorio}") String diretorio) {
        this.raiz = Path.of(diretorio).toAbsolutePath().normalize();
    }

    /** Grava o arquivo e devolve o caminho relativo à pasta raiz (com "/" como separador). */
    public String gravar(UUID envioId, ArquivoValidacao arquivo) {
        String relativo = envioId + "/" + arquivo.tipo().name().toLowerCase() + extensao(arquivo.contentType());
        Path destino = resolver(relativo);
        try {
            Files.createDirectories(destino.getParent());
            Path temporario = Files.createTempFile(destino.getParent(), "upload-", ".tmp");
            Files.write(temporario, arquivo.conteudo());
            Files.move(temporario, destino, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            throw new UncheckedIOException("Não foi possível gravar a imagem de validação.", e);
        }
        return relativo;
    }

    /** Lê o arquivo gravado; vazio se ele não existir mais na pasta. */
    public Optional<byte[]> ler(String relativo) {
        Path arquivo = resolver(relativo);
        if (!Files.isRegularFile(arquivo)) {
            return Optional.empty();
        }
        try {
            return Optional.of(Files.readAllBytes(arquivo));
        } catch (IOException e) {
            throw new UncheckedIOException("Não foi possível ler a imagem de validação.", e);
        }
    }

    private Path resolver(String relativo) {
        Path caminho = raiz.resolve(relativo).normalize();
        if (!caminho.startsWith(raiz)) {
            throw new IllegalArgumentException("Caminho de arquivo fora da pasta de validação.");
        }
        return caminho;
    }

    private static String extensao(String contentType) {
        return switch (contentType) {
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            default -> ".jpg";
        };
    }
}
