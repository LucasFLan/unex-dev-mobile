package com.unex.chamaAi.usuario.infrastructure.email;

import com.unex.chamaAi.usuario.application.ports.EnviadorEmail;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

/** Envia pelo SMTP configurado em spring.mail.* (ex.: Gmail com senha de app). */
public class EnviadorEmailSmtp implements EnviadorEmail {

    private static final String NOME_REMETENTE = "ChamaAí";

    private final JavaMailSender mailSender;
    private final String remetente;

    public EnviadorEmailSmtp(JavaMailSender mailSender, String remetente) {
        this.mailSender = mailSender;
        this.remetente = remetente;
    }

    @Override
    public void enviar(String destinatario, String assunto, String texto) {
        try {
            MimeMessage mensagem = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensagem, StandardCharsets.UTF_8.name());
            helper.setFrom(remetente, NOME_REMETENTE);
            helper.setTo(destinatario);
            helper.setSubject(assunto);
            helper.setText(texto);
            mailSender.send(mensagem);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new MailSendException("Não foi possível montar o e-mail.", e);
        }
    }
}
