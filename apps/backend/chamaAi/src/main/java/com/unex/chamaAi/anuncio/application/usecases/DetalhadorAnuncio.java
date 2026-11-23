package com.unex.chamaAi.anuncio.application.usecases;

import com.unex.chamaAi.anuncio.application.dto.AnuncioDetalhado;
import com.unex.chamaAi.anuncio.application.dto.CategoriaResumo;
import com.unex.chamaAi.anuncio.application.ports.AutoresAnuncio;
import com.unex.chamaAi.anuncio.application.ports.AutoresAnuncio.AutorAnuncio;
import com.unex.chamaAi.anuncio.domain.entities.Anuncio;
import com.unex.chamaAi.anuncio.domain.entities.Categoria;
import com.unex.chamaAi.anuncio.domain.repositories.CategoriaRepository;
import com.unex.chamaAi.shared.domain.paginacao.Pagina;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/** Monta a saída dos anúncios com a categoria e o autor (nome, foto e reputação). */
class DetalhadorAnuncio {

    private final CategoriaRepository categorias;
    private final AutoresAnuncio autores;

    DetalhadorAnuncio(CategoriaRepository categorias, AutoresAnuncio autores) {
        this.categorias = categorias;
        this.autores = autores;
    }

    AnuncioDetalhado detalhar(Anuncio anuncio) {
        CategoriaResumo categoria = categorias.buscarPorId(anuncio.getCategoriaId())
                .map(CategoriaResumo::de)
                .orElse(null);
        AutorAnuncio autor = autores.buscar(List.of(anuncio.getAutorId())).get(anuncio.getAutorId());
        return AnuncioDetalhado.de(anuncio, categoria, autor);
    }

    /** As categorias são poucas: carrega todas de uma vez. Os autores da página vêm numa consulta só. */
    Pagina<AnuncioDetalhado> detalhar(Pagina<Anuncio> pagina) {
        Map<UUID, CategoriaResumo> porId = categorias.listarTodas().stream()
                .collect(Collectors.toMap(Categoria::id, CategoriaResumo::de));
        List<UUID> autorIds = pagina.itens().stream().map(Anuncio::getAutorId).distinct().toList();
        Map<UUID, AutorAnuncio> autoresPorId = autorIds.isEmpty() ? Map.of() : autores.buscar(autorIds);
        return pagina.mapear(anuncio -> AnuncioDetalhado.de(anuncio, porId.get(anuncio.getCategoriaId()),
                autoresPorId.get(anuncio.getAutorId())));
    }
}
