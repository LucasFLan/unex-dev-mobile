package com.unex.chamaAi.usuario.application.ports;

import java.util.UUID;

/** Avisa o usuário do resultado da análise dos documentos. */
public interface NotificadorValidacao {

    void contaAprovada(UUID usuarioId);

    void contaReprovada(UUID usuarioId, String motivo);
}
