package com.unex.chamaAi.anuncio.application.ports;

import com.unex.chamaAi.anuncio.domain.valueobjects.Coordenadas;

import java.util.Optional;

/** Busca o endereço (e, quando houver, o ponto no mapa) de um CEP em um serviço externo. */
public interface ConsultaCep {

    /**
     * Vazio quando o CEP não existe. Lança {@link com.unex.chamaAi.shared.domain.exceptions.ServicoExternoIndisponivelException}
     * se o serviço não responder.
     */
    Optional<EnderecoCep> buscar(String cep);

    /** coordenadas nulas quando o serviço não tem o ponto daquele CEP. */
    record EnderecoCep(String cep, String logradouro, String bairro, String cidade, String uf,
                       Coordenadas coordenadas) {
    }
}
