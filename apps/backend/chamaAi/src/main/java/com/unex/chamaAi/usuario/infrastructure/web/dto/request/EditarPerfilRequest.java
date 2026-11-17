package com.unex.chamaAi.usuario.infrastructure.web.dto.request;

import com.unex.chamaAi.usuario.application.dto.EditarPerfilCommand;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED;
import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Schema(description = "Substitui os dados editáveis do perfil. CPF e email não são alterados por esta rota.")
public record EditarPerfilRequest(
        @Schema(description = "Nome completo.", example = "João da Silva Santos", maxLength = 150,
                requiredMode = REQUIRED)
        String nome,

        @Schema(description = "Data de nascimento (`yyyy-MM-dd`). Não pode estar no futuro.",
                example = "2000-01-31", requiredMode = REQUIRED)
        LocalDate dataNascimento,

        @Schema(description = "Telefone para contato. Enviar `null` ou omitir remove o telefone.",
                example = "71988887777", maxLength = 20, requiredMode = NOT_REQUIRED, nullable = true)
        String telefone) {

    public EditarPerfilCommand paraComando() {
        return new EditarPerfilCommand(nome, dataNascimento, telefone);
    }
}
