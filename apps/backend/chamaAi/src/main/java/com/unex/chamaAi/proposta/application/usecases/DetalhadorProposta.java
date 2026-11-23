package com.unex.chamaAi.proposta.application.usecases;

import com.unex.chamaAi.anuncio.domain.entities.Anuncio;
import com.unex.chamaAi.anuncio.domain.repositories.AnuncioRepository;
import com.unex.chamaAi.proposta.application.dto.PropostaDetalhada;
import com.unex.chamaAi.proposta.application.dto.PropostaDetalhada.AnuncioResumo;
import com.unex.chamaAi.proposta.application.dto.PropostaDetalhada.EnderecoServico;
import com.unex.chamaAi.proposta.application.dto.PropostaDetalhada.ParticipanteResumo;
import com.unex.chamaAi.proposta.domain.entities.Avaliacao;
import com.unex.chamaAi.proposta.domain.entities.Proposta;
import com.unex.chamaAi.proposta.domain.repositories.AvaliacaoRepository;
import com.unex.chamaAi.proposta.domain.repositories.FotoPropostaRepository;
import com.unex.chamaAi.shared.domain.paginacao.Pagina;
import com.unex.chamaAi.usuario.domain.entities.Usuario;
import com.unex.chamaAi.usuario.domain.repositories.UsuarioRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/** Monta a saída das propostas com o anúncio, as pessoas envolvidas, as fotos e a avaliação. */
class DetalhadorProposta {

    private final AnuncioRepository anuncios;
    private final UsuarioRepository usuarios;
    private final FotoPropostaRepository fotos;
    /** Nulo onde não há avaliação possível (proposta acabou de ser enviada). */
    private final AvaliacaoRepository avaliacoes;

    DetalhadorProposta(AnuncioRepository anuncios, UsuarioRepository usuarios, FotoPropostaRepository fotos,
                       AvaliacaoRepository avaliacoes) {
        this.anuncios = anuncios;
        this.usuarios = usuarios;
        this.fotos = fotos;
        this.avaliacoes = avaliacoes;
    }

    PropostaDetalhada detalhar(Proposta proposta) {
        List<UUID> ids = List.of(proposta.getId());
        return detalhar(proposta, new HashMap<>(), new HashMap<>(), fotos.listarIds(ids), avaliacoesDe(ids));
    }

    /** Cada anúncio e cada usuário é buscado uma vez só por página; fotos e avaliações, numa consulta cada. */
    Pagina<PropostaDetalhada> detalhar(Pagina<Proposta> pagina) {
        Map<UUID, Optional<Anuncio>> anunciosPorId = new HashMap<>();
        Map<UUID, Optional<Usuario>> usuariosPorId = new HashMap<>();
        List<UUID> ids = pagina.itens().stream().map(Proposta::getId).toList();
        Map<UUID, List<UUID>> fotosPorProposta = ids.isEmpty() ? Map.of() : fotos.listarIds(ids);
        Map<UUID, Avaliacao> avaliacoesPorProposta = avaliacoesDe(ids);
        return pagina.mapear(proposta -> detalhar(proposta, anunciosPorId, usuariosPorId, fotosPorProposta,
                avaliacoesPorProposta));
    }

    private Map<UUID, Avaliacao> avaliacoesDe(List<UUID> propostaIds) {
        return avaliacoes == null || propostaIds.isEmpty() ? Map.of() : avaliacoes.buscarPorPropostas(propostaIds);
    }

    private PropostaDetalhada detalhar(Proposta proposta, Map<UUID, Optional<Anuncio>> anunciosPorId,
                                       Map<UUID, Optional<Usuario>> usuariosPorId,
                                       Map<UUID, List<UUID>> fotosPorProposta,
                                       Map<UUID, Avaliacao> avaliacoesPorProposta) {
        Optional<Anuncio> anuncio = anunciosPorId.computeIfAbsent(proposta.getAnuncioId(), anuncios::buscarPorId);
        AnuncioResumo resumo = anuncio
                .map(encontrado -> new AnuncioResumo(encontrado.getId(), encontrado.getTitulo(),
                        encontrado.getTipo().name()))
                .orElse(null);
        boolean contato = proposta.contatoLiberado();
        return PropostaDetalhada.de(proposta, resumo,
                participante(usuariosPorId.computeIfAbsent(proposta.getRemetenteId(), usuarios::buscarPorId), contato),
                participante(usuariosPorId.computeIfAbsent(proposta.getDestinatarioId(), usuarios::buscarPorId),
                        contato),
                fotosPorProposta.getOrDefault(proposta.getId(), List.of()),
                avaliacoesPorProposta.get(proposta.getId()),
                anuncio.map(DetalhadorProposta::endereco).orElse(null));
    }

    /** Endereço completo do anúncio (PropostaDetalhada só o expõe depois do aceite). */
    private static EnderecoServico endereco(Anuncio anuncio) {
        var localizacao = anuncio.getLocalizacao();
        return anuncio.getEndereco()
                .map(endereco -> new EnderecoServico(endereco.cep(), endereco.logradouro(), endereco.numero(),
                        endereco.complemento(), endereco.bairro(), localizacao.cidade(), localizacao.uf(),
                        endereco.coordenadas() == null ? null : endereco.coordenadas().latitude(),
                        endereco.coordenadas() == null ? null : endereco.coordenadas().longitude()))
                .orElse(new EnderecoServico(null, null, null, null, null, localizacao.cidade(), localizacao.uf(),
                        null, null));
    }

    private static ParticipanteResumo participante(Optional<Usuario> usuario, boolean contatoLiberado) {
        return usuario.map(encontrado -> new ParticipanteResumo(encontrado.getId(), encontrado.getNome(),
                        contatoLiberado ? encontrado.getTelefone() : null))
                .orElse(null);
    }
}
