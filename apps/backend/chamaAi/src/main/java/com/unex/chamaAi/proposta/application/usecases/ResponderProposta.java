package com.unex.chamaAi.proposta.application.usecases;

import com.unex.chamaAi.anuncio.domain.entities.Anuncio;
import com.unex.chamaAi.anuncio.domain.repositories.AnuncioRepository;
import com.unex.chamaAi.anuncio.domain.valueobjects.TipoAnuncio;
import com.unex.chamaAi.proposta.application.dto.PropostaDetalhada;
import com.unex.chamaAi.proposta.application.ports.NotificadorProposta;
import com.unex.chamaAi.proposta.domain.entities.Proposta;
import com.unex.chamaAi.proposta.domain.exceptions.AcessoNegadoPropostaException;
import com.unex.chamaAi.proposta.domain.exceptions.PropostaNaoEncontradaException;
import com.unex.chamaAi.proposta.domain.repositories.PropostaRepository;
import com.unex.chamaAi.usuario.domain.entities.Usuario;
import com.unex.chamaAi.usuario.domain.repositories.UsuarioRepository;

import java.util.UUID;

/** Ações sobre uma proposta já enviada: o autor do anúncio aceita ou recusa; quem enviou pode cancelar. */
public class ResponderProposta {

    private final PropostaRepository propostas;
    private final AnuncioRepository anuncios;
    private final UsuarioRepository usuarios;
    private final NotificadorProposta notificador;
    private final DetalhadorProposta detalhador;

    public ResponderProposta(PropostaRepository propostas, AnuncioRepository anuncios, UsuarioRepository usuarios,
                             NotificadorProposta notificador) {
        this.propostas = propostas;
        this.anuncios = anuncios;
        this.usuarios = usuarios;
        this.notificador = notificador;
        this.detalhador = new DetalhadorProposta(anuncios, usuarios);
    }

    /**
     * Aceita a proposta e libera o contato entre as duas pessoas. Num PEDIDO o cliente já contratou alguém:
     * o pedido é pausado e as outras propostas pendentes viram NAO_SELECIONADA. Numa OFERTA o profissional
     * pode atender vários clientes, então nada mais muda.
     */
    public PropostaDetalhada aceitar(UUID propostaId, UUID usuarioId) {
        Proposta proposta = recebida(propostaId, usuarioId);
        Anuncio anuncio = anuncios.buscarPorId(proposta.getAnuncioId()).orElseThrow(PropostaNaoEncontradaException::new);
        String nomeAutor = nome(usuarioId);

        proposta.aceitar();
        propostas.salvar(proposta);
        notificador.propostaRespondida(proposta, anuncio.getTitulo(), nomeAutor);

        if (anuncio.getTipo() == TipoAnuncio.PEDIDO) {
            anuncio.desativar();
            anuncios.salvar(anuncio);
            for (Proposta outra : propostas.buscarPendentesPorAnuncio(anuncio.getId())) {
                outra.naoSelecionar();
                propostas.salvar(outra);
                notificador.propostaRespondida(outra, anuncio.getTitulo(), nomeAutor);
            }
        }
        return detalhador.detalhar(proposta);
    }

    public PropostaDetalhada recusar(UUID propostaId, UUID usuarioId) {
        Proposta proposta = recebida(propostaId, usuarioId);
        String titulo = anuncios.buscarPorId(proposta.getAnuncioId()).map(Anuncio::getTitulo).orElse("seu anúncio");

        proposta.recusar();
        propostas.salvar(proposta);
        notificador.propostaRespondida(proposta, titulo, nome(usuarioId));
        return detalhador.detalhar(proposta);
    }

    /** Só quem enviou pode retirar a proposta, enquanto ela estiver pendente. */
    public PropostaDetalhada cancelar(UUID propostaId, UUID usuarioId) {
        Proposta proposta = visivel(propostaId, usuarioId);
        if (!proposta.foiEnviadaPor(usuarioId)) {
            throw new AcessoNegadoPropostaException("Só quem enviou a proposta pode cancelá-la.");
        }
        proposta.cancelar();
        propostas.salvar(proposta);
        return detalhador.detalhar(proposta);
    }

    /** Só o autor do anúncio responde. */
    private Proposta recebida(UUID propostaId, UUID usuarioId) {
        Proposta proposta = visivel(propostaId, usuarioId);
        if (!proposta.foiRecebidaPor(usuarioId)) {
            throw new AcessoNegadoPropostaException("Só o autor do anúncio pode responder a esta proposta.");
        }
        return proposta;
    }

    /** Para quem não está envolvido, a proposta "não existe". */
    private Proposta visivel(UUID propostaId, UUID usuarioId) {
        return propostas.buscarPorId(propostaId)
                .filter(proposta -> proposta.foiEnviadaPor(usuarioId) || proposta.foiRecebidaPor(usuarioId))
                .orElseThrow(PropostaNaoEncontradaException::new);
    }

    private String nome(UUID usuarioId) {
        return usuarios.buscarPorId(usuarioId).map(Usuario::getNome).orElse("O anunciante");
    }
}
