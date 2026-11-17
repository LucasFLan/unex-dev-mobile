package com.unex.chamaAi.anuncio.application.usecases;

import com.unex.chamaAi.anuncio.domain.entities.Anuncio;
import com.unex.chamaAi.anuncio.domain.entities.Categoria;
import com.unex.chamaAi.anuncio.domain.repositories.AnuncioRepository;
import com.unex.chamaAi.anuncio.domain.repositories.CategoriaRepository;
import com.unex.chamaAi.anuncio.domain.repositories.FiltroAnuncios;
import com.unex.chamaAi.shared.domain.paginacao.Pagina;
import com.unex.chamaAi.shared.domain.paginacao.Paginacao;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

final class RepositoriosEmMemoria {

    private RepositoriosEmMemoria() {
    }

    static class Anuncios implements AnuncioRepository {

        final Map<UUID, Anuncio> dados = new LinkedHashMap<>();

        @Override
        public void salvar(Anuncio anuncio) {
            dados.put(anuncio.getId(), anuncio);
        }

        @Override
        public Optional<Anuncio> buscarPorId(UUID id) {
            return Optional.ofNullable(dados.get(id));
        }

        @Override
        public Pagina<Anuncio> buscarAtivos(FiltroAnuncios filtro, Paginacao paginacao) {
            Predicate<Anuncio> condicao = Anuncio::isAtivo;
            if (filtro.tipo() != null) condicao = condicao.and(a -> a.getTipo() == filtro.tipo());
            if (filtro.categoriaId() != null) condicao = condicao.and(a -> a.getCategoriaId().equals(filtro.categoriaId()));
            if (filtro.uf() != null) condicao = condicao.and(a -> a.getLocalizacao().uf().equals(filtro.uf()));
            if (filtro.cidade() != null) condicao = condicao.and(a -> a.getLocalizacao().cidade().equalsIgnoreCase(filtro.cidade()));
            return paginar(dados.values().stream().filter(condicao).toList(), paginacao);
        }

        @Override
        public Pagina<Anuncio> buscarPorAutor(UUID autorId, Paginacao paginacao) {
            return paginar(dados.values().stream().filter(a -> a.pertenceA(autorId)).toList(), paginacao);
        }

        @Override
        public void excluir(UUID id) {
            dados.remove(id);
        }

        private static Pagina<Anuncio> paginar(List<Anuncio> todos, Paginacao paginacao) {
            List<Anuncio> ordenados = todos.stream()
                    .sorted(Comparator.comparing(Anuncio::getCriadoEm).reversed()).toList();
            int inicio = Math.min(paginacao.pagina() * paginacao.tamanho(), ordenados.size());
            int fim = Math.min(inicio + paginacao.tamanho(), ordenados.size());
            int totalPaginas = (int) Math.ceil((double) ordenados.size() / paginacao.tamanho());
            return new Pagina<>(ordenados.subList(inicio, fim), paginacao.pagina(), paginacao.tamanho(),
                    ordenados.size(), totalPaginas);
        }
    }

    static class Categorias implements CategoriaRepository {

        final Map<UUID, Categoria> dados = new LinkedHashMap<>();

        Categoria adicionar(String nome) {
            Categoria categoria = new Categoria(UUID.randomUUID(), nome.toLowerCase(), nome);
            dados.put(categoria.id(), categoria);
            return categoria;
        }

        @Override
        public List<Categoria> listarTodas() {
            return dados.values().stream().sorted(Comparator.comparing(Categoria::nome)).toList();
        }

        @Override
        public Optional<Categoria> buscarPorId(UUID id) {
            return Optional.ofNullable(dados.get(id));
        }
    }
}
