package com.unex.chamaAi.usuario.application.usecases;

import java.time.LocalDate;

public record CadastrarUsuarioCommand(String nome, LocalDate dataNascimento, String cpf, String email,
                                      String senha, String telefone) {

    @Override
    public String toString() {
        return "CadastrarUsuarioCommand[****]";
    }
}
