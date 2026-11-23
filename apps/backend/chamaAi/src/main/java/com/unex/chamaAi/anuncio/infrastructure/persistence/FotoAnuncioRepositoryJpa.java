package com.unex.chamaAi.anuncio.infrastructure.persistence;

import com.unex.chamaAi.anuncio.domain.repositories.FotoAnuncioRepository;
import com.unex.chamaAi.anuncio.domain.valueobjects.FotoAnuncio;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * Metadados no banco (tabela {@code anuncios_fotos}) e os bytes numa pasta local
 * ({@code app.anuncios.fotos-diretorio}): {@code <diretorio>/<anuncioId>/<fotoId>}, sem extensão.
 */
@Repository
public class FotoAnuncioRepositoryJpa implements FotoAnuncioRepository {

    private final FotoAnuncioSpringDataRepository springData;
    private final Path raiz;

    public FotoAnuncioRepositoryJpa(FotoAnuncioSpringDataRepository springData,
                                    @Value("${app.anuncios.fotos-diretorio}") String diretorio) {
        this.springData = springData;
        this.raiz = Path.of(diretorio).toAbsolutePath().normalize();
    }

    /** Fotos enviadas juntas ganham 1 µs de diferença cada, para a ordem do envio ser mantida. */
    @Override
    public List<UUID> gravar(UUID anuncioId, List<FotoAnuncio> fotos) {
        Instant agora = Instant.now().truncatedTo(ChronoUnit.MICROS);
        List<UUID> ids = new ArrayList<>(fotos.size());
        List<FotoAnuncioJpaEntity> entidades = new ArrayList<>(fotos.size());
        try {
            for (int i = 0; i < fotos.size(); i++) {
                FotoAnuncio foto = fotos.get(i);
                UUID id = UUID.randomUUID();
                escrever(anuncioId, id, foto.conteudo());
                ids.add(id);
                entidades.add(new FotoAnuncioJpaEntity(id, anuncioId, foto.contentType(), agora.plus(i, ChronoUnit.MICROS)));
            }
            springData.saveAllAndFlush(entidades);
        } catch (RuntimeException e) {
            ids.forEach(id -> apagar(arquivo(anuncioId, id)));
            throw e;
        }
        return ids;
    }

    @Override
    public Map<UUID, List<UUID>> listarIds(Collection<UUID> anuncioIds) {
        if (anuncioIds.isEmpty()) {
            return Map.of();
        }
        Map<UUID, List<UUID>> porAnuncio = new LinkedHashMap<>();
        for (FotoAnuncioJpaEntity foto : springData.findByAnuncioIdInOrderByCriadoEmAscIdAsc(anuncioIds)) {
            porAnuncio.computeIfAbsent(foto.getAnuncioId(), chave -> new ArrayList<>()).add(foto.getId());
        }
        return porAnuncio;
    }

    @Override
    public Optional<FotoAnuncio> buscar(UUID anuncioId, UUID fotoId) {
        return springData.findByIdAndAnuncioId(fotoId, anuncioId).flatMap(foto -> ler(anuncioId, fotoId));
    }

    @Override
    public boolean remover(UUID anuncioId, UUID fotoId) {
        Optional<FotoAnuncioJpaEntity> foto = springData.findByIdAndAnuncioId(fotoId, anuncioId);
        foto.ifPresent(encontrada -> {
            springData.delete(encontrada);
            springData.flush();
            apagar(arquivo(anuncioId, fotoId));
        });
        return foto.isPresent();
    }

    @Override
    public void removerArquivos(UUID anuncioId) {
        Path pasta = raiz.resolve(anuncioId.toString());
        if (!Files.isDirectory(pasta)) {
            return;
        }
        try (Stream<Path> caminhos = Files.walk(pasta)) {
            caminhos.sorted(Comparator.reverseOrder()).forEach(FotoAnuncioRepositoryJpa::apagar);
        } catch (IOException ignorado) {
            // Arquivo órfão no disco não afeta o app.
        }
    }

    private void escrever(UUID anuncioId, UUID fotoId, byte[] conteudo) {
        Path destino = arquivo(anuncioId, fotoId);
        try {
            Files.createDirectories(destino.getParent());
            Path temporario = Files.createTempFile(destino.getParent(), "upload-", ".tmp");
            Files.write(temporario, conteudo);
            Files.move(temporario, destino, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            throw new UncheckedIOException("Não foi possível gravar a foto do anúncio.", e);
        }
    }

    private Optional<FotoAnuncio> ler(UUID anuncioId, UUID fotoId) {
        Path arquivo = arquivo(anuncioId, fotoId);
        if (!Files.isRegularFile(arquivo)) {
            return Optional.empty();
        }
        try {
            return Optional.of(FotoAnuncio.de(Files.readAllBytes(arquivo)));
        } catch (IOException e) {
            throw new UncheckedIOException("Não foi possível ler a foto do anúncio.", e);
        }
    }

    private static void apagar(Path caminho) {
        try {
            Files.deleteIfExists(caminho);
        } catch (IOException ignorado) {
            // Arquivo órfão no disco não afeta o app.
        }
    }

    /** Os dois nomes vêm de UUIDs, então não há como sair da pasta raiz. */
    private Path arquivo(UUID anuncioId, UUID fotoId) {
        return raiz.resolve(anuncioId.toString()).resolve(fotoId.toString());
    }
}
