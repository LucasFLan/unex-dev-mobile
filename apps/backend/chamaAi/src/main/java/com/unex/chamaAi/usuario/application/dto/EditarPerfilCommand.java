package com.unex.chamaAi.usuario.application.dto;

import java.time.LocalDate;

public record EditarPerfilCommand(String nome, LocalDate dataNascimento, String telefone) {
}
