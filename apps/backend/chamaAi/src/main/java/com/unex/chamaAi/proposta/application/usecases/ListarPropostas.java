package com.unex.chamaAi.proposta.application.usecases;

import com.unex.chamaAi.anuncio.domain.repositories.AnuncioRepository;
import com.unex.chamaAi.proposta.application.dto.PropostaDetalhada;
import com.unex.chamaAi.proposta.domain.repositories.PropostaRepository;
import com.unex.chamaAi.shared.domain.paginacao.Pagina;
import com.unex.chamaAi.shared.domain.paginacao.Paginacao;
import com.unex.chamaAi.usuario.domain.repositories.UsuarioRepository;

import java.util.UUID;

/** Propostas recebidas (nos anúncios do usuário) ou enviadas por ele. */
public class ListarPropostas {

    private final PropostaRepository propostas;
    private final DetalhadorProposta detalhador;

    public ListarPropostas(PropostaRepository propostas, AnuncioRepository anuncios, UsuarioRepository usuarios) {
        this.propostas = propostas;
        this.detalhador = new DetalhadorProposta(anuncios, usuarios);
    }

    public Pagina<PropostaDetalhada> recebidas(UUID usuarioId, Integer pagina, Integer tamanho) {
        return detalhador.detalhar(propostas.buscarPorDestinatario(usuarioId, Paginacao.de(pagina, tamanho)));
    }

    public Pagina<PropostaDetalhada> enviadas(UUID usuarioId, Integer pagina, Integer tamanho) {
        return detalhador.detalhar(propostas.buscarPorRemetente(usuarioId, Paginacao.de(pagina, tamanho)));
    }
}
