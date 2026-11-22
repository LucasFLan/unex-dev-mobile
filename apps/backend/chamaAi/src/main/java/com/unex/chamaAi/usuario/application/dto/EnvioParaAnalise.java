package com.unex.chamaAi.usuario.application.dto;

import com.unex.chamaAi.usuario.domain.entities.EnvioValidacao;
import com.unex.chamaAi.usuario.domain.entities.Usuario;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/** Envio de documentos com os dados do cadastro, para o admin comparar com as imagens. */
public record EnvioParaAnalise(UUID id, Solicitante usuario, String status, Instant enviadoEm, Instant analisadoEm,
                               String motivoReprovacao) {

    public record Solicitante(UUID id, String nome, String email, String cpf, LocalDate dataNascimento,
                              String telefone) {
    }

    public static EnvioParaAnalise de(EnvioValidacao envio, Usuario usuario) {
        Solicitante solicitante = usuario == null ? null
                : new Solicitante(usuario.getId(), usuario.getNome(), usuario.getEmail().valor(),
                usuario.getCpf().valor(), usuario.getDataNascimento(), usuario.getTelefone());
        return new EnvioParaAnalise(envio.getId(), solicitante, envio.getStatus().name(), envio.getEnviadoEm(),
                envio.getAnalisadoEm().orElse(null), envio.getMotivoReprovacao().orElse(null));
    }
}
