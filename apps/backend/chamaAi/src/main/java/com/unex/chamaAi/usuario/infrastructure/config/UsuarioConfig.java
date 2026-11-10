package com.unex.chamaAi.usuario.infrastructure.config;

import com.unex.chamaAi.usuario.application.usecases.CadastrarUsuario;
import com.unex.chamaAi.usuario.domain.repositories.UsuarioRepository;
import com.unex.chamaAi.usuario.domain.services.CriptografadorSenha;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UsuarioConfig {

    @Bean
    public CadastrarUsuario cadastrarUsuario(UsuarioRepository usuarios, CriptografadorSenha criptografador) {
        return new CadastrarUsuario(usuarios, criptografador);
    }
}
