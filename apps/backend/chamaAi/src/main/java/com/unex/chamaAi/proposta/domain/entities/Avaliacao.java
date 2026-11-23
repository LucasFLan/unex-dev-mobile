package com.unex.chamaAi.proposta.domain.entities;

import com.unex.chamaAi.shared.domain.exceptions.DadoInvalidoException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Avaliação que quem contratou dá ao profissional depois de concluir o serviço: nota de 1 a 5 e um comentário
 * opcional. Guarda o título e a categoria do serviço para continuar legível se o anúncio for apagado.
 */
public class Avaliacao {

    public static final int NOTA_MINIMA = 1;
    public static final int NOTA_MAXIMA = 5;
    public static final int TAMANHO_MAXIMO_COMENTARIO = 500;

    private final UUID id;
    /** Vazio se a proposta deixou de existir (anúncio apagado). */
    private final UUID propostaId;
    /** Vazio se quem avaliou apagou a conta. */
    private final UUID avaliadorId;
    private final UUID avaliadoId;
    private final int nota;
    private final String comentario;
    private final String servicoTitulo;
    private final String servicoCategoria;
    private final Instant criadoEm;

    private Avaliacao(UUID id, UUID propostaId, UUID avaliadorId, UUID avaliadoId, int nota, String comentario,
                      String servicoTitulo, String servicoCategoria, Instant criadoEm) {
        this.id = Objects.requireNonNull(id, "O id é obrigatório.");
        this.propostaId = propostaId;
        this.avaliadorId = avaliadorId;
        this.avaliadoId = Objects.requireNonNull(avaliadoId, "Quem é avaliado é obrigatório.");
        this.nota = validarNota(nota);
        this.comentario = validarComentario(comentario);
        this.servicoTitulo = Objects.requireNonNull(servicoTitulo, "O título do serviço é obrigatório.");
        this.servicoCategoria = servicoCategoria;
        this.criadoEm = Objects.requireNonNull(criadoEm, "A data é obrigatória.");
    }

    public static Avaliacao criar(UUID propostaId, UUID avaliadorId, UUID avaliadoId, Integer nota,
                                  String comentario, String servicoTitulo, String servicoCategoria) {
        if (nota == null) {
            throw new DadoInvalidoException("Escolha uma nota de 1 a 5 estrelas.");
        }
        return new Avaliacao(UUID.randomUUID(), Objects.requireNonNull(propostaId),
                Objects.requireNonNull(avaliadorId), avaliadoId, nota, comentario, servicoTitulo, servicoCategoria,
                Instant.now().truncatedTo(ChronoUnit.MICROS));
    }

    public static Avaliacao reconstituir(UUID id, UUID propostaId, UUID avaliadorId, UUID avaliadoId, int nota,
                                         String comentario, String servicoTitulo, String servicoCategoria,
                                         Instant criadoEm) {
        return new Avaliacao(id, propostaId, avaliadorId, avaliadoId, nota, comentario, servicoTitulo,
                servicoCategoria, criadoEm);
    }

    private static int validarNota(int nota) {
        if (nota < NOTA_MINIMA || nota > NOTA_MAXIMA) {
            throw new DadoInvalidoException("A nota deve ser de 1 a 5 estrelas.");
        }
        return nota;
    }

    /** Comentário em branco vira "sem comentário". */
    private static String validarComentario(String comentario) {
        if (comentario == null || comentario.isBlank()) {
            return null;
        }
        String semEspacos = comentario.trim();
        if (semEspacos.length() > TAMANHO_MAXIMO_COMENTARIO) {
            throw new DadoInvalidoException("O comentário deve ter no máximo " + TAMANHO_MAXIMO_COMENTARIO
                    + " caracteres.");
        }
        return semEspacos;
    }

    public UUID getId() {
        return id;
    }

    public Optional<UUID> getPropostaId() {
        return Optional.ofNullable(propostaId);
    }

    public Optional<UUID> getAvaliadorId() {
        return Optional.ofNullable(avaliadorId);
    }

    public UUID getAvaliadoId() {
        return avaliadoId;
    }

    public int getNota() {
        return nota;
    }

    public Optional<String> getComentario() {
        return Optional.ofNullable(comentario);
    }

    public String getServicoTitulo() {
        return servicoTitulo;
    }

    public Optional<String> getServicoCategoria() {
        return Optional.ofNullable(servicoCategoria);
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Avaliacao outra && id.equals(outra.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
