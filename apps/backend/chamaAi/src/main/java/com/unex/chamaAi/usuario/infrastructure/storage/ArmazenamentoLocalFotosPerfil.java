package com.unex.chamaAi.usuario.infrastructure.storage;

import com.unex.chamaAi.usuario.domain.repositories.FotoPerfilRepository;
import com.unex.chamaAi.usuario.domain.valueobjects.FotoPerfil;
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
 * Guarda as fotos de perfil numa pasta local ({@code app.perfil.fotos-diretorio}), um arquivo por usuário com o
 * id como nome e sem extensão: trocar a foto sobrescreve o mesmo arquivo, e o formato é lido dos bytes.
 */
@Component
public class ArmazenamentoLocalFotosPerfil implements FotoPerfilRepository {

    private final Path raiz;

    public ArmazenamentoLocalFotosPerfil(@Value("${app.perfil.fotos-diretorio}") String diretorio) {
        this.raiz = Path.of(diretorio).toAbsolutePath().normalize();
    }

    @Override
    public void gravar(UUID usuarioId, FotoPerfil foto) {
        Path destino = arquivo(usuarioId);
        try {
            Files.createDirectories(raiz);
            Path temporario = Files.createTempFile(raiz, "upload-", ".tmp");
            Files.write(temporario, foto.conteudo());
            Files.move(temporario, destino, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            throw new UncheckedIOException("Não foi possível gravar a foto de perfil.", e);
        }
    }

    @Override
    public Optional<FotoPerfil> buscar(UUID usuarioId) {
        Path arquivo = arquivo(usuarioId);
        if (!Files.isRegularFile(arquivo)) {
            return Optional.empty();
        }
        try {
            return Optional.of(FotoPerfil.de(Files.readAllBytes(arquivo)));
        } catch (IOException e) {
            throw new UncheckedIOException("Não foi possível ler a foto de perfil.", e);
        }
    }

    @Override
    public void remover(UUID usuarioId) {
        try {
            Files.deleteIfExists(arquivo(usuarioId));
        } catch (IOException e) {
            throw new UncheckedIOException("Não foi possível apagar a foto de perfil.", e);
        }
    }

    /** O nome vem de um UUID, então não há como sair da pasta raiz. */
    private Path arquivo(UUID usuarioId) {
        return raiz.resolve(usuarioId.toString());
    }
}
