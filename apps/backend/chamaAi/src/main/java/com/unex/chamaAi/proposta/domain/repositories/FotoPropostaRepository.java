package com.unex.chamaAi.proposta.domain.repositories;

import com.unex.chamaAi.proposta.domain.valueobjects.FotoProposta;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/** Fotos anexadas às propostas. Só são gravadas junto com a proposta e não mudam depois. */
public interface FotoPropostaRepository {

    /** Grava as fotos na ordem recebida e devolve o id de cada uma, na mesma ordem. */
    List<UUID> gravar(UUID propostaId, List<FotoProposta> fotos);

    /** Ids das fotos de cada proposta, na ordem do envio. Propostas sem foto ficam de fora do mapa. */
    Map<UUID, List<UUID>> listarIds(Collection<UUID> propostaIds);

    Optional<FotoProposta> buscar(UUID propostaId, UUID fotoId);
}
