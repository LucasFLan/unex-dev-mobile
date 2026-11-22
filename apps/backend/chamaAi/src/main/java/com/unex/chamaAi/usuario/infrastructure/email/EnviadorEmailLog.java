package com.unex.chamaAi.usuario.infrastructure.email;

import com.unex.chamaAi.usuario.application.ports.EnviadorEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Usado quando MAIL_USERNAME/MAIL_PASSWORD não estão configurados: em vez de enviar, escreve o e-mail no
 * log da aplicação. Serve para desenvolver e testar localmente sem conta de e-mail.
 */
public class EnviadorEmailLog implements EnviadorEmail {

    private static final Logger LOG = LoggerFactory.getLogger(EnviadorEmailLog.class);

    @Override
    public void enviar(String destinatario, String assunto, String texto) {
        LOG.warn("""
                E-mail NÃO enviado (MAIL_USERNAME/MAIL_PASSWORD não configurados). Conteúdo:
                Para: {}
                Assunto: {}
                {}""", destinatario, assunto, texto);
    }
}
