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
import com.unex.chamaAi.usuario.domain.repositories.FotoPerfilRepository;
import com.unex.chamaAi.usuario.domain.repositories.UsuarioRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class AnuncioConfig {

    @Bean
    public CriarAnuncio criarAnuncio(AnuncioRepository anuncios, CategoriaRepository categorias,
                                     UsuarioRepository usuarios, FotoAnuncioRepository fotos,
                                     FotoPerfilRepository fotosPerfil) {
        return new CriarAnuncio(anuncios, categorias, usuarios, fotos, fotosPerfil);
    }

    @Bean
    public BuscarAnuncio buscarAnuncio(AnuncioRepository anuncios, CategoriaRepository categorias,
            FotoAnuncioRepository fotos, FotoPerfilRepository fotosPerfil) {
        return new BuscarAnuncio(anuncios, categorias, fotos, fotosPerfil);
    }

    @Bean
    public ListarAnuncios listarAnuncios(AnuncioRepository anuncios, CategoriaRepository categorias,
            FotoAnuncioRepository fotos, FotoPerfilRepository fotosPerfil) {
        return new ListarAnuncios(anuncios, categorias, fotos, fotosPerfil);
    }

    @Bean
    public ListarMeusAnuncios listarMeusAnuncios(AnuncioRepository anuncios, CategoriaRepository categorias,
            FotoAnuncioRepository fotos, FotoPerfilRepository fotosPerfil) {
        return new ListarMeusAnuncios(anuncios, categorias, fotos, fotosPerfil);
    }

    @Bean
    public EditarAnuncio editarAnuncio(AnuncioRepository anuncios, CategoriaRepository categorias,
            FotoAnuncioRepository fotos, FotoPerfilRepository fotosPerfil) {
        return new EditarAnuncio(anuncios, categorias, fotos, fotosPerfil);
    }

    @Bean
    public ExcluirAnuncio excluirAnuncio(AnuncioRepository anuncios, FotoAnuncioRepository fotos) {
        return new ExcluirAnuncio(anuncios, fotos);
    }

    @Bean
    public AlterarAtivacaoAnuncio alterarAtivacaoAnuncio(AnuncioRepository anuncios, CategoriaRepository categorias,
            FotoAnuncioRepository fotos, FotoPerfilRepository fotosPerfil) {
        return new AlterarAtivacaoAnuncio(anuncios, categorias, fotos, fotosPerfil);
    }

    @Bean
    public GerenciarFotosAnuncio gerenciarFotosAnuncio(AnuncioRepository anuncios, CategoriaRepository categorias,
            FotoAnuncioRepository fotos, FotoPerfilRepository fotosPerfil, Clock relogio) {
        return new GerenciarFotosAnuncio(anuncios, categorias, fotos, fotosPerfil, relogio);
    }

    @Bean
    public ListarCategorias listarCategorias(CategoriaRepository categorias) {
        return new ListarCategorias(categorias);
    }
}
