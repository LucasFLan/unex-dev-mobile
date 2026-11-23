package com.unex.chamaAi.proposta.infrastructure.persistence;

import com.unex.chamaAi.proposta.domain.repositories.FotoPropostaRepository;
import com.unex.chamaAi.proposta.domain.valueobjects.FotoProposta;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Metadados no banco (tabela {@code propostas_fotos}) e os bytes numa pasta local
 * ({@code app.propostas.fotos-diretorio}): {@code <diretorio>/<propostaId>/<fotoId>}, sem extensão.
 */
@Repository
public class FotoPropostaRepositoryJpa implements FotoPropostaRepository {

    private final FotoPropostaSpringDataRepository springData;
    private final Path raiz;

    public FotoPropostaRepositoryJpa(FotoPropostaSpringDataRepository springData,
                                     @Value("${app.propostas.fotos-diretorio}") String diretorio) {
        this.springData = springData;
        this.raiz = Path.of(diretorio).toAbsolutePath().normalize();
    }

    @Override
    public List<UUID> gravar(UUID propostaId, List<FotoProposta> fotos) {
        Instant agora = Instant.now().truncatedTo(ChronoUnit.MICROS);
        List<UUID> ids = new ArrayList<>(fotos.size());
        List<FotoPropostaJpaEntity> entidades = new ArrayList<>(fotos.size());
        for (int i = 0; i < fotos.size(); i++) {
            FotoProposta foto = fotos.get(i);
            UUID id = UUID.randomUUID();
            escrever(propostaId, id, foto.conteudo());
            ids.add(id);
            entidades.add(new FotoPropostaJpaEntity(id, propostaId, (short) i, foto.contentType(), agora));
        }
        springData.saveAll(entidades);
        return ids;
    }

    @Override
    public Map<UUID, List<UUID>> listarIds(Collection<UUID> propostaIds) {
        Map<UUID, List<UUID>> porProposta = new LinkedHashMap<>();
        for (FotoPropostaJpaEntity foto : springData.findByPropostaIdInOrderByPropostaIdAscOrdemAsc(propostaIds)) {
            porProposta.computeIfAbsent(foto.getPropostaId(), chave -> new ArrayList<>()).add(foto.getId());
        }
        return porProposta;
    }

    @Override
    public Optional<FotoProposta> buscar(UUID propostaId, UUID fotoId) {
        return springData.findByIdAndPropostaId(fotoId, propostaId).flatMap(foto -> ler(propostaId, fotoId));
    }

    private void escrever(UUID propostaId, UUID fotoId, byte[] conteudo) {
        Path destino = arquivo(propostaId, fotoId);
        try {
            Files.createDirectories(destino.getParent());
            Path temporario = Files.createTempFile(destino.getParent(), "upload-", ".tmp");
            Files.write(temporario, conteudo);
            Files.move(temporario, destino, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            throw new UncheckedIOException("Não foi possível gravar a foto da proposta.", e);
        }
    }

    private Optional<FotoProposta> ler(UUID propostaId, UUID fotoId) {
        Path arquivo = arquivo(propostaId, fotoId);
        if (!Files.isRegularFile(arquivo)) {
            return Optional.empty();
        }
        try {
            return Optional.of(FotoProposta.de(Files.readAllBytes(arquivo)));
        } catch (IOException e) {
            throw new UncheckedIOException("Não foi possível ler a foto da proposta.", e);
        }
    }

    /** Os dois nomes vêm de UUIDs, então não há como sair da pasta raiz. */
    private Path arquivo(UUID propostaId, UUID fotoId) {
        return raiz.resolve(propostaId.toString()).resolve(fotoId.toString());
    }
}
