package com.unex.chamaAi.usuario.domain.entities;

import com.unex.chamaAi.shared.domain.exceptions.DadoInvalidoException;
import com.unex.chamaAi.usuario.domain.exceptions.EnvioValidacaoJaAnalisadoException;
import com.unex.chamaAi.usuario.domain.valueobjects.StatusValidacao;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Um envio de documentos para análise. Nasce PENDENTE e o admin aprova ou reprova uma única vez. As imagens
 * ficam no repositório, fora da entidade, para a fila do admin não carregá-las.
 */
public class EnvioValidacao {

    public static final int TAMANHO_MINIMO_MOTIVO = 5;
    public static final int TAMANHO_MAXIMO_MOTIVO = 500;

    private final UUID id;
    private final UUID usuarioId;
    private StatusValidacao status;
    private String motivoReprovacao;
    private final Instant enviadoEm;
    private Instant analisadoEm;
    private UUID analisadoPor;

    private EnvioValidacao(UUID id, UUID usuarioId, StatusValidacao status, String motivoReprovacao,
                           Instant enviadoEm, Instant analisadoEm, UUID analisadoPor) {
        this.id = Objects.requireNonNull(id, "O id é obrigatório.");
        this.usuarioId = Objects.requireNonNull(usuarioId, "O usuário é obrigatório.");
        this.status = Objects.requireNonNull(status, "O status é obrigatório.");
        this.motivoReprovacao = motivoReprovacao;
        this.enviadoEm = Objects.requireNonNull(enviadoEm, "A data de envio é obrigatória.");
        this.analisadoEm = analisadoEm;
        this.analisadoPor = analisadoPor;
    }

    public static EnvioValidacao criar(UUID usuarioId, Instant agora) {
        return new EnvioValidacao(UUID.randomUUID(), usuarioId, StatusValidacao.PENDENTE, null,
                agora.truncatedTo(ChronoUnit.MICROS), null, null);
    }

    public static EnvioValidacao reconstituir(UUID id, UUID usuarioId, StatusValidacao status,
                                              String motivoReprovacao, Instant enviadoEm, Instant analisadoEm,
                                              UUID analisadoPor) {
        return new EnvioValidacao(id, usuarioId, status, motivoReprovacao, enviadoEm, analisadoEm, analisadoPor);
    }

    public void aprovar(UUID adminId, Instant agora) {
        analisar(adminId, agora);
        status = StatusValidacao.APROVADO;
    }

    public void reprovar(UUID adminId, String motivo, Instant agora) {
        String motivoValido = validarMotivo(motivo);
        analisar(adminId, agora);
        status = StatusValidacao.REPROVADO;
        motivoReprovacao = motivoValido;
    }

    private void analisar(UUID adminId, Instant agora) {
        if (!isPendente()) {
            throw new EnvioValidacaoJaAnalisadoException();
        }
        analisadoPor = Objects.requireNonNull(adminId, "O admin é obrigatório.");
        analisadoEm = agora.truncatedTo(ChronoUnit.MICROS);
    }

    private static String validarMotivo(String motivo) {
        String semEspacos = motivo == null ? "" : motivo.trim();
        if (semEspacos.length() < TAMANHO_MINIMO_MOTIVO) {
            throw new DadoInvalidoException("Explique o motivo da reprovação (pelo menos " + TAMANHO_MINIMO_MOTIVO
                    + " caracteres): o usuário vai ver essa mensagem.");
        }
        if (semEspacos.length() > TAMANHO_MAXIMO_MOTIVO) {
            throw new DadoInvalidoException("O motivo pode ter no máximo " + TAMANHO_MAXIMO_MOTIVO + " caracteres.");
        }
        return semEspacos;
    }

    public boolean isPendente() {
        return status == StatusValidacao.PENDENTE;
    }

    public UUID getId() {
        return id;
    }

    public UUID getUsuarioId() {
        return usuarioId;
    }

    public StatusValidacao getStatus() {
        return status;
    }

    public Optional<String> getMotivoReprovacao() {
        return Optional.ofNullable(motivoReprovacao);
    }

    public Instant getEnviadoEm() {
        return enviadoEm;
    }

    public Optional<Instant> getAnalisadoEm() {
        return Optional.ofNullable(analisadoEm);
    }

    public Optional<UUID> getAnalisadoPor() {
        return Optional.ofNullable(analisadoPor);
    }
}
