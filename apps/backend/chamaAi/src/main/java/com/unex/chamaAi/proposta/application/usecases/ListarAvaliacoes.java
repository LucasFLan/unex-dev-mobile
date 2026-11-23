package com.unex.chamaAi.proposta.application.usecases;

import com.unex.chamaAi.proposta.application.dto.AvaliacaoDetalhada;
import com.unex.chamaAi.proposta.application.dto.AvaliacoesDoUsuario;
import com.unex.chamaAi.proposta.domain.entities.Avaliacao;
import com.unex.chamaAi.proposta.domain.repositories.AvaliacaoRepository;
import com.unex.chamaAi.shared.domain.paginacao.Pagina;
import com.unex.chamaAi.shared.domain.paginacao.Paginacao;
import com.unex.chamaAi.usuario.domain.entities.Usuario;
import com.unex.chamaAi.usuario.domain.exceptions.UsuarioNaoEncontradoException;
import com.unex.chamaAi.usuario.domain.repositories.UsuarioRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/** Avaliações recebidas por um profissional, com a média, para o perfil público. */
public class ListarAvaliacoes {

    private final AvaliacaoRepository avaliacoes;
    private final UsuarioRepository usuarios;

    public ListarAvaliacoes(AvaliacaoRepository avaliacoes, UsuarioRepository usuarios) {
        this.avaliacoes = avaliacoes;
        this.usuarios = usuarios;
    }

    public AvaliacoesDoUsuario doUsuario(UUID usuarioId, Integer pagina, Integer tamanho) {
        Usuario usuario = usuarios.buscarPorId(usuarioId).orElseThrow(UsuarioNaoEncontradoException::new);
        Pagina<Avaliacao> encontradas = avaliacoes.buscarPorAvaliado(usuarioId, Paginacao.de(pagina, tamanho));

        // Cada autor é buscado uma vez só por página.
        Map<UUID, Optional<Usuario>> autores = new HashMap<>();
        Pagina<AvaliacaoDetalhada> itens = encontradas.mapear(avaliacao -> AvaliacaoDetalhada.de(avaliacao,
                avaliacao.getAvaliadorId()
                        .flatMap(id -> autores.computeIfAbsent(id, usuarios::buscarPorId))
                        .orElse(null)));
        return new AvaliacoesDoUsuario(usuario.getId(), usuario.getNome(), usuario.getFotoAtualizadaEm(),
                avaliacoes.resumo(usuarioId), itens);
    }
}
