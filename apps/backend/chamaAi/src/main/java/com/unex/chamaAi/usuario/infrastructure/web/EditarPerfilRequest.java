package com.unex.chamaAi.usuario.infrastructure.web;

import com.unex.chamaAi.usuario.application.dto.EditarPerfilCommand;

import java.time.LocalDate;

public record EditarPerfilRequest(String nome, LocalDate dataNascimento, String telefone) {

    public EditarPerfilCommand paraComando() {
        return new EditarPerfilCommand(nome, dataNascimento, telefone);
    }
}
