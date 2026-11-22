package com.unex.chamaAi.usuario.application.ports;

/** Envia e-mails para os usuários (implementado na infraestrutura, ex.: SMTP do Gmail). */
public interface EnviadorEmail {

    void enviar(String destinatario, String assunto, String texto);
}
