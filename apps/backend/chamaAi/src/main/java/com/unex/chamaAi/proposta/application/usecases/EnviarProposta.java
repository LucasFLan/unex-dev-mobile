package com.unex.chamaAi.proposta.application.usecases;

import com.unex.chamaAi.anuncio.domain.entities.Anuncio;
import com.unex.chamaAi.anuncio.domain.exceptions.AnuncioNaoEncontradoException;
import com.unex.chamaAi.anuncio.domain.repositories.AnuncioRepository;
import com.unex.chamaAi.anuncio.domain.valueobjects.Preco;
import com.unex.chamaAi.proposta.application.dto.EnviarPropostaCommand;
import com.unex.chamaAi.proposta.application.dto.PropostaDetalhada;
import com.unex.chamaAi.proposta.application.ports.NotificadorProposta;
import com.unex.chamaAi.proposta.domain.entities.Proposta;
import com.unex.chamaAi.proposta.domain.exceptions.PropostaDuplicadaException;
import com.unex.chamaAi.proposta.domain.exceptions.PropostaParaProprioAnuncioException;
import com.unex.chamaAi.proposta.domain.repositories.PropostaRepository;
import com.unex.chamaAi.usuario.domain.entities.Usuario;
import com.unex.chamaAi.usuario.domain.exceptions.ContaNaoValidadaException;
import com.unex.chamaAi.usuario.domain.exceptions.UsuarioNaoEncontradoException;
import com.unex.chamaAi.usuario.domain.repositories.UsuarioRepository;

import java.util.UUID;

public class EnviarProposta {

    private final PropostaRepository propostas;
    private final AnuncioRepository anuncios;
    private final UsuarioRepository usuarios;
    private final NotificadorProposta notificador;
    private final DetalhadorProposta detalhador;

    public EnviarProposta(PropostaRepository propostas, AnuncioRepository anuncios, UsuarioRepository usuarios,
                          NotificadorProposta notificador) {
        this.propostas = propostas;
        this.anuncios = anuncios;
        this.usuarios = usuarios;
        this.notificador = notificador;
        this.detalhador = new DetalhadorProposta(anuncios, usuarios);
    }

    /**
     * Envia a proposta para o autor do anúncio e o notifica. Anúncio inativo de outra pessoa "não existe";
     * não dá para propor no próprio anúncio nem mandar duas propostas para o mesmo anúncio.
     */
    public PropostaDetalhada executar(UUID anuncioId, UUID remetenteId, EnviarPropostaCommand comando) {
        Anuncio anuncio = anuncios.buscarPorId(anuncioId)
                .filter(encontrado -> encontrado.visivelPara(remetenteId))
                .orElseThrow(AnuncioNaoEncontradoException::new);
        if (anuncio.pertenceA(remetenteId)) {
            throw new PropostaParaProprioAnuncioException();
        }
        Usuario remetente = usuarios.buscarPorId(remetenteId).orElseThrow(UsuarioNaoEncontradoException::new);
        if (!remetente.isAprovado()) {
            throw new ContaNaoValidadaException();
        }

        Proposta proposta = Proposta.criar(anuncio.getId(), remetenteId, anuncio.getAutorId(),
                Preco.deOuNulo(comando.valor()), comando.mensagem());
        if (propostas.existePorAnuncioERemetente(anuncio.getId(), remetenteId)) {
            throw new PropostaDuplicadaException();
        }

        propostas.salvar(proposta);
        notificador.propostaRecebida(proposta, anuncio.getTitulo(), remetente.getNome());
        return detalhador.detalhar(proposta);
    }
}
