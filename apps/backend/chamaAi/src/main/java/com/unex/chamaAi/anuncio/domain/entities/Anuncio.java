package com.unex.chamaAi.anuncio.domain.entities;

import com.unex.chamaAi.anuncio.domain.valueobjects.Localizacao;
import com.unex.chamaAi.anuncio.domain.valueobjects.Preco;
import com.unex.chamaAi.anuncio.domain.valueobjects.TipoAnuncio;
import com.unex.chamaAi.shared.domain.exceptions.DadoInvalidoException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class Anuncio {

    private static final int TAMANHO_MINIMO_TITULO = 3;
    private static final int TAMANHO_MAXIMO_TITULO = 120;
    private static final int TAMANHO_MAXIMO_DESCRICAO = 5000;

    private final UUID id;
    private final UUID autorId;
    private UUID categoriaId;
    private TipoAnuncio tipo;
    private String titulo;
    private String descricao;
    private Preco preco;
    private Localizacao localizacao;
    private boolean ativo;
    private final Instant criadoEm;
    private Instant atualizadoEm;

    private Anuncio(UUID id, UUID autorId, UUID categoriaId, TipoAnuncio tipo, String titulo, String descricao,
                    Preco preco, Localizacao localizacao, boolean ativo, Instant criadoEm, Instant atualizadoEm) {
        this.id = Objects.requireNonNull(id, "O id é obrigatório.");
        this.autorId = Objects.requireNonNull(autorId, "O autor é obrigatório.");
        this.categoriaId = validarCategoria(categoriaId);
        this.tipo = validarTipo(tipo);
        this.titulo = validarTitulo(titulo);
        this.descricao = validarDescricao(descricao);
        this.preco = preco;
        this.localizacao = validarLocalizacao(localizacao);
        this.ativo = ativo;
        this.criadoEm = Objects.requireNonNull(criadoEm, "A data de criação é obrigatória.");
        this.atualizadoEm = Objects.requireNonNull(atualizadoEm, "A data de atualização é obrigatória.");
    }

    public static Anuncio criar(UUID autorId, UUID categoriaId, TipoAnuncio tipo, String titulo, String descricao,
                                Preco preco, Localizacao localizacao) {
        Instant agora = agora();
        return new Anuncio(UUID.randomUUID(), autorId, categoriaId, tipo, titulo, descricao, preco, localizacao,
                true, agora, agora);
    }

    public static Anuncio reconstituir(UUID id, UUID autorId, UUID categoriaId, TipoAnuncio tipo, String titulo,
                                       String descricao, Preco preco, Localizacao localizacao, boolean ativo,
                                       Instant criadoEm, Instant atualizadoEm) {
        return new Anuncio(id, autorId, categoriaId, tipo, titulo, descricao, preco, localizacao, ativo,
                criadoEm, atualizadoEm);
    }

    public void editar(UUID novaCategoriaId, TipoAnuncio novoTipo, String novoTitulo, String novaDescricao,
                       Preco novoPreco, Localizacao novaLocalizacao) {
        this.categoriaId = validarCategoria(novaCategoriaId);
        this.tipo = validarTipo(novoTipo);
        this.titulo = validarTitulo(novoTitulo);
        this.descricao = validarDescricao(novaDescricao);
        this.preco = novoPreco;
        this.localizacao = validarLocalizacao(novaLocalizacao);
        this.atualizadoEm = agora();
    }

    public boolean pertenceA(UUID usuarioId) {
        return autorId.equals(usuarioId);
    }

    /** Anúncio inativo só aparece para o próprio autor. */
    public boolean visivelPara(UUID usuarioId) {
        return ativo || pertenceA(usuarioId);
    }

    /** Precisão de microssegundos, a mesma do TIMESTAMPTZ do Postgres. */
    private static Instant agora() {
        return Instant.now().truncatedTo(ChronoUnit.MICROS);
    }

    private static UUID validarCategoria(UUID categoriaId) {
        if (categoriaId == null) {
            throw new DadoInvalidoException("A categoria é obrigatória.");
        }
        return categoriaId;
    }

    private static TipoAnuncio validarTipo(TipoAnuncio tipo) {
        if (tipo == null) {
            throw new DadoInvalidoException("O tipo do anúncio é obrigatório.");
        }
        return tipo;
    }

    private static Localizacao validarLocalizacao(Localizacao localizacao) {
        if (localizacao == null) {
            throw new DadoInvalidoException("A localização é obrigatória.");
        }
        return localizacao;
    }

    private static String validarTitulo(String titulo) {
        if (titulo == null || titulo.isBlank()) {
            throw new DadoInvalidoException("O título é obrigatório.");
        }
        String semEspacos = titulo.trim();
        if (semEspacos.length() < TAMANHO_MINIMO_TITULO || semEspacos.length() > TAMANHO_MAXIMO_TITULO) {
            throw new DadoInvalidoException("O título deve ter entre " + TAMANHO_MINIMO_TITULO + " e "
                    + TAMANHO_MAXIMO_TITULO + " caracteres.");
        }
        return semEspacos;
    }

    private static String validarDescricao(String descricao) {
        if (descricao == null || descricao.isBlank()) {
            throw new DadoInvalidoException("A descrição é obrigatória.");
        }
        String semEspacos = descricao.trim();
        if (semEspacos.length() > TAMANHO_MAXIMO_DESCRICAO) {
            throw new DadoInvalidoException("A descrição deve ter no máximo " + TAMANHO_MAXIMO_DESCRICAO + " caracteres.");
        }
        return semEspacos;
    }

    public UUID getId() {
        return id;
    }

    public UUID getAutorId() {
        return autorId;
    }

    public UUID getCategoriaId() {
        return categoriaId;
    }

    public TipoAnuncio getTipo() {
        return tipo;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    /** Vazio significa "a combinar". */
    public Optional<Preco> getPreco() {
        return Optional.ofNullable(preco);
    }

    public Localizacao getLocalizacao() {
        return localizacao;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }

    public Instant getAtualizadoEm() {
        return atualizadoEm;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Anuncio outro)) return false;
        return id.equals(outro.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
