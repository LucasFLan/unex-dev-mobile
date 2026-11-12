package com.unex.chamaAi.usuario.application.dto;

import com.unex.chamaAi.usuario.domain.entities.Usuario;

import java.time.LocalDate;
import java.util.UUID;

public record PerfilUsuario(UUID id, String nome, LocalDate dataNascimento, String cpf, String email,
                            String telefone, boolean verificado, String papel) {

    public static PerfilUsuario de(Usuario usuario) {
        return new PerfilUsuario(usuario.getId(), usuario.getNome(), usuario.getDataNascimento(),
                usuario.getCpf().valor(), usuario.getEmail().valor(), usuario.getTelefone(), usuario.isVerificado(),
                usuario.getPapel().name());
    }
}
