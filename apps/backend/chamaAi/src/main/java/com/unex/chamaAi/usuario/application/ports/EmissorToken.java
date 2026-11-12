package com.unex.chamaAi.usuario.application.ports;

import com.unex.chamaAi.usuario.application.dto.TokenAcesso;
import com.unex.chamaAi.usuario.domain.entities.Usuario;

/**
 * Porta para emitir o token de acesso de um usuário autenticado.
 * A implementação (JWT) fica na infraestrutura.
 */
public interface EmissorToken {

    TokenAcesso emitir(Usuario usuario);
}
