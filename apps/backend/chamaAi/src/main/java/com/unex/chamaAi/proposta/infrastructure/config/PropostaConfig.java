package com.unex.chamaAi.proposta.infrastructure.config;

import com.unex.chamaAi.anuncio.domain.repositories.AnuncioRepository;
import com.unex.chamaAi.proposta.application.ports.NotificadorProposta;
import com.unex.chamaAi.proposta.application.usecases.EnviarProposta;
import com.unex.chamaAi.proposta.application.usecases.ListarPropostas;
import com.unex.chamaAi.proposta.application.usecases.ResponderProposta;
import com.unex.chamaAi.proposta.domain.repositories.PropostaRepository;
import com.unex.chamaAi.usuario.domain.repositories.UsuarioRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PropostaConfig {

    @Bean
    public EnviarProposta enviarProposta(PropostaRepository propostas, AnuncioRepository anuncios,
                                         UsuarioRepository usuarios, NotificadorProposta notificador) {
        return new EnviarProposta(propostas, anuncios, usuarios, notificador);
    }

    @Bean
    public ResponderProposta responderProposta(PropostaRepository propostas, AnuncioRepository anuncios,
                                               UsuarioRepository usuarios, NotificadorProposta notificador) {
        return new ResponderProposta(propostas, anuncios, usuarios, notificador);
    }

    @Bean
    public ListarPropostas listarPropostas(PropostaRepository propostas, AnuncioRepository anuncios,
                                           UsuarioRepository usuarios) {
        return new ListarPropostas(propostas, anuncios, usuarios);
    }
}
