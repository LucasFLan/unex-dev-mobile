package com.unex.chamaAi.usuario.infrastructure.config;

import com.unex.chamaAi.shared.domain.exceptions.DadoInvalidoException;
import com.unex.chamaAi.usuario.application.usecases.PromoverUsuarioParaAdmin;
import com.unex.chamaAi.usuario.domain.exceptions.UsuarioNaoEncontradoException;
import com.unex.chamaAi.usuario.domain.valueobjects.Email;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Promove para ADMIN, na subida da aplicação, os usuários cujos emails estão em
 * {@code chamaai.admin.emails} (variável ADMIN_EMAILS, separada por vírgula).
 * O usuário precisa ter se cadastrado normalmente antes.
 */
@Component
public class AdminsIniciaisRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminsIniciaisRunner.class);

    private final PromoverUsuarioParaAdmin promoverUsuarioParaAdmin;
    private final String emails;

    public AdminsIniciaisRunner(PromoverUsuarioParaAdmin promoverUsuarioParaAdmin,
                                @Value("${chamaai.admin.emails:}") String emails) {
        this.promoverUsuarioParaAdmin = promoverUsuarioParaAdmin;
        this.emails = emails;
    }

    @Override
    public void run(ApplicationArguments args) {
        Arrays.stream(emails.split(","))
                .map(String::trim)
                .filter(email -> !email.isEmpty())
                .forEach(this::promover);
    }

    private void promover(String email) {
        try {
            if (promoverUsuarioParaAdmin.executar(new Email(email))) {
                log.info("Usuário {} promovido para ADMIN.", email);
            }
        } catch (UsuarioNaoEncontradoException | DadoInvalidoException e) {
            log.warn("Não foi possível promover {} para ADMIN: {}", email, e.getMessage());
        }
    }
}
