package com.unex.chamaAi.anuncio.infrastructure.persistence;

import com.unex.chamaAi.anuncio.domain.repositories.FotoAnuncioRepository;
import com.unex.chamaAi.anuncio.domain.valueobjects.FotoAnuncio;
import com.unex.chamaAi.shared.domain.valueobjects.Imagem;
import com.unex.chamaAi.shared.infrastructure.storage.PastaDeImagens;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/** Fotos em {@code app.anuncios.diretorio}: {@code <anuncioId>/<fotoId>.jpg}. */
@Repository
public class FotoAnuncioRepositoryJpa implements FotoAnuncioRepository {

    private final FotoAnuncioSpringDataRepository springData;
    private final PastaDeImagens pasta;

    public FotoAnuncioRepositoryJpa(FotoAnuncioSpringDataRepository springData,
                                    @Value("${app.anuncios.diretorio}") String diretorio) {
        this.springData = springData;
        this.pasta = new PastaDeImagens(diretorio);
    }

    @Override
    public List<FotoAnuncio> listar(UUID anuncioId) {
        return springData.findByAnuncioIdOrderByCriadoEmAscIdAsc(anuncioId).stream()
                .map(FotoAnuncioRepositoryJpa::paraDominio).toList();
    }

    @Override
    public Map<UUID, List<FotoAnuncio>> listar(Collection<UUID> anuncioIds) {
        if (anuncioIds.isEmpty()) {
            return Map.of();
        }
        return springData.findByAnuncioIdInOrderByCriadoEmAscIdAsc(anuncioIds).stream()
                .map(FotoAnuncioRepositoryJpa::paraDominio)
                .collect(Collectors.groupingBy(FotoAnuncio::anuncioId, LinkedHashMap::new, Collectors.toList()));
    }

    /** Fotos enviadas juntas ganham 1 µs de diferença cada, para a ordem do envio ser mantida. */
    @Override
    public void adicionar(UUID anuncioId, List<Imagem> imagens, Instant enviadasEm) {
        List<FotoAnuncioJpaEntity> novas = new ArrayList<>();
        List<String> gravados = new ArrayList<>();
        try {
            for (int i = 0; i < imagens.size(); i++) {
                Imagem imagem = imagens.get(i);
                UUID id = UUID.randomUUID();
                String caminho = anuncioId + "/" + id + imagem.extensao();
                pasta.gravar(caminho, imagem.conteudo());
                gravados.add(caminho);
                novas.add(new FotoAnuncioJpaEntity(id, anuncioId, imagem.contentType(), caminho,
                        enviadasEm.plus(i, ChronoUnit.MICROS)));
            }
            springData.saveAllAndFlush(novas);
        } catch (RuntimeException e) {
            gravados.forEach(pasta::excluir);
            throw e;
        }
    }

    @Override
    public boolean remover(UUID anuncioId, UUID fotoId) {
        Optional<FotoAnuncioJpaEntity> foto = springData.findByIdAndAnuncioId(fotoId, anuncioId);
        foto.ifPresent(encontrada -> {
            springData.delete(encontrada);
            springData.flush();
            pasta.excluir(encontrada.getCaminho());
        });
        return foto.isPresent();
    }

    @Override
    public void removerTodas(UUID anuncioId) {
        pasta.excluir(anuncioId.toString());
    }

    @Override
    public Optional<Imagem> ler(UUID anuncioId, UUID fotoId) {
        return springData.findByIdAndAnuncioId(fotoId, anuncioId)
                .flatMap(foto -> pasta.ler(foto.getCaminho()).map(bytes -> new Imagem(foto.getContentType(), bytes)));
    }

    private static FotoAnuncio paraDominio(FotoAnuncioJpaEntity entidade) {
        return new FotoAnuncio(entidade.getId(), entidade.getAnuncioId(), entidade.getCriadoEm());
    }
}
