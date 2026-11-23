package com.unex.chamaAi.shared.infrastructure.storage;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Pasta local onde ficam as fotos enviadas (o banco guarda só o caminho relativo, com "/" como separador).
 * Caminhos que escapem da pasta raiz são recusados.
 */
public class PastaDeImagens {

    private final Path raiz;

    public PastaDeImagens(String diretorio) {
        this.raiz = Path.of(diretorio).toAbsolutePath().normalize();
    }

    /** Grava de forma atômica: quem lê nunca vê um arquivo pela metade. */
    public void gravar(String relativo, byte[] conteudo) {
        Path destino = resolver(relativo);
        try {
            Files.createDirectories(destino.getParent());
            Path temporario = Files.createTempFile(destino.getParent(), "upload-", ".tmp");
            Files.write(temporario, conteudo);
            Files.move(temporario, destino, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            throw new UncheckedIOException("Não foi possível gravar a imagem.", e);
        }
    }

    /** Vazio se o arquivo não existir mais na pasta. */
    public Optional<byte[]> ler(String relativo) {
        Path arquivo = resolver(relativo);
        if (!Files.isRegularFile(arquivo)) {
            return Optional.empty();
        }
        try {
            return Optional.of(Files.readAllBytes(arquivo));
        } catch (IOException e) {
            throw new UncheckedIOException("Não foi possível ler a imagem.", e);
        }
    }

    /** Apaga um arquivo ou uma subpasta inteira. Falhas não interrompem a operação: sobra só lixo no disco. */
    public void excluir(String relativo) {
        Path alvo = resolver(relativo);
        if (!Files.exists(alvo) || alvo.equals(raiz)) {
            return;
        }
        try (Stream<Path> caminhos = Files.walk(alvo)) {
            caminhos.sorted(Comparator.reverseOrder()).forEach(caminho -> {
                try {
                    Files.deleteIfExists(caminho);
                } catch (IOException ignorado) {
                    // Arquivo órfão no disco não afeta o app.
                }
            });
        } catch (IOException ignorado) {
            // Idem.
        }
    }

    private Path resolver(String relativo) {
        Path caminho = raiz.resolve(relativo).normalize();
        if (!caminho.startsWith(raiz)) {
            throw new IllegalArgumentException("Caminho de arquivo fora da pasta de imagens.");
        }
        return caminho;
    }
}
