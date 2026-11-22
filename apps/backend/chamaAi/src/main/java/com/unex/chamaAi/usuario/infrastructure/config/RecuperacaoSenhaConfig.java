package com.unex.chamaAi.usuario.infrastructure.config;

import com.unex.chamaAi.usuario.application.ports.EnviadorEmail;
import com.unex.chamaAi.usuario.application.ports.GeradorCodigo;
import com.unex.chamaAi.usuario.application.usecases.RecuperarSenha;
import com.unex.chamaAi.usuario.domain.repositories.CodigoRecuperacaoSenhaRepository;
import com.unex.chamaAi.usuario.domain.repositories.UsuarioRepository;
import com.unex.chamaAi.usuario.domain.services.CriptografadorSenha;
import com.unex.chamaAi.usuario.infrastructure.email.EnviadorEmailLog;
import com.unex.chamaAi.usuario.infrastructure.email.EnviadorEmailSmtp;
import com.unex.chamaAi.usuario.infrastructure.security.GeradorCodigoAleatorio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.Clock;

@Configuration
public class RecuperacaoSenhaConfig {

    private static final Logger LOG = LoggerFactory.getLogger(RecuperacaoSenhaConfig.class);

    @Bean
    @ConditionalOnMissingBean
    public Clock relogio() {
        return Clock.systemUTC();
    }

    @Bean
    @ConditionalOnMissingBean
    public GeradorCodigo geradorCodigo() {
        return new GeradorCodigoAleatorio();
    }

    /**
     * Com MAIL_USERNAME e MAIL_PASSWORD definidos (no ambiente ou no arquivo .env), envia de verdade pelo SMTP.
     * Sem eles, só escreve o e-mail no log, para dar para testar localmente.
     */
    @Bean
    @ConditionalOnMissingBean
    public EnviadorEmail enviadorEmail(ObjectProvider<JavaMailSender> mailSender,
                                       @Value("${spring.mail.username:}") String usuario,
                                       @Value("${spring.mail.password:}") String senha) {
        JavaMailSender sender = mailSender.getIfAvailable();
        if (sender != null && !usuario.isBlank() && !senha.isBlank()) {
            LOG.info("E-mails da aplicação serão enviados por SMTP a partir de {}.", usuario);
            return new EnviadorEmailSmtp(sender, usuario);
        }
        LOG.warn("MAIL_USERNAME/MAIL_PASSWORD não definidos: os e-mails (ex.: código de recuperação de senha) "
                + "serão só escritos no log. Veja apps/backend/chamaAi/.env.example.");
        return new EnviadorEmailLog();
    }

    @Bean
    public RecuperarSenha recuperarSenha(UsuarioRepository usuarios, CodigoRecuperacaoSenhaRepository codigos,
                                         CriptografadorSenha criptografador, EnviadorEmail enviadorEmail,
                                         GeradorCodigo geradorCodigo, Clock relogio) {
        return new RecuperarSenha(usuarios, codigos, criptografador, enviadorEmail, geradorCodigo, relogio);
    }
}
