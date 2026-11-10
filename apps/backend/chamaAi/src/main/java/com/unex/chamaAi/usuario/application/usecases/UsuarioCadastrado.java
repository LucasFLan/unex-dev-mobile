package com.unex.chamaAi.usuario.application.usecases;

import java.util.UUID;

public record UsuarioCadastrado(UUID id, String nome, String email) {
}
