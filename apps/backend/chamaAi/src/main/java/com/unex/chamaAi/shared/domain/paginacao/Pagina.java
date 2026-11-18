package com.unex.chamaAi.shared.domain.paginacao;

import java.util.List;
import java.util.function.Function;

public record Pagina<T>(List<T> itens, int pagina, int tamanho, long totalItens, int totalPaginas) {

    public Pagina {
        itens = List.copyOf(itens);
    }

    public <R> Pagina<R> mapear(Function<? super T, ? extends R> conversor) {
        return new Pagina<>(itens.stream().<R>map(conversor).toList(), pagina, tamanho, totalItens, totalPaginas);
    }
}
