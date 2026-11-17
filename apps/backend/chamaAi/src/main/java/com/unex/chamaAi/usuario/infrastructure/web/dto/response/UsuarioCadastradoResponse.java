package com.unex.chamaAi.usuario.infrastructure.web.dto.response;

import com.unex.chamaAi.usuario.application.dto.UsuarioCadastrado;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Conta recém-criada.")
public record UsuarioCadastradoResponse(
        @Schema(description = "Id do usuário (é o `sub` do token após o login).",
                example = "3f2b6c1e-8a4d-4e0b-9c7a-1d2e3f4a5b6c")
        UUID id,

        @Schema(description = "Nome cadastrado.", example = "João da Silva")
        String nome,

        @Schema(description = "Email cadastrado.", example = "joao@unex.edu.br", format = "email")
        String email) {

    public static UsuarioCadastradoResponse de(UsuarioCadastrado usuario) {
        return new UsuarioCadastradoResponse(usuario.id(), usuario.nome(), usuario.email());
    }
}
