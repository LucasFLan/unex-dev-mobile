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
import com.unex.chamaAi.proposta.domain.repositories.FotoPropostaRepository;
import com.unex.chamaAi.proposta.domain.repositories.PropostaRepository;
import com.unex.chamaAi.proposta.domain.valueobjects.FotoProposta;
import com.unex.chamaAi.shared.domain.exceptions.DadoInvalidoException;
import com.unex.chamaAi.usuario.domain.entities.Usuario;
import com.unex.chamaAi.usuario.domain.exceptions.ContaNaoValidadaException;
import com.unex.chamaAi.usuario.domain.exceptions.UsuarioNaoEncontradoException;
import com.unex.chamaAi.usuario.domain.repositories.UsuarioRepository;

import java.util.List;
import java.util.UUID;

public class EnviarProposta {

    private final PropostaRepository propostas;
    private final AnuncioRepository anuncios;
    private final UsuarioRepository usuarios;
    private final NotificadorProposta notificador;
    private final FotoPropostaRepository fotos;
    private final DetalhadorProposta detalhador;

    public EnviarProposta(PropostaRepository propostas, AnuncioRepository anuncios, UsuarioRepository usuarios,
                          FotoPropostaRepository fotos, NotificadorProposta notificador) {
        this.propostas = propostas;
        this.anuncios = anuncios;
        this.usuarios = usuarios;
        this.fotos = fotos;
        this.notificador = notificador;
        this.detalhador = new DetalhadorProposta(anuncios, usuarios, fotos);
    }

    /**
     * Envia a proposta para o autor do anúncio e o notifica. Anúncio inativo de outra pessoa "não existe";
     * não dá para propor no próprio anúncio nem mandar duas propostas para o mesmo anúncio. As fotos (até
     * {@link FotoProposta#MAXIMO_POR_PROPOSTA}) são gravadas junto com a proposta.
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
        List<FotoProposta> anexos = validarFotos(comando.fotos());
        if (propostas.existePorAnuncioERemetente(anuncio.getId(), remetenteId)) {
            throw new PropostaDuplicadaException();
        }

        propostas.salvar(proposta);
        if (!anexos.isEmpty()) {
            fotos.gravar(proposta.getId(), anexos);
        }
        notificador.propostaRecebida(proposta, anuncio.getTitulo(), remetente.getNome());
        return detalhador.detalhar(proposta);
    }

    private static List<FotoProposta> validarFotos(List<byte[]> conteudos) {
        if (conteudos.size() > FotoProposta.MAXIMO_POR_PROPOSTA) {
            throw new DadoInvalidoException("Anexe no máximo " + FotoProposta.MAXIMO_POR_PROPOSTA + " fotos.");
        }
        return conteudos.stream().map(FotoProposta::de).toList();
    }
}
