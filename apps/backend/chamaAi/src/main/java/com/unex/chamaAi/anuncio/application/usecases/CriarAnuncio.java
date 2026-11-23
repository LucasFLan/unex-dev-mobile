package com.unex.chamaAi.anuncio.application.usecases;

import com.unex.chamaAi.anuncio.application.ports.AutoresAnuncio;
import com.unex.chamaAi.anuncio.application.ports.ConsultaCep;
import com.unex.chamaAi.usuario.domain.entities.Usuario;
import com.unex.chamaAi.usuario.domain.exceptions.ContaNaoValidadaException;
import com.unex.chamaAi.usuario.domain.repositories.UsuarioRepository;
import com.unex.chamaAi.anuncio.application.dto.AnuncioDetalhado;
import com.unex.chamaAi.anuncio.application.dto.DadosAnuncioCommand;
import com.unex.chamaAi.anuncio.domain.entities.Anuncio;
import com.unex.chamaAi.anuncio.domain.repositories.AnuncioRepository;
import com.unex.chamaAi.anuncio.domain.repositories.CategoriaRepository;
import com.unex.chamaAi.anuncio.domain.repositories.FotoAnuncioRepository;

import java.util.UUID;

public class CriarAnuncio {

    private final AnuncioRepository anuncios;
    private final UsuarioRepository usuarios;
    private final ValidadorDadosAnuncio validador;
    private final DetalhadorAnuncio detalhador;

    public CriarAnuncio(AnuncioRepository anuncios, CategoriaRepository categorias, UsuarioRepository usuarios,
                        ConsultaCep consultaCep, AutoresAnuncio autores) {
        this(anuncios, categorias, usuarios, consultaCep, autores, null);
    }

    public CriarAnuncio(AnuncioRepository anuncios, CategoriaRepository categorias, UsuarioRepository usuarios,
                        ConsultaCep consultaCep, AutoresAnuncio autores,
            FotoAnuncioRepository fotos) {
        this.anuncios = anuncios;
        this.usuarios = usuarios;
        this.validador = new ValidadorDadosAnuncio(categorias, consultaCep);
        this.detalhador = new DetalhadorAnuncio(categorias, autores, fotos);
    }

    /** Só conta com documentos aprovados publica anúncio. */
    public AnuncioDetalhado executar(UUID autorId, DadosAnuncioCommand comando) {
        boolean aprovado = usuarios.buscarPorId(autorId).map(Usuario::isAprovado).orElse(false);
        if (!aprovado) {
            throw new ContaNaoValidadaException();
        }
        var dados = validador.validar(comando);
        Anuncio anuncio = Anuncio.criar(autorId, dados.categoriaId(), dados.tipo(), dados.titulo(),
                dados.descricao(), dados.preco(), dados.localizacao(), dados.endereco(), dados.raioAtendimentoKm());
        anuncios.salvar(anuncio);
        return detalhador.detalhar(anuncio);
    }
}
