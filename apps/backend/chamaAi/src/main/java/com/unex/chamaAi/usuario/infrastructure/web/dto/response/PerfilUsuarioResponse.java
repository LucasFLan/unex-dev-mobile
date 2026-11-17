package com.unex.chamaAi.usuario.infrastructure.web.dto.response;

import com.unex.chamaAi.usuario.application.dto.PerfilUsuario;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.UUID;

@Schema(description = "Perfil do usuário logado. A senha nunca é retornada.")
public record PerfilUsuarioResponse(
        @Schema(description = "Id do usuário.", example = "3f2b6c1e-8a4d-4e0b-9c7a-1d2e3f4a5b6c")
        UUID id,

        @Schema(description = "Nome completo.", example = "João da Silva")
        String nome,

        @Schema(description = "Data de nascimento (`yyyy-MM-dd`).", example = "2000-01-31")
        LocalDate dataNascimento,

        @Schema(description = "CPF só com os 11 dígitos, sem máscara.", example = "52998224725")
        String cpf,

        @Schema(description = "Email da conta.", example = "joao@unex.edu.br", format = "email")
        String email,

        @Schema(description = "Telefone para contato.", example = "71999999999", nullable = true)
        String telefone,

        @Schema(description = "Se a conta já foi verificada. Toda conta nova começa como `false`.", example = "false")
        boolean verificado,

        @Schema(description = "Papel de acesso.", example = "USUARIO", allowableValues = {"USUARIO", "ADMIN"})
        String papel) {

    public static PerfilUsuarioResponse de(PerfilUsuario perfil) {
        return new PerfilUsuarioResponse(perfil.id(), perfil.nome(), perfil.dataNascimento(), perfil.cpf(),
                perfil.email(), perfil.telefone(), perfil.verificado(), perfil.papel());
    }
}
