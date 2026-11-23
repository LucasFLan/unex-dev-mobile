package com.unex.chamaAi.proposta.domain.repositories;

import com.unex.chamaAi.proposta.domain.entities.Proposta;
import com.unex.chamaAi.shared.domain.paginacao.Pagina;
import com.unex.chamaAi.shared.domain.paginacao.Paginacao;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PropostaRepository {

    /** Insere ou atualiza. */
    void salvar(Proposta proposta);

    Optional<Proposta> buscarPorId(UUID id);

    /** Há proposta PENDENTE ou ACEITA desta pessoa para o anúncio? Encerradas não contam. */
    boolean existeEmAbertoPorAnuncioERemetente(UUID anuncioId, UUID remetenteId);

    /** Propostas ainda pendentes de um anúncio. */
    List<Proposta> buscarPendentesPorAnuncio(UUID anuncioId);

    /** Propostas recebidas pelo usuário, da mais recente para a mais antiga. */
    Pagina<Proposta> buscarPorDestinatario(UUID destinatarioId, Paginacao paginacao);

    /** Propostas enviadas pelo usuário, da mais recente para a mais antiga. */
    Pagina<Proposta> buscarPorRemetente(UUID remetenteId, Paginacao paginacao);
}
