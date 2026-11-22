package com.unex.chamaAi.anuncio.infrastructure.config;

import com.unex.chamaAi.anuncio.application.usecases.AlterarAtivacaoAnuncio;
import com.unex.chamaAi.anuncio.application.usecases.BuscarAnuncio;
import com.unex.chamaAi.anuncio.application.usecases.CriarAnuncio;
import com.unex.chamaAi.anuncio.application.usecases.EditarAnuncio;
import com.unex.chamaAi.anuncio.application.usecases.ExcluirAnuncio;
import com.unex.chamaAi.anuncio.application.usecases.ListarAnuncios;
import com.unex.chamaAi.anuncio.application.usecases.ListarCategorias;
import com.unex.chamaAi.anuncio.application.usecases.ListarMeusAnuncios;
import com.unex.chamaAi.anuncio.domain.repositories.AnuncioRepository;
import com.unex.chamaAi.anuncio.domain.repositories.CategoriaRepository;
import com.unex.chamaAi.usuario.domain.repositories.UsuarioRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AnuncioConfig {

    @Bean
    public CriarAnuncio criarAnuncio(AnuncioRepository anuncios, CategoriaRepository categorias,
                                     UsuarioRepository usuarios) {
        return new CriarAnuncio(anuncios, categorias, usuarios);
    }

    @Bean
    public BuscarAnuncio buscarAnuncio(AnuncioRepository anuncios, CategoriaRepository categorias) {
        return new BuscarAnuncio(anuncios, categorias);
    }

    @Bean
    public ListarAnuncios listarAnuncios(AnuncioRepository anuncios, CategoriaRepository categorias) {
        return new ListarAnuncios(anuncios, categorias);
    }

    @Bean
    public ListarMeusAnuncios listarMeusAnuncios(AnuncioRepository anuncios, CategoriaRepository categorias) {
        return new ListarMeusAnuncios(anuncios, categorias);
    }

    @Bean
    public EditarAnuncio editarAnuncio(AnuncioRepository anuncios, CategoriaRepository categorias) {
        return new EditarAnuncio(anuncios, categorias);
    }

    @Bean
    public ExcluirAnuncio excluirAnuncio(AnuncioRepository anuncios) {
        return new ExcluirAnuncio(anuncios);
    }

    @Bean
    public AlterarAtivacaoAnuncio alterarAtivacaoAnuncio(AnuncioRepository anuncios, CategoriaRepository categorias) {
        return new AlterarAtivacaoAnuncio(anuncios, categorias);
    }

    @Bean
    public ListarCategorias listarCategorias(CategoriaRepository categorias) {
        return new ListarCategorias(categorias);
    }
}
