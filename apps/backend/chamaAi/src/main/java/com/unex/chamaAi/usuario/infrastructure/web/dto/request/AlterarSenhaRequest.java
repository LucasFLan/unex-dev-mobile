package com.unex.chamaAi.usuario.infrastructure.web.dto.request;

import com.unex.chamaAi.usuario.application.dto.AlterarSenhaCommand;
import io.swagger.v3.oas.annotations.media.Schema;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Schema(description = "Troca de senha do usuário logado.")
public record AlterarSenhaRequest(
        @Schema(description = "Senha usada hoje no login.", example = "Senha123!", format = "password",
                requiredMode = REQUIRED)
        String senhaAtual,

        @Schema(description = "Nova senha: mínimo de 8 caracteres, com pelo menos uma letra maiúscula, um número "
                + "e um caractere especial. Máximo de 72 bytes.",
                example = "NovaSenha456@", minLength = 8, format = "password", requiredMode = REQUIRED)
        String novaSenha) {

    public AlterarSenhaCommand paraComando() {
        return new AlterarSenhaCommand(senhaAtual, novaSenha);
    }

    @Override
    public String toString() {
        return "AlterarSenhaRequest[****]";
    }
}
