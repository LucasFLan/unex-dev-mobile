package com.unex.chamaAi.usuario.infrastructure.web.dto.request;

import com.unex.chamaAi.usuario.application.dto.CadastrarUsuarioCommand;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED;
import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Schema(description = "Dados para criar uma conta.")
public record CadastrarUsuarioRequest(
        @Schema(description = "Nome completo. Espaços nas pontas são removidos.", example = "João da Silva",
                maxLength = 150, requiredMode = REQUIRED)
        String nome,

        @Schema(description = "Data de nascimento (ISO-8601, `yyyy-MM-dd`). Não pode estar no futuro.",
                example = "2000-01-31", requiredMode = REQUIRED)
        LocalDate dataNascimento,

        @Schema(description = "CPF válido, com ou sem máscara. É armazenado só com os 11 dígitos.",
                example = "529.982.247-25", requiredMode = REQUIRED)
        String cpf,

        @Schema(description = "Email único no sistema.", example = "joao@unex.edu.br", maxLength = 254,
                format = "email", requiredMode = REQUIRED)
        String email,

        @Schema(description = "Mínimo de 8 caracteres, com pelo menos uma letra maiúscula, um número e um "
                + "caractere especial. Máximo de 72 bytes (limite do BCrypt).",
                example = "Senha123!", minLength = 8, format = "password", requiredMode = REQUIRED)
        String senha,

        @Schema(description = "Telefone para contato.", example = "71999999999", maxLength = 20,
                requiredMode = NOT_REQUIRED, nullable = true)
        String telefone) {

    public CadastrarUsuarioCommand paraComando() {
        return new CadastrarUsuarioCommand(nome, dataNascimento, cpf, email, senha, telefone);
    }

    @Override
    public String toString() {
        return "CadastrarUsuarioRequest[****]";
    }
}
