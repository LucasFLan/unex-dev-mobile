package com.unex.chamaAi.anuncio.application.usecases;

import com.unex.chamaAi.usuario.domain.entities.Usuario;
import com.unex.chamaAi.usuario.domain.exceptions.ContaNaoValidadaException;
import com.unex.chamaAi.usuario.domain.repositories.UsuarioRepository;
import com.unex.chamaAi.anuncio.application.dto.AnuncioDetalhado;
import com.unex.chamaAi.anuncio.application.dto.DadosAnuncioCommand;
import com.unex.chamaAi.anuncio.domain.entities.Anuncio;
import com.unex.chamaAi.anuncio.domain.repositories.AnuncioRepository;
import com.unex.chamaAi.anuncio.domain.repositories.CategoriaRepository;
import com.unex.chamaAi.anuncio.domain.repositories.FotoAnuncioRepository;
import com.unex.chamaAi.usuario.domain.repositories.FotoPerfilRepository;

import java.util.UUID;

public class CriarAnuncio {

    private final AnuncioRepository anuncios;
    private final UsuarioRepository usuarios;
    private final ValidadorDadosAnuncio validador;
    private final DetalhadorAnuncio detalhador;

    public CriarAnuncio(AnuncioRepository anuncios, CategoriaRepository categorias, UsuarioRepository usuarios) {
        this(anuncios, categorias, usuarios, null, null);
    }

    public CriarAnuncio(AnuncioRepository anuncios, CategoriaRepository categorias, UsuarioRepository usuarios,
            FotoAnuncioRepository fotos, FotoPerfilRepository fotosPerfil) {
        this.anuncios = anuncios;
        this.usuarios = usuarios;
        this.validador = new ValidadorDadosAnuncio(categorias);
        this.detalhador = new DetalhadorAnuncio(categorias, fotos, fotosPerfil);
    }

    /** Só conta com documentos aprovados publica anúncio. */
    public AnuncioDetalhado executar(UUID autorId, DadosAnuncioCommand comando) {
        boolean aprovado = usuarios.buscarPorId(autorId).map(Usuario::isAprovado).orElse(false);
        if (!aprovado) {
            throw new ContaNaoValidadaException();
        }
        var dados = validador.validar(comando);
        Anuncio anuncio = Anuncio.criar(autorId, dados.categoriaId(), dados.tipo(), dados.titulo(),
                dados.descricao(), dados.preco(), dados.localizacao());
        anuncios.salvar(anuncio);
        return detalhador.detalhar(anuncio);
    }
}
