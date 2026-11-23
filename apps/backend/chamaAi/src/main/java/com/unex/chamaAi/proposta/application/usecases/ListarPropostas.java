package com.unex.chamaAi.proposta.application.usecases;

import com.unex.chamaAi.anuncio.domain.repositories.AnuncioRepository;
import com.unex.chamaAi.proposta.application.dto.PropostaDetalhada;
import com.unex.chamaAi.proposta.domain.exceptions.FotoPropostaNaoEncontradaException;
import com.unex.chamaAi.proposta.domain.exceptions.PropostaNaoEncontradaException;
import com.unex.chamaAi.proposta.domain.repositories.FotoPropostaRepository;
import com.unex.chamaAi.proposta.domain.repositories.PropostaRepository;
import com.unex.chamaAi.proposta.domain.valueobjects.FotoProposta;
import com.unex.chamaAi.shared.domain.paginacao.Pagina;
import com.unex.chamaAi.shared.domain.paginacao.Paginacao;
import com.unex.chamaAi.usuario.domain.repositories.UsuarioRepository;

import java.util.UUID;

/** Propostas recebidas (nos anúncios do usuário) ou enviadas por ele. */
public class ListarPropostas {

    private final PropostaRepository propostas;
    private final FotoPropostaRepository fotos;
    private final DetalhadorProposta detalhador;

    public ListarPropostas(PropostaRepository propostas, AnuncioRepository anuncios, UsuarioRepository usuarios,
                           FotoPropostaRepository fotos) {
        this.propostas = propostas;
        this.fotos = fotos;
        this.detalhador = new DetalhadorProposta(anuncios, usuarios, fotos);
    }

    public Pagina<PropostaDetalhada> recebidas(UUID usuarioId, Integer pagina, Integer tamanho) {
        return detalhador.detalhar(propostas.buscarPorDestinatario(usuarioId, Paginacao.de(pagina, tamanho)));
    }

    public Pagina<PropostaDetalhada> enviadas(UUID usuarioId, Integer pagina, Integer tamanho) {
        return detalhador.detalhar(propostas.buscarPorRemetente(usuarioId, Paginacao.de(pagina, tamanho)));
    }

    /** Foto anexada: só quem enviou ou recebeu a proposta vê. Para os outros, ela "não existe". */
    public FotoProposta foto(UUID propostaId, UUID fotoId, UUID usuarioId) {
        propostas.buscarPorId(propostaId)
                .filter(proposta -> proposta.foiEnviadaPor(usuarioId) || proposta.foiRecebidaPor(usuarioId))
                .orElseThrow(PropostaNaoEncontradaException::new);
        return fotos.buscar(propostaId, fotoId).orElseThrow(FotoPropostaNaoEncontradaException::new);
    }
}
