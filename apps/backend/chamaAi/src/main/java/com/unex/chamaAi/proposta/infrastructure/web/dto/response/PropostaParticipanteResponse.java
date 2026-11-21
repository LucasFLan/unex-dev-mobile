package com.unex.chamaAi.proposta.infrastructure.web.dto.response;

import com.unex.chamaAi.proposta.application.dto.PropostaDetalhada.ParticipanteResumo;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Pessoa envolvida na proposta (quem enviou ou quem recebeu).")
public record PropostaParticipanteResponse(
        @Schema(description = "Id do usuário.", example = "3f2b6c1e-8a4d-4e0b-9c7a-1d2e3f4a5b6c")
        UUID id,

        @Schema(description = "Nome do usuário.", example = "Maria Souza")
        String nome,

        @Schema(description = "Telefone para contato. Só vem preenchido depois que a proposta é **aceita**.",
                example = "71999999999", nullable = true)
        String telefone) {

    public static PropostaParticipanteResponse de(ParticipanteResumo participante) {
        return participante == null ? null
                : new PropostaParticipanteResponse(participante.id(), participante.nome(), participante.telefone());
    }
}
