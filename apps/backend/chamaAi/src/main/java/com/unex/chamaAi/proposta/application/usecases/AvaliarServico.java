package com.unex.chamaAi.proposta.application.usecases;

import com.unex.chamaAi.anuncio.domain.entities.Anuncio;
import com.unex.chamaAi.anuncio.domain.entities.Categoria;
import com.unex.chamaAi.anuncio.domain.repositories.AnuncioRepository;
import com.unex.chamaAi.anuncio.domain.repositories.CategoriaRepository;
import com.unex.chamaAi.proposta.application.dto.AvaliacaoDetalhada;
import com.unex.chamaAi.proposta.application.ports.NotificadorProposta;
import com.unex.chamaAi.proposta.domain.entities.Avaliacao;
import com.unex.chamaAi.proposta.domain.entities.Proposta;
import com.unex.chamaAi.proposta.domain.exceptions.AcessoNegadoPropostaException;
import com.unex.chamaAi.proposta.domain.exceptions.AvaliacaoDuplicadaException;
import com.unex.chamaAi.proposta.domain.exceptions.AvaliacaoIndisponivelException;
import com.unex.chamaAi.proposta.domain.exceptions.PropostaNaoEncontradaException;
import com.unex.chamaAi.proposta.domain.repositories.AvaliacaoRepository;
import com.unex.chamaAi.proposta.domain.repositories.PropostaRepository;
import com.unex.chamaAi.usuario.domain.entities.Usuario;
import com.unex.chamaAi.usuario.domain.repositories.UsuarioRepository;

import java.util.UUID;

/** Quem contratou avalia o profissional (1 a 5 estrelas e comentário opcional), uma vez por serviço concluído. */
public class AvaliarServico {

    private final PropostaRepository propostas;
    private final AnuncioRepository anuncios;
    private final CategoriaRepository categorias;
    private final UsuarioRepository usuarios;
    private final AvaliacaoRepository avaliacoes;
    private final NotificadorProposta notificador;

    public AvaliarServico(PropostaRepository propostas, AnuncioRepository anuncios, CategoriaRepository categorias,
                          UsuarioRepository usuarios, AvaliacaoRepository avaliacoes,
                          NotificadorProposta notificador) {
        this.propostas = propostas;
        this.anuncios = anuncios;
        this.categorias = categorias;
        this.usuarios = usuarios;
        this.avaliacoes = avaliacoes;
        this.notificador = notificador;
    }

    public AvaliacaoDetalhada executar(UUID propostaId, UUID usuarioId, Integer nota, String comentario) {
        // Para quem não está na proposta, ela "não existe".
        Proposta proposta = propostas.buscarPorId(propostaId)
                .filter(encontrada -> encontrada.foiEnviadaPor(usuarioId) || encontrada.foiRecebidaPor(usuarioId))
                .orElseThrow(PropostaNaoEncontradaException::new);
        Anuncio anuncio = anuncios.buscarPorId(proposta.getAnuncioId()).orElseThrow(PropostaNaoEncontradaException::new);
        if (!proposta.clienteId(anuncio.getTipo()).equals(usuarioId)) {
            throw new AcessoNegadoPropostaException("Só quem contratou pode avaliar o serviço.");
        }
        if (!proposta.isConcluida()) {
            throw new AvaliacaoIndisponivelException();
        }
        if (avaliacoes.existePorProposta(propostaId)) {
            throw new AvaliacaoDuplicadaException();
        }

        String categoria = categorias.buscarPorId(anuncio.getCategoriaId()).map(Categoria::nome).orElse(null);
        UUID profissionalId = proposta.profissionalId(anuncio.getTipo());
        Avaliacao avaliacao = Avaliacao.criar(propostaId, usuarioId, profissionalId, nota, comentario,
                anuncio.getTitulo(), categoria);
        avaliacoes.salvar(avaliacao);

        Usuario cliente = usuarios.buscarPorId(usuarioId).orElse(null);
        String nomeCliente = cliente == null ? "Um cliente" : cliente.getNome();
        notificador.avaliacaoRecebida(proposta, profissionalId, anuncio.getTitulo(), nomeCliente, avaliacao.getNota());
        return AvaliacaoDetalhada.de(avaliacao, cliente);
    }
}
