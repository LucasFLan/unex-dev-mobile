package com.unex.chamaAi.proposta.infrastructure.web.dto.response;

import com.unex.chamaAi.proposta.application.dto.PropostaDetalhada;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Schema(description = "Proposta enviada para o autor de um anúncio.")
public record PropostaResponse(
        @Schema(description = "Id da proposta.", example = "c4d5e6f7-1a2b-4c3d-8e9f-0a1b2c3d4e5f")
        UUID id,

        @Schema(description = "Anúncio da proposta.")
        PropostaAnuncioResponse anuncio,

        @Schema(description = "Quem enviou a proposta.")
        PropostaParticipanteResponse remetente,

        @Schema(description = "Quem recebeu (autor do anúncio).")
        PropostaParticipanteResponse destinatario,

        @Schema(description = "Valor proposto em reais. `null` = \"a combinar\".", example = "180.00", nullable = true)
        BigDecimal valor,

        @Schema(description = "Mensagem para o autor do anúncio.",
                example = "Posso fazer amanhã pela manhã, levo o material.")
        String mensagem,

        @Schema(description = """
                `PENDENTE` (aguardando resposta), `ACEITA`, `RECUSADA`, `NAO_SELECIONADA` (o autor do pedido aceitou \
                outra proposta), `CANCELADA` (quem enviou retirou) ou `CONCLUIDA` (quem contratou confirmou que o \
                serviço foi feito).""", example = "PENDENTE",
                allowableValues = {"PENDENTE", "ACEITA", "RECUSADA", "NAO_SELECIONADA", "CANCELADA", "CONCLUIDA"})
        String status,

        @Schema(description = "Data de envio (ISO-8601, UTC).", example = "2026-11-20T21:30:00.123456Z")
        Instant criadoEm,

        @Schema(description = "Data em que saiu de PENDENTE (ISO-8601, UTC). `null` enquanto pendente.",
                example = "2026-11-21T09:15:00.654321Z", nullable = true)
        Instant respondidoEm,

        @Schema(description = "Data em que quem contratou marcou o serviço como concluído. `null` antes disso.",
                example = "2026-11-22T18:00:00.000000Z", nullable = true)
        Instant concluidoEm,

        @Schema(description = "Ids das fotos anexadas, na ordem do envio (vazio = sem fotos). Cada imagem fica em "
                + "`GET /propostas/{id}/fotos/{fotoId}`.", example = "[\"0b1c2d3e-4f5a-4b6c-8d7e-9f0a1b2c3d4e\"]")
        List<UUID> fotos,

        @Schema(description = "Avaliação de quem contratou, depois de concluído. `null` enquanto não houver.",
                nullable = true)
        Avaliacao avaliacao,

        @Schema(description = "Endereço completo do anúncio. Só vem para as duas pessoas depois que a proposta é aceita; "
                + "campos do endereço podem ser `null` em anúncios sem CEP.", nullable = true)
        Endereco endereco) {

    @Schema(description = "Nota (1 a 5) e comentário de quem contratou.")
    public record Avaliacao(
            @Schema(example = "5") int nota,
            @Schema(example = "Pontual e caprichoso.", nullable = true) String comentario,
            Instant criadoEm) {
    }

    @Schema(description = "Endereço do serviço, liberado depois do aceite.")
    public record Endereco(
            @Schema(example = "40140110", nullable = true) String cep,
            @Schema(example = "Rua Exemplo", nullable = true) String logradouro,
            @Schema(example = "123", nullable = true) String numero,
            @Schema(example = "Apto 201", nullable = true) String complemento,
            @Schema(example = "Barra", nullable = true) String bairro,
            @Schema(example = "Salvador") String cidade,
            @Schema(example = "BA") String uf,
            @Schema(example = "-13.0101", nullable = true) Double latitude,
            @Schema(example = "-38.5101", nullable = true) Double longitude) {
    }


    public static PropostaResponse de(PropostaDetalhada proposta) {
        return new PropostaResponse(proposta.id(), PropostaAnuncioResponse.de(proposta.anuncio()),
                PropostaParticipanteResponse.de(proposta.remetente()),
                PropostaParticipanteResponse.de(proposta.destinatario()), proposta.valor(), proposta.mensagem(),
                proposta.status(), proposta.criadoEm(), proposta.respondidoEm(), proposta.concluidoEm(),
                proposta.fotos(),
                proposta.avaliacao() == null ? null : new Avaliacao(proposta.avaliacao().nota(),
                        proposta.avaliacao().comentario(), proposta.avaliacao().criadoEm()),
                proposta.endereco() == null ? null : new Endereco(proposta.endereco().cep(),
                        proposta.endereco().logradouro(), proposta.endereco().numero(),
                        proposta.endereco().complemento(), proposta.endereco().bairro(), proposta.endereco().cidade(),
                        proposta.endereco().uf(), proposta.endereco().latitude(), proposta.endereco().longitude()));
    }
}
