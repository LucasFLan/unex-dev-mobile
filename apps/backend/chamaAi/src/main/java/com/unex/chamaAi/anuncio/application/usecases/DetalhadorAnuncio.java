package com.unex.chamaAi.anuncio.application.usecases;

import com.unex.chamaAi.anuncio.application.dto.AnuncioDetalhado;
import com.unex.chamaAi.anuncio.application.dto.CategoriaResumo;
import com.unex.chamaAi.anuncio.application.dto.FotoResumo;
import com.unex.chamaAi.anuncio.domain.entities.Anuncio;
import com.unex.chamaAi.anuncio.domain.entities.Categoria;
import com.unex.chamaAi.anuncio.domain.repositories.CategoriaRepository;
import com.unex.chamaAi.anuncio.domain.repositories.FotoAnuncioRepository;
import com.unex.chamaAi.anuncio.domain.valueobjects.FotoAnuncio;
import com.unex.chamaAi.shared.domain.paginacao.Pagina;
import com.unex.chamaAi.usuario.application.dto.FotoPerfilUrl;
import com.unex.chamaAi.usuario.domain.repositories.FotoPerfilRepository;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Monta a saída dos anúncios junto com a categoria, as fotos e a foto de perfil do autor.
 * Sem os repositórios de fotos (testes de unidade), os anúncios saem sem fotos.
 */
class DetalhadorAnuncio {

    private final CategoriaRepository categorias;
    private final FotoAnuncioRepository fotos;
    private final FotoPerfilRepository fotosPerfil;

    DetalhadorAnuncio(CategoriaRepository categorias) {
        this(categorias, null, null);
    }

    DetalhadorAnuncio(CategoriaRepository categorias, FotoAnuncioRepository fotos, FotoPerfilRepository fotosPerfil) {
        this.categorias = categorias;
        this.fotos = fotos;
        this.fotosPerfil = fotosPerfil;
    }

    AnuncioDetalhado detalhar(Anuncio anuncio) {
        CategoriaResumo categoria = categorias.buscarPorId(anuncio.getCategoriaId())
                .map(CategoriaResumo::de)
                .orElse(null);
        List<FotoResumo> doAnuncio = fotos == null ? List.of()
                : fotos.listar(anuncio.getId()).stream().map(FotoResumo::de).toList();
        String fotoAutor = fotosPerfil == null ? null
                : fotosPerfil.versao(anuncio.getAutorId()).map(v -> FotoPerfilUrl.de(anuncio.getAutorId(), v))
                .orElse(null);
        return AnuncioDetalhado.de(anuncio, categoria, doAnuncio, fotoAutor);
    }

    /** Categorias, fotos e autores são carregados de uma vez para a página toda, não um por anúncio. */
    Pagina<AnuncioDetalhado> detalhar(Pagina<Anuncio> pagina) {
        Map<UUID, CategoriaResumo> porId = categorias.listarTodas().stream()
                .collect(Collectors.toMap(Categoria::id, CategoriaResumo::de));
        Set<UUID> ids = pagina.itens().stream().map(Anuncio::getId).collect(Collectors.toSet());
        Set<UUID> autores = pagina.itens().stream().map(Anuncio::getAutorId).collect(Collectors.toSet());
        Map<UUID, List<FotoAnuncio>> fotosPorAnuncio = fotos == null ? Map.of() : fotos.listar(ids);
        Map<UUID, Instant> versoesAutores = fotosPerfil == null ? Map.of() : fotosPerfil.versoes(autores);

        return pagina.mapear(anuncio -> AnuncioDetalhado.de(anuncio, porId.get(anuncio.getCategoriaId()),
                fotosPorAnuncio.getOrDefault(anuncio.getId(), List.of()).stream().map(FotoResumo::de).toList(),
                FotoPerfilUrl.de(anuncio.getAutorId(), versoesAutores.get(anuncio.getAutorId()))));
    }
}
