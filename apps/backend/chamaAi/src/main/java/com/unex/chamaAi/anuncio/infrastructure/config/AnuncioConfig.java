package com.unex.chamaAi.anuncio.infrastructure.config;

import com.unex.chamaAi.anuncio.application.usecases.AlterarAtivacaoAnuncio;
import com.unex.chamaAi.anuncio.application.usecases.BuscarAnuncio;
import com.unex.chamaAi.anuncio.application.usecases.CriarAnuncio;
import com.unex.chamaAi.anuncio.application.usecases.EditarAnuncio;
import com.unex.chamaAi.anuncio.application.usecases.ExcluirAnuncio;
import com.unex.chamaAi.anuncio.application.usecases.GerenciarFotosAnuncio;
import com.unex.chamaAi.anuncio.application.usecases.ListarAnuncios;
import com.unex.chamaAi.anuncio.application.usecases.ListarCategorias;
import com.unex.chamaAi.anuncio.application.usecases.ListarMeusAnuncios;
import com.unex.chamaAi.anuncio.domain.repositories.AnuncioRepository;
import com.unex.chamaAi.anuncio.domain.repositories.CategoriaRepository;
import com.unex.chamaAi.anuncio.domain.repositories.FotoAnuncioRepository;
import com.unex.chamaAi.usuario.domain.repositories.UsuarioRepository;
import com.unex.chamaAi.anuncio.application.ports.AutoresAnuncio;
import com.unex.chamaAi.anuncio.application.ports.ConsultaCep;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AnuncioConfig {

    @Bean
    public CriarAnuncio criarAnuncio(AnuncioRepository anuncios, CategoriaRepository categorias,
                                     UsuarioRepository usuarios, ConsultaCep consultaCep, AutoresAnuncio autores,
            FotoAnuncioRepository fotos) {
        return new CriarAnuncio(anuncios, categorias, usuarios, consultaCep, autores, fotos);
    }

    @Bean
    public BuscarAnuncio buscarAnuncio(AnuncioRepository anuncios, CategoriaRepository categorias,
                                       AutoresAnuncio autores,
            FotoAnuncioRepository fotos) {
        return new BuscarAnuncio(anuncios, categorias, autores, fotos);
    }

    @Bean
    public ListarAnuncios listarAnuncios(AnuncioRepository anuncios, CategoriaRepository categorias,
                                         AutoresAnuncio autores,
            FotoAnuncioRepository fotos) {
        return new ListarAnuncios(anuncios, categorias, autores, fotos);
    }

    @Bean
    public ListarMeusAnuncios listarMeusAnuncios(AnuncioRepository anuncios, CategoriaRepository categorias,
                                                 AutoresAnuncio autores,
            FotoAnuncioRepository fotos) {
        return new ListarMeusAnuncios(anuncios, categorias, autores, fotos);
    }

    @Bean
    public EditarAnuncio editarAnuncio(AnuncioRepository anuncios, CategoriaRepository categorias,
                                       ConsultaCep consultaCep, AutoresAnuncio autores,
            FotoAnuncioRepository fotos) {
        return new EditarAnuncio(anuncios, categorias, consultaCep, autores, fotos);
    }

    @Bean
    public ExcluirAnuncio excluirAnuncio(AnuncioRepository anuncios,
            FotoAnuncioRepository fotos) {
        return new ExcluirAnuncio(anuncios, fotos);
    }

    @Bean
    public AlterarAtivacaoAnuncio alterarAtivacaoAnuncio(AnuncioRepository anuncios, CategoriaRepository categorias,
                                                         AutoresAnuncio autores,
            FotoAnuncioRepository fotos) {
        return new AlterarAtivacaoAnuncio(anuncios, categorias, autores, fotos);
    }

    @Bean
    public GerenciarFotosAnuncio gerenciarFotosAnuncio(AnuncioRepository anuncios, CategoriaRepository categorias,
                                                       AutoresAnuncio autores, FotoAnuncioRepository fotos) {
        return new GerenciarFotosAnuncio(anuncios, categorias, autores, fotos);
    }

    @Bean
    public ListarCategorias listarCategorias(CategoriaRepository categorias) {
        return new ListarCategorias(categorias);
    }
}
