package com.unex.chamaAi.anuncio.domain.entities;

import com.unex.chamaAi.anuncio.domain.valueobjects.Endereco;
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
    public static final int RAIO_MAXIMO_KM = 200;

    private final UUID id;
    private final UUID autorId;
    private UUID categoriaId;
    private TipoAnuncio tipo;
    private String titulo;
    private String descricao;
    private Preco preco;
    private Localizacao localizacao;
    /** Vazio em anúncios antigos, criados antes do CEP. */
    private Endereco endereco;
    /** Até onde o profissional atende (só OFERTA); vazio = não informado. */
    private Integer raioAtendimentoKm;
    private boolean ativo;
    private final Instant criadoEm;
    private Instant atualizadoEm;

    private Anuncio(UUID id, UUID autorId, UUID categoriaId, TipoAnuncio tipo, String titulo, String descricao,
                    Preco preco, Localizacao localizacao, Endereco endereco, Integer raioAtendimentoKm,
                    boolean ativo, Instant criadoEm, Instant atualizadoEm) {
        this.id = Objects.requireNonNull(id, "O id é obrigatório.");
        this.autorId = Objects.requireNonNull(autorId, "O autor é obrigatório.");
        this.categoriaId = validarCategoria(categoriaId);
        this.tipo = validarTipo(tipo);
        this.titulo = validarTitulo(titulo);
        this.descricao = validarDescricao(descricao);
        this.preco = preco;
        this.localizacao = validarLocalizacao(localizacao);
        this.endereco = endereco;
        this.raioAtendimentoKm = validarRaio(this.tipo, raioAtendimentoKm);
        this.ativo = ativo;
        this.criadoEm = Objects.requireNonNull(criadoEm, "A data de criação é obrigatória.");
        this.atualizadoEm = Objects.requireNonNull(atualizadoEm, "A data de atualização é obrigatória.");
    }

    public static Anuncio criar(UUID autorId, UUID categoriaId, TipoAnuncio tipo, String titulo, String descricao,
                                Preco preco, Localizacao localizacao) {
        return criar(autorId, categoriaId, tipo, titulo, descricao, preco, localizacao, null, null);
    }

    public static Anuncio criar(UUID autorId, UUID categoriaId, TipoAnuncio tipo, String titulo, String descricao,
                                Preco preco, Localizacao localizacao, Endereco endereco, Integer raioAtendimentoKm) {
        Instant agora = agora();
        return new Anuncio(UUID.randomUUID(), autorId, categoriaId, tipo, titulo, descricao, preco, localizacao,
                endereco, raioAtendimentoKm, true, agora, agora);
    }

    public static Anuncio reconstituir(UUID id, UUID autorId, UUID categoriaId, TipoAnuncio tipo, String titulo,
                                       String descricao, Preco preco, Localizacao localizacao, boolean ativo,
                                       Instant criadoEm, Instant atualizadoEm) {
        return reconstituir(id, autorId, categoriaId, tipo, titulo, descricao, preco, localizacao, null, null, ativo,
                criadoEm, atualizadoEm);
    }

    public static Anuncio reconstituir(UUID id, UUID autorId, UUID categoriaId, TipoAnuncio tipo, String titulo,
                                       String descricao, Preco preco, Localizacao localizacao, Endereco endereco,
                                       Integer raioAtendimentoKm, boolean ativo, Instant criadoEm,
                                       Instant atualizadoEm) {
        return new Anuncio(id, autorId, categoriaId, tipo, titulo, descricao, preco, localizacao, endereco,
                raioAtendimentoKm, ativo, criadoEm, atualizadoEm);
    }

    public void editar(UUID novaCategoriaId, TipoAnuncio novoTipo, String novoTitulo, String novaDescricao,
                       Preco novoPreco, Localizacao novaLocalizacao) {
        editar(novaCategoriaId, novoTipo, novoTitulo, novaDescricao, novoPreco, novaLocalizacao, endereco,
                raioAtendimentoKm);
    }

    /** A edição substitui tudo, inclusive o endereço (vazio = sem CEP) e o raio. */
    public void editar(UUID novaCategoriaId, TipoAnuncio novoTipo, String novoTitulo, String novaDescricao,
                       Preco novoPreco, Localizacao novaLocalizacao, Endereco novoEndereco, Integer novoRaioKm) {
        this.categoriaId = validarCategoria(novaCategoriaId);
        this.tipo = validarTipo(novoTipo);
        this.titulo = validarTitulo(novoTitulo);
        this.descricao = validarDescricao(novaDescricao);
        this.preco = novoPreco;
        this.localizacao = validarLocalizacao(novaLocalizacao);
        this.endereco = novoEndereco;
        this.raioAtendimentoKm = validarRaio(this.tipo, novoRaioKm);
        this.atualizadoEm = agora();
    }

    /** Idempotente: só mexe em atualizadoEm quando o estado muda. */
    public void ativar() {
        alterarAtivo(true);
    }

    /** Idempotente: só mexe em atualizadoEm quando o estado muda. */
    public void desativar() {
        alterarAtivo(false);
    }

    private void alterarAtivo(boolean novoAtivo) {
        if (ativo != novoAtivo) {
            ativo = novoAtivo;
            atualizadoEm = agora();
        }
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

    /** O raio só faz sentido para quem oferece o serviço; num pedido é ignorado. */
    private static Integer validarRaio(TipoAnuncio tipo, Integer raioKm) {
        if (tipo != TipoAnuncio.OFERTA || raioKm == null) {
            return null;
        }
        if (raioKm < 1 || raioKm > RAIO_MAXIMO_KM) {
            throw new DadoInvalidoException("O raio de atendimento deve ser de 1 a " + RAIO_MAXIMO_KM + " km.");
        }
        return raioKm;
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

    /** Vazio em anúncios criados sem CEP. */
    public Optional<Endereco> getEndereco() {
        return Optional.ofNullable(endereco);
    }

    public Optional<Integer> getRaioAtendimentoKm() {
        return Optional.ofNullable(raioAtendimentoKm);
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
