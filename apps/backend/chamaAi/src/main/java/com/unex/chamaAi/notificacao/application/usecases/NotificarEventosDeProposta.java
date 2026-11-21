package com.unex.chamaAi.notificacao.application.usecases;

import com.unex.chamaAi.anuncio.domain.valueobjects.Preco;
import com.unex.chamaAi.notificacao.domain.entities.Notificacao;
import com.unex.chamaAi.notificacao.domain.repositories.NotificacaoRepository;
import com.unex.chamaAi.notificacao.domain.valueobjects.TipoNotificacao;
import com.unex.chamaAi.proposta.application.ports.NotificadorProposta;
import com.unex.chamaAi.proposta.domain.entities.Proposta;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.UUID;

/** Cria as notificações de cada etapa de uma proposta. */
public class NotificarEventosDeProposta implements NotificadorProposta {

    static final String TITULO_RECEBIDA = "Nova proposta recebida";
    private static final int TAMANHO_MAXIMO_TRECHO = 200;
    private static final Locale PT_BR = Locale.of("pt", "BR");

    private final NotificacaoRepository notificacoes;

    public NotificarEventosDeProposta(NotificacaoRepository notificacoes) {
        this.notificacoes = notificacoes;
    }

    @Override
    public void propostaRecebida(Proposta proposta, String tituloAnuncio, String nomeRemetente) {
        String mensagem = "%s enviou uma proposta %s para \"%s\": %s"
                .formatted(nomeRemetente, valor(proposta), tituloAnuncio, trecho(proposta.getMensagem()));
        salvar(proposta, proposta.getDestinatarioId(), TipoNotificacao.PROPOSTA_RECEBIDA, TITULO_RECEBIDA, mensagem);
    }

    @Override
    public void propostaRespondida(Proposta proposta, String tituloAnuncio, String nomeDestinatario) {
        switch (proposta.getStatus()) {
            case ACEITA -> salvar(proposta, proposta.getRemetenteId(), TipoNotificacao.PROPOSTA_ACEITA,
                    "Proposta aceita!",
                    "%s aceitou sua proposta para \"%s\". Os contatos foram liberados: combine os detalhes."
                            .formatted(nomeDestinatario, tituloAnuncio));
            case RECUSADA -> salvar(proposta, proposta.getRemetenteId(), TipoNotificacao.PROPOSTA_RECUSADA,
                    "Proposta recusada",
                    "%s recusou sua proposta para \"%s\".".formatted(nomeDestinatario, tituloAnuncio));
            case NAO_SELECIONADA -> salvar(proposta, proposta.getRemetenteId(),
                    TipoNotificacao.PROPOSTA_NAO_SELECIONADA, "Proposta não selecionada",
                    "%s escolheu outra proposta para \"%s\". Obrigado pelo interesse!"
                            .formatted(nomeDestinatario, tituloAnuncio));
            default -> {
                // PENDENTE e CANCELADA não geram aviso para quem enviou.
            }
        }
    }

    private void salvar(Proposta proposta, UUID usuarioId, TipoNotificacao tipo, String titulo,
                        String mensagem) {
        notificacoes.salvar(Notificacao.criar(usuarioId, tipo, titulo, mensagem, proposta.getAnuncioId(),
                proposta.getId()));
    }

    private static String valor(Proposta proposta) {
        return proposta.getValor()
                .map(Preco::valor)
                .map(v -> "de " + NumberFormat.getCurrencyInstance(PT_BR).format(v))
                .orElse("(valor a combinar)");
    }

    private static String trecho(String texto) {
        return texto.length() <= TAMANHO_MAXIMO_TRECHO ? texto : texto.substring(0, TAMANHO_MAXIMO_TRECHO) + "…";
    }
}
