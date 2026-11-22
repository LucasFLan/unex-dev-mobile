package com.unex.chamaAi.proposta.domain.entities;

import com.unex.chamaAi.anuncio.domain.valueobjects.Preco;
import com.unex.chamaAi.proposta.domain.exceptions.PropostaJaRespondidaException;
import com.unex.chamaAi.proposta.domain.valueobjects.StatusProposta;
import com.unex.chamaAi.shared.domain.exceptions.DadoInvalidoException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Proposta que um usuário envia para o autor de um anúncio: um profissional respondendo a um pedido, ou um
 * cliente pedindo orçamento para uma oferta. Nasce PENDENTE e só sai desse estado uma vez.
 */
public class Proposta {

    public static final int TAMANHO_MAXIMO_MENSAGEM = 1000;

    private final UUID id;
    private final UUID anuncioId;
    private final UUID remetenteId;
    private final UUID destinatarioId;
    private final Preco valor;
    private final String mensagem;
    private StatusProposta status;
    private final Instant criadoEm;
    private Instant respondidoEm;

    private Proposta(UUID id, UUID anuncioId, UUID remetenteId, UUID destinatarioId, Preco valor, String mensagem,
                     StatusProposta status, Instant criadoEm, Instant respondidoEm) {
        this.id = Objects.requireNonNull(id, "O id é obrigatório.");
        this.anuncioId = Objects.requireNonNull(anuncioId, "O anúncio é obrigatório.");
        this.remetenteId = Objects.requireNonNull(remetenteId, "O remetente é obrigatório.");
        this.destinatarioId = Objects.requireNonNull(destinatarioId, "O destinatário é obrigatório.");
        this.valor = valor;
        this.mensagem = validarMensagem(mensagem);
        this.status = Objects.requireNonNull(status, "O status é obrigatório.");
        this.criadoEm = Objects.requireNonNull(criadoEm, "A data de criação é obrigatória.");
        this.respondidoEm = respondidoEm;
    }

    /** valor nulo = "a combinar". */
    public static Proposta criar(UUID anuncioId, UUID remetenteId, UUID destinatarioId, Preco valor,
                                 String mensagem) {
        return new Proposta(UUID.randomUUID(), anuncioId, remetenteId, destinatarioId, valor, mensagem,
                StatusProposta.PENDENTE, agora(), null);
    }

    public static Proposta reconstituir(UUID id, UUID anuncioId, UUID remetenteId, UUID destinatarioId, Preco valor,
                                        String mensagem, StatusProposta status, Instant criadoEm,
                                        Instant respondidoEm) {
        return new Proposta(id, anuncioId, remetenteId, destinatarioId, valor, mensagem, status, criadoEm,
                respondidoEm);
    }

    /** Decisão do autor do anúncio. */
    public void aceitar() {
        responder(StatusProposta.ACEITA);
    }

    /** Decisão do autor do anúncio. */
    public void recusar() {
        responder(StatusProposta.RECUSADA);
    }

    /** O autor aceitou outra proposta para o mesmo pedido. */
    public void naoSelecionar() {
        responder(StatusProposta.NAO_SELECIONADA);
    }

    /** Quem enviou retira a proposta. */
    public void cancelar() {
        responder(StatusProposta.CANCELADA);
    }

    private void responder(StatusProposta novoStatus) {
        if (!isPendente()) {
            throw new PropostaJaRespondidaException();
        }
        status = novoStatus;
        respondidoEm = agora();
    }

    public boolean isPendente() {
        return status == StatusProposta.PENDENTE;
    }

    /** Só depois do aceite os dois lados veem o contato um do outro. */
    public boolean contatoLiberado() {
        return status == StatusProposta.ACEITA;
    }

    public boolean foiEnviadaPor(UUID usuarioId) {
        return remetenteId.equals(usuarioId);
    }

    public boolean foiRecebidaPor(UUID usuarioId) {
        return destinatarioId.equals(usuarioId);
    }

    /** Precisão de microssegundos, a mesma do TIMESTAMPTZ do Postgres. */
    private static Instant agora() {
        return Instant.now().truncatedTo(ChronoUnit.MICROS);
    }

    private static String validarMensagem(String mensagem) {
        if (mensagem == null || mensagem.isBlank()) {
            throw new DadoInvalidoException("A mensagem da proposta é obrigatória.");
        }
        String semEspacos = mensagem.trim();
        if (semEspacos.length() > TAMANHO_MAXIMO_MENSAGEM) {
            throw new DadoInvalidoException("A mensagem deve ter no máximo " + TAMANHO_MAXIMO_MENSAGEM
                    + " caracteres.");
        }
        return semEspacos;
    }

    public UUID getId() {
        return id;
    }

    public UUID getAnuncioId() {
        return anuncioId;
    }

    public UUID getRemetenteId() {
        return remetenteId;
    }

    public UUID getDestinatarioId() {
        return destinatarioId;
    }

    /** Vazio significa "a combinar". */
    public Optional<Preco> getValor() {
        return Optional.ofNullable(valor);
    }

    public String getMensagem() {
        return mensagem;
    }

    public StatusProposta getStatus() {
        return status;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }

    /** Vazio enquanto a proposta está pendente. */
    public Optional<Instant> getRespondidoEm() {
        return Optional.ofNullable(respondidoEm);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Proposta outra)) return false;
        return id.equals(outra.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
