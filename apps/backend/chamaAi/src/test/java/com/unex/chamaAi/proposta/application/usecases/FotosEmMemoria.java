package com.unex.chamaAi.proposta.application.usecases;

import com.unex.chamaAi.proposta.domain.repositories.FotoPropostaRepository;
import com.unex.chamaAi.proposta.domain.valueobjects.FotoProposta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/** Fotos de proposta guardadas em memória, para os testes de caso de uso. */
class FotosEmMemoria implements FotoPropostaRepository {

    private final Map<UUID, Map<UUID, FotoProposta>> porProposta = new LinkedHashMap<>();

    @Override
    public List<UUID> gravar(UUID propostaId, List<FotoProposta> fotos) {
        Map<UUID, FotoProposta> daProposta = porProposta.computeIfAbsent(propostaId, chave -> new LinkedHashMap<>());
        List<UUID> ids = new ArrayList<>();
        for (FotoProposta foto : fotos) {
            UUID id = UUID.randomUUID();
            daProposta.put(id, foto);
            ids.add(id);
        }
        return ids;
    }

    @Override
    public Map<UUID, List<UUID>> listarIds(Collection<UUID> propostaIds) {
        Map<UUID, List<UUID>> resultado = new LinkedHashMap<>();
        for (UUID propostaId : propostaIds) {
            Map<UUID, FotoProposta> daProposta = porProposta.get(propostaId);
            if (daProposta != null && !daProposta.isEmpty()) {
                resultado.put(propostaId, List.copyOf(daProposta.keySet()));
            }
        }
        return resultado;
    }

    @Override
    public Optional<FotoProposta> buscar(UUID propostaId, UUID fotoId) {
        return Optional.ofNullable(porProposta.getOrDefault(propostaId, Map.of()).get(fotoId));
    }
}
