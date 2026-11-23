package com.unex.chamaAi.anuncio.domain.repositories;

import com.unex.chamaAi.anuncio.domain.valueobjects.FotoAnuncio;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/** Fotos dos anúncios: o autor adiciona e remove; uma foto gravada nunca muda (trocar = remover + enviar). */
public interface FotoAnuncioRepository {

    /** Grava depois das que já existem, na ordem recebida, e devolve o id de cada uma. */
    List<UUID> gravar(UUID anuncioId, List<FotoAnuncio> fotos);

    /** Ids das fotos de cada anúncio, na ordem do envio. Anúncios sem foto ficam de fora do mapa. */
    Map<UUID, List<UUID>> listarIds(Collection<UUID> anuncioIds);

    Optional<FotoAnuncio> buscar(UUID anuncioId, UUID fotoId);

    /** false se a foto não existe ou é de outro anúncio. */
    boolean remover(UUID anuncioId, UUID fotoId);

    /** Apaga os arquivos antes de o anúncio ser excluído (as linhas saem em cascata no banco). */
    void removerArquivos(UUID anuncioId);
}
