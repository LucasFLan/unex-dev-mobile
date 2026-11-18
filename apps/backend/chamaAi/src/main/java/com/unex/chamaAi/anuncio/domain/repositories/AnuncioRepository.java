package com.unex.chamaAi.anuncio.domain.repositories;

import com.unex.chamaAi.anuncio.domain.entities.Anuncio;
import com.unex.chamaAi.shared.domain.paginacao.Pagina;
import com.unex.chamaAi.shared.domain.paginacao.Paginacao;

import java.util.Optional;
import java.util.UUID;

public interface AnuncioRepository {

    void salvar(Anuncio anuncio);

    Optional<Anuncio> buscarPorId(UUID id);

    /** Somente anúncios ativos, do mais recente para o mais antigo. */
    Pagina<Anuncio> buscarAtivos(FiltroAnuncios filtro, Paginacao paginacao);

    /** Todos os anúncios do autor (ativos e inativos), do mais recente para o mais antigo. */
    Pagina<Anuncio> buscarPorAutor(UUID autorId, Paginacao paginacao);

    void excluir(UUID id);
}
