package com.unex.chamaAi.usuario.application.dto;

import com.unex.chamaAi.usuario.domain.entities.Usuario;

import java.time.LocalDate;
import java.util.UUID;

/** fotoPerfil: caminho da foto na API (ver {@link FotoPerfilUrl}); nulo se o usuário não tem foto. */
public record PerfilUsuario(UUID id, String nome, LocalDate dataNascimento, String cpf, String email,
                            String telefone, boolean verificado, String papel, String statusValidacao,
                            String fotoPerfil) {

    public static PerfilUsuario de(Usuario usuario) {
        return de(usuario, null);
    }

    public static PerfilUsuario de(Usuario usuario, String fotoPerfil) {
        return new PerfilUsuario(usuario.getId(), usuario.getNome(), usuario.getDataNascimento(),
                usuario.getCpf().valor(), usuario.getEmail().valor(), usuario.getTelefone(), usuario.isVerificado(),
                usuario.getPapel().name(), usuario.getStatusValidacao().name(), fotoPerfil);
    }
}
