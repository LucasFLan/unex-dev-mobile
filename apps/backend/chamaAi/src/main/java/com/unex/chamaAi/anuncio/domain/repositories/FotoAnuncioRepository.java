package com.unex.chamaAi.anuncio.domain.repositories;

import com.unex.chamaAi.anuncio.domain.valueobjects.FotoAnuncio;
import com.unex.chamaAi.shared.domain.valueobjects.Imagem;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface FotoAnuncioRepository {

    /** Na ordem em que foram enviadas. */
    List<FotoAnuncio> listar(UUID anuncioId);

    /** Fotos de vários anúncios de uma vez; anúncio sem foto fica de fora do mapa. */
    Map<UUID, List<FotoAnuncio>> listar(Collection<UUID> anuncioIds);

    void adicionar(UUID anuncioId, List<Imagem> imagens, Instant enviadasEm);

    /** false se a foto não existe ou é de outro anúncio. */
    boolean remover(UUID anuncioId, UUID fotoId);

    /** Apaga as fotos do disco antes de o anúncio ser excluído (as linhas saem em cascata). */
    void removerTodas(UUID anuncioId);

    Optional<Imagem> ler(UUID anuncioId, UUID fotoId);
}
