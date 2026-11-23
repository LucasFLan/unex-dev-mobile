package com.unex.chamaAi.usuario.infrastructure.config;

import com.unex.chamaAi.usuario.application.ports.EmissorToken;
import com.unex.chamaAi.usuario.application.usecases.AlterarSenhaUsuario;
import com.unex.chamaAi.usuario.application.usecases.AprovarUsuario;
import com.unex.chamaAi.usuario.application.usecases.AutenticarUsuario;
import com.unex.chamaAi.usuario.application.usecases.BuscarPerfilUsuario;
import com.unex.chamaAi.usuario.application.usecases.CadastrarUsuario;
import com.unex.chamaAi.usuario.application.usecases.EditarPerfilUsuario;
import com.unex.chamaAi.usuario.application.usecases.GerenciarFotoPerfil;
import com.unex.chamaAi.usuario.application.usecases.PromoverUsuarioParaAdmin;
import com.unex.chamaAi.usuario.application.usecases.ReprovarUsuario;
import com.unex.chamaAi.usuario.domain.repositories.FotoPerfilRepository;
import com.unex.chamaAi.usuario.domain.repositories.UsuarioRepository;
import com.unex.chamaAi.usuario.domain.services.CriptografadorSenha;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class UsuarioConfig {

    @Bean
    public CadastrarUsuario cadastrarUsuario(UsuarioRepository usuarios, CriptografadorSenha criptografador) {
        return new CadastrarUsuario(usuarios, criptografador);
    }

    @Bean
    public BuscarPerfilUsuario buscarPerfilUsuario(UsuarioRepository usuarios, FotoPerfilRepository fotos) {
        return new BuscarPerfilUsuario(usuarios, fotos);
    }

    @Bean
    public EditarPerfilUsuario editarPerfilUsuario(UsuarioRepository usuarios, FotoPerfilRepository fotos) {
        return new EditarPerfilUsuario(usuarios, fotos);
    }

    @Bean
    public GerenciarFotoPerfil gerenciarFotoPerfil(UsuarioRepository usuarios, FotoPerfilRepository fotos, Clock relogio) {
        return new GerenciarFotoPerfil(usuarios, fotos, relogio);
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
    public AprovarUsuario aprovarUsuario(UsuarioRepository usuarios) {
        return new AprovarUsuario(usuarios);
    }

    @Bean
    public ReprovarUsuario reprovarUsuario(UsuarioRepository usuarios) {
        return new ReprovarUsuario(usuarios);
    }

    @Bean
    public AutenticarUsuario autenticarUsuario(UsuarioRepository usuarios, CriptografadorSenha criptografador,
                                               EmissorToken emissorToken) {
        return new AutenticarUsuario(usuarios, criptografador, emissorToken);
    }
}