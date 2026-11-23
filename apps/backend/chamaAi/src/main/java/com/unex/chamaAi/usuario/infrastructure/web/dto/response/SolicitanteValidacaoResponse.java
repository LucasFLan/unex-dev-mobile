package com.unex.chamaAi.usuario.infrastructure.web.dto.response;

import com.unex.chamaAi.usuario.application.dto.EnvioParaAnalise.Solicitante;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.UUID;

@Schema(description = "Dados do cadastro de quem enviou os documentos, para comparar com as imagens.")
public record SolicitanteValidacaoResponse(
        @Schema(description = "Id do usuário.", example = "3f2b6c1e-8a4d-4e0b-9c7a-1d2e3f4a5b6c")
        UUID id,

        @Schema(description = "Nome completo do cadastro.", example = "João da Silva")
        String nome,

        @Schema(description = "Email da conta.", example = "joao@unex.edu.br")
        String email,

        @Schema(description = "CPF só com dígitos.", example = "52998224725")
        String cpf,

        @Schema(description = "Data de nascimento (`yyyy-MM-dd`).", example = "2000-01-31")
        LocalDate dataNascimento,

        @Schema(description = "Telefone.", example = "71999999999", nullable = true)
        String telefone) {

    public static SolicitanteValidacaoResponse de(Solicitante solicitante) {
        return solicitante == null ? null : new SolicitanteValidacaoResponse(solicitante.id(), solicitante.nome(),
                solicitante.email(), solicitante.cpf(), solicitante.dataNascimento(), solicitante.telefone());
    }
}
