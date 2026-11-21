package com.unex.chamaAi.notificacao.domain.entities;

import com.unex.chamaAi.notificacao.domain.valueobjects.TipoNotificacao;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.UUID;

public class Notificacao {

    private final UUID id;
    private final UUID usuarioId;
    private final TipoNotificacao tipo;
    private final String titulo;
    private final String mensagem;
    private final UUID anuncioId;
    private final UUID propostaId;
    private boolean lida;
    private final Instant criadoEm;

    private Notificacao(UUID id, UUID usuarioId, TipoNotificacao tipo, String titulo, String mensagem,
                        UUID anuncioId, UUID propostaId, boolean lida, Instant criadoEm) {
        this.id = Objects.requireNonNull(id, "O id é obrigatório.");
        this.usuarioId = Objects.requireNonNull(usuarioId, "O usuário é obrigatório.");
        this.tipo = Objects.requireNonNull(tipo, "O tipo é obrigatório.");
        this.titulo = Objects.requireNonNull(titulo, "O título é obrigatório.");
        this.mensagem = Objects.requireNonNull(mensagem, "A mensagem é obrigatória.");
        this.anuncioId = anuncioId;
        this.propostaId = propostaId;
        this.lida = lida;
        this.criadoEm = Objects.requireNonNull(criadoEm, "A data de criação é obrigatória.");
    }

    /** Toda notificação nasce não lida. anuncioId e propostaId podem ser nulos. */
    public static Notificacao criar(UUID usuarioId, TipoNotificacao tipo, String titulo, String mensagem,
                                    UUID anuncioId, UUID propostaId) {
        return new Notificacao(UUID.randomUUID(), usuarioId, tipo, titulo, mensagem, anuncioId, propostaId, false,
                Instant.now().truncatedTo(ChronoUnit.MICROS));
    }

    public static Notificacao reconstituir(UUID id, UUID usuarioId, TipoNotificacao tipo, String titulo,
                                           String mensagem, UUID anuncioId, UUID propostaId, boolean lida,
                                           Instant criadoEm) {
        return new Notificacao(id, usuarioId, tipo, titulo, mensagem, anuncioId, propostaId, lida, criadoEm);
    }

    /** Idempotente. */
    public void marcarComoLida() {
        lida = true;
    }

    public boolean pertenceA(UUID usuario) {
        return usuarioId.equals(usuario);
    }

    public UUID getId() {
        return id;
    }

    public UUID getUsuarioId() {
        return usuarioId;
    }

    public TipoNotificacao getTipo() {
        return tipo;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getMensagem() {
        return mensagem;
    }

    public UUID getAnuncioId() {
        return anuncioId;
    }

    public UUID getPropostaId() {
        return propostaId;
    }

    public boolean isLida() {
        return lida;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Notificacao outra)) return false;
        return id.equals(outra.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
