package com.unex.chamaAi.usuario.infrastructure.config;

import com.unex.chamaAi.usuario.application.ports.NotificadorValidacao;
import com.unex.chamaAi.usuario.application.usecases.AnalisarValidacao;
import com.unex.chamaAi.usuario.application.usecases.ValidarConta;
import com.unex.chamaAi.usuario.domain.repositories.EnvioValidacaoRepository;
import com.unex.chamaAi.usuario.domain.repositories.UsuarioRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class ValidacaoContaConfig {

    @Bean
    public ValidarConta validarConta(UsuarioRepository usuarios, EnvioValidacaoRepository envios, Clock relogio) {
        return new ValidarConta(usuarios, envios, relogio);
    }

    @Bean
    public AnalisarValidacao analisarValidacao(UsuarioRepository usuarios, EnvioValidacaoRepository envios,
                                               NotificadorValidacao notificador, Clock relogio) {
        return new AnalisarValidacao(usuarios, envios, notificador, relogio);
    }
}
