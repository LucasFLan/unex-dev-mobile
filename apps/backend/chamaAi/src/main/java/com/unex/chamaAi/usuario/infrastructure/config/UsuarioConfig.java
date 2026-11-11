package com.unex.chamaAi.usuario.infrastructure.config;

import com.unex.chamaAi.usuario.application.ports.EmissorToken;
import com.unex.chamaAi.usuario.application.usecases.AlterarSenhaUsuario;
import com.unex.chamaAi.usuario.application.usecases.AutenticarUsuario;
import com.unex.chamaAi.usuario.application.usecases.BuscarPerfilUsuario;
import com.unex.chamaAi.usuario.application.usecases.CadastrarUsuario;
import com.unex.chamaAi.usuario.application.usecases.EditarPerfilUsuario;
import com.unex.chamaAi.usuario.application.usecases.PromoverUsuarioParaAdmin;
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

    @Bean
    public BuscarPerfilUsuario buscarPerfilUsuario(UsuarioRepository usuarios) {
        return new BuscarPerfilUsuario(usuarios);
    }

    @Bean
    public EditarPerfilUsuario editarPerfilUsuario(UsuarioRepository usuarios) {
        return new EditarPerfilUsuario(usuarios);
    }

    @Bean
    public AlterarSenhaUsuario alterarSenhaUsuario(UsuarioRepository usuarios, CriptografadorSenha criptografador) {
        return new AlterarSenhaUsuario(usuarios, criptografador);
    }

    @Bean
    public PromoverUsuarioParaAdmin promoverUsuarioParaAdmin(UsuarioRepository usuarios) {
        return new PromoverUsuarioParaAdmin(usuarios);
    }

    @Bean
    public AutenticarUsuario autenticarUsuario(UsuarioRepository usuarios, CriptografadorSenha criptografador,
                                               EmissorToken emissorToken) {
        return new AutenticarUsuario(usuarios, criptografador, emissorToken);
    }
}
