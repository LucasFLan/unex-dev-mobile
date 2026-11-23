package com.unex.chamaAi.anuncio.application.usecases;

import com.unex.chamaAi.anuncio.application.dto.AnuncioDetalhado;
import com.unex.chamaAi.anuncio.application.dto.CategoriaResumo;
import com.unex.chamaAi.anuncio.application.ports.AutoresAnuncio;
import com.unex.chamaAi.anuncio.application.ports.AutoresAnuncio.AutorAnuncio;
import com.unex.chamaAi.anuncio.domain.entities.Anuncio;
import com.unex.chamaAi.anuncio.domain.entities.Categoria;
import com.unex.chamaAi.anuncio.domain.repositories.CategoriaRepository;
import com.unex.chamaAi.anuncio.domain.repositories.FotoAnuncioRepository;
import com.unex.chamaAi.shared.domain.paginacao.Pagina;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Monta a saída dos anúncios com a categoria, o autor (nome, foto e reputação) e os ids das fotos.
 * Sem o repositório de fotos (testes de unidade), os anúncios saem sem fotos.
 */
class DetalhadorAnuncio {

    private final CategoriaRepository categorias;
    private final AutoresAnuncio autores;
    private final FotoAnuncioRepository fotos;

    DetalhadorAnuncio(CategoriaRepository categorias, AutoresAnuncio autores) {
        this(categorias, autores, null);
    }

    DetalhadorAnuncio(CategoriaRepository categorias, AutoresAnuncio autores, FotoAnuncioRepository fotos) {
        this.categorias = categorias;
        this.autores = autores;
        this.fotos = fotos;
    }

    private Map<UUID, List<UUID>> fotosDe(List<UUID> anuncioIds) {
        return fotos == null || anuncioIds.isEmpty() ? Map.of() : fotos.listarIds(anuncioIds);
    }

    AnuncioDetalhado detalhar(Anuncio anuncio) {
        CategoriaResumo categoria = categorias.buscarPorId(anuncio.getCategoriaId())
                .map(CategoriaResumo::de)
                .orElse(null);
        AutorAnuncio autor = autores.buscar(List.of(anuncio.getAutorId())).get(anuncio.getAutorId());
        List<UUID> doAnuncio = fotosDe(List.of(anuncio.getId())).getOrDefault(anuncio.getId(), List.of());
        return AnuncioDetalhado.de(anuncio, categoria, autor, doAnuncio);
    }

    /** As categorias são poucas: carrega todas de uma vez. Os autores da página vêm numa consulta só. */
    Pagina<AnuncioDetalhado> detalhar(Pagina<Anuncio> pagina) {
        Map<UUID, CategoriaResumo> porId = categorias.listarTodas().stream()
                .collect(Collectors.toMap(Categoria::id, CategoriaResumo::de));
        List<UUID> autorIds = pagina.itens().stream().map(Anuncio::getAutorId).distinct().toList();
        Map<UUID, AutorAnuncio> autoresPorId = autorIds.isEmpty() ? Map.of() : autores.buscar(autorIds);
        Map<UUID, List<UUID>> fotosPorAnuncio = fotosDe(pagina.itens().stream().map(Anuncio::getId).toList());
        return pagina.mapear(anuncio -> AnuncioDetalhado.de(anuncio, porId.get(anuncio.getCategoriaId()),
                autoresPorId.get(anuncio.getAutorId()), fotosPorAnuncio.getOrDefault(anuncio.getId(), List.of())));
    }
}
