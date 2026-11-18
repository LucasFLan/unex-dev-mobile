package com.unex.chamaAi.anuncio.application.usecases;

import com.unex.chamaAi.anuncio.application.dto.AnuncioDetalhado;
import com.unex.chamaAi.anuncio.application.dto.CategoriaResumo;
import com.unex.chamaAi.anuncio.domain.entities.Anuncio;
import com.unex.chamaAi.anuncio.domain.entities.Categoria;
import com.unex.chamaAi.anuncio.domain.repositories.CategoriaRepository;
import com.unex.chamaAi.shared.domain.paginacao.Pagina;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/** Monta a saída dos anúncios junto com os dados da categoria. */
class DetalhadorAnuncio {

    private final CategoriaRepository categorias;

    DetalhadorAnuncio(CategoriaRepository categorias) {
        this.categorias = categorias;
    }

    AnuncioDetalhado detalhar(Anuncio anuncio) {
        CategoriaResumo categoria = categorias.buscarPorId(anuncio.getCategoriaId())
                .map(CategoriaResumo::de)
                .orElse(null);
        return AnuncioDetalhado.de(anuncio, categoria);
    }

    /** As categorias são poucas: carrega todas de uma vez em vez de uma consulta por anúncio. */
    Pagina<AnuncioDetalhado> detalhar(Pagina<Anuncio> pagina) {
        Map<UUID, CategoriaResumo> porId = categorias.listarTodas().stream()
                .collect(Collectors.toMap(Categoria::id, CategoriaResumo::de));
        return pagina.mapear(anuncio -> AnuncioDetalhado.de(anuncio, porId.get(anuncio.getCategoriaId())));
    }
}
