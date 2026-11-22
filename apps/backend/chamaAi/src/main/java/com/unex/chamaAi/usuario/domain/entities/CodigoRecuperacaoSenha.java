package com.unex.chamaAi.usuario.domain.entities;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.UUID;

/**
 * Código de uso único enviado por e-mail para redefinir a senha. Guarda só o hash do código; expira em
 * {@link #VALIDADE} e é bloqueado depois de {@link #MAXIMO_TENTATIVAS} tentativas erradas.
 */
public class CodigoRecuperacaoSenha {

    public static final Duration VALIDADE = Duration.ofMinutes(15);
    public static final int MAXIMO_TENTATIVAS = 5;

    private final UUID id;
    private final UUID usuarioId;
    private final String codigoHash;
    private final Instant expiraEm;
    private int tentativas;
    private Instant usadoEm;
    private final Instant criadoEm;

    private CodigoRecuperacaoSenha(UUID id, UUID usuarioId, String codigoHash, Instant expiraEm, int tentativas,
                                   Instant usadoEm, Instant criadoEm) {
        this.id = Objects.requireNonNull(id, "O id é obrigatório.");
        this.usuarioId = Objects.requireNonNull(usuarioId, "O usuário é obrigatório.");
        this.codigoHash = Objects.requireNonNull(codigoHash, "O hash do código é obrigatório.");
        this.expiraEm = Objects.requireNonNull(expiraEm, "A expiração é obrigatória.");
        this.tentativas = tentativas;
        this.usadoEm = usadoEm;
        this.criadoEm = Objects.requireNonNull(criadoEm, "A data de criação é obrigatória.");
    }

    public static CodigoRecuperacaoSenha criar(UUID usuarioId, String codigoHash, Instant agora) {
        Instant criadoEm = agora.truncatedTo(ChronoUnit.MICROS);
        return new CodigoRecuperacaoSenha(UUID.randomUUID(), usuarioId, codigoHash, criadoEm.plus(VALIDADE), 0, null,
                criadoEm);
    }

    public static CodigoRecuperacaoSenha reconstituir(UUID id, UUID usuarioId, String codigoHash, Instant expiraEm,
                                                      int tentativas, Instant usadoEm, Instant criadoEm) {
        return new CodigoRecuperacaoSenha(id, usuarioId, codigoHash, expiraEm, tentativas, usadoEm, criadoEm);
    }

    /** Ainda pode ser usado: não foi usado, não expirou e não estourou as tentativas. */
    public boolean ativo(Instant agora) {
        return usadoEm == null && agora.isBefore(expiraEm) && tentativas < MAXIMO_TENTATIVAS;
    }

    /**
     * Confere o hash informado. Errar conta uma tentativa; acertar não muda nada (o código só é consumido
     * em {@link #usar}).
     */
    public boolean conferir(String hashInformado, Instant agora) {
        if (!ativo(agora)) {
            return false;
        }
        if (codigoHash.equals(hashInformado)) {
            return true;
        }
        tentativas++;
        return false;
    }

    public void usar(Instant agora) {
        usadoEm = agora.truncatedTo(ChronoUnit.MICROS);
    }

    /** Um código novo anula os anteriores. */
    public void invalidar(Instant agora) {
        if (usadoEm == null) {
            usar(agora);
        }
    }

    public UUID getId() {
        return id;
    }

    public UUID getUsuarioId() {
        return usuarioId;
    }

    public String getCodigoHash() {
        return codigoHash;
    }

    public Instant getExpiraEm() {
        return expiraEm;
    }

    public int getTentativas() {
        return tentativas;
    }

    public Instant getUsadoEm() {
        return usadoEm;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }
}
