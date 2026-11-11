package com.unex.chamaAi.usuario.infrastructure.web;

import com.unex.chamaAi.usuario.application.dto.CadastrarUsuarioCommand;

import java.time.LocalDate;

public record CadastrarUsuarioRequest(String nome, LocalDate dataNascimento, String cpf, String email,
                                      String senha, String telefone) {

    public CadastrarUsuarioCommand paraComando() {
        return new CadastrarUsuarioCommand(nome, dataNascimento, cpf, email, senha, telefone);
    }

    @Override
    public String toString() {
        return "CadastrarUsuarioRequest[****]";
    }
}
