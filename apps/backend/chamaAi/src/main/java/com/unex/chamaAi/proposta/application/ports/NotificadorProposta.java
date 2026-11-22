package com.unex.chamaAi.proposta.application.ports;

import com.unex.chamaAi.proposta.domain.entities.Proposta;

/** Avisa as pessoas envolvidas em cada etapa de uma proposta. */
public interface NotificadorProposta {

    /** Para o autor do anúncio: chegou uma proposta. */
    void propostaRecebida(Proposta proposta, String tituloAnuncio, String nomeRemetente);

    /** Para quem enviou: a proposta foi aceita, recusada ou não selecionada (ver o status da proposta). */
    void propostaRespondida(Proposta proposta, String tituloAnuncio, String nomeDestinatario);
}
