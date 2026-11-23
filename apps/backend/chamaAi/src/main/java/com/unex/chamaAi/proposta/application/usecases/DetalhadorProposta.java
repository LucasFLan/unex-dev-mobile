package com.unex.chamaAi.proposta.application.usecases;

import com.unex.chamaAi.anuncio.domain.entities.Anuncio;
import com.unex.chamaAi.anuncio.domain.repositories.AnuncioRepository;
import com.unex.chamaAi.proposta.application.dto.PropostaDetalhada;
import com.unex.chamaAi.proposta.application.dto.PropostaDetalhada.AnuncioResumo;
import com.unex.chamaAi.proposta.application.dto.PropostaDetalhada.ParticipanteResumo;
import com.unex.chamaAi.proposta.domain.entities.Proposta;
import com.unex.chamaAi.proposta.domain.repositories.FotoPropostaRepository;
import com.unex.chamaAi.shared.domain.paginacao.Pagina;
import com.unex.chamaAi.usuario.domain.entities.Usuario;
import com.unex.chamaAi.usuario.domain.repositories.UsuarioRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/** Monta a saída das propostas com o anúncio, as pessoas envolvidas e as fotos anexadas. */
class DetalhadorProposta {

    private final AnuncioRepository anuncios;
    private final UsuarioRepository usuarios;
    private final FotoPropostaRepository fotos;

    DetalhadorProposta(AnuncioRepository anuncios, UsuarioRepository usuarios, FotoPropostaRepository fotos) {
        this.anuncios = anuncios;
        this.usuarios = usuarios;
        this.fotos = fotos;
    }

    PropostaDetalhada detalhar(Proposta proposta) {
        return detalhar(proposta, new HashMap<>(), new HashMap<>(), fotos.listarIds(List.of(proposta.getId())));
    }

    /** Cada anúncio e cada usuário é buscado uma vez só por página; as fotos, numa consulta só. */
    Pagina<PropostaDetalhada> detalhar(Pagina<Proposta> pagina) {
        Map<UUID, Optional<Anuncio>> anunciosPorId = new HashMap<>();
        Map<UUID, Optional<Usuario>> usuariosPorId = new HashMap<>();
        Map<UUID, List<UUID>> fotosPorProposta = pagina.itens().isEmpty() ? Map.of()
                : fotos.listarIds(pagina.itens().stream().map(Proposta::getId).toList());
        return pagina.mapear(proposta -> detalhar(proposta, anunciosPorId, usuariosPorId, fotosPorProposta));
    }

    private PropostaDetalhada detalhar(Proposta proposta, Map<UUID, Optional<Anuncio>> anunciosPorId,
                                       Map<UUID, Optional<Usuario>> usuariosPorId,
                                       Map<UUID, List<UUID>> fotosPorProposta) {
        AnuncioResumo anuncio = anunciosPorId.computeIfAbsent(proposta.getAnuncioId(), anuncios::buscarPorId)
                .map(encontrado -> new AnuncioResumo(encontrado.getId(), encontrado.getTitulo(),
                        encontrado.getTipo().name()))
                .orElse(null);
        boolean contato = proposta.contatoLiberado();
        return PropostaDetalhada.de(proposta, anuncio,
                participante(usuariosPorId.computeIfAbsent(proposta.getRemetenteId(), usuarios::buscarPorId), contato),
                participante(usuariosPorId.computeIfAbsent(proposta.getDestinatarioId(), usuarios::buscarPorId),
                        contato),
                fotosPorProposta.getOrDefault(proposta.getId(), List.of()));
    }

    private static ParticipanteResumo participante(Optional<Usuario> usuario, boolean contatoLiberado) {
        return usuario.map(encontrado -> new ParticipanteResumo(encontrado.getId(), encontrado.getNome(),
                        contatoLiberado ? encontrado.getTelefone() : null))
                .orElse(null);
    }
}
