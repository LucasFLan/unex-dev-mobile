package com.unex.chamaAi.proposta.infrastructure.config;

import com.unex.chamaAi.anuncio.domain.repositories.AnuncioRepository;
import com.unex.chamaAi.proposta.application.ports.NotificadorProposta;
import com.unex.chamaAi.anuncio.domain.repositories.CategoriaRepository;
import com.unex.chamaAi.proposta.application.usecases.AvaliarServico;
import com.unex.chamaAi.proposta.application.usecases.EnviarProposta;
import com.unex.chamaAi.proposta.application.usecases.ListarAvaliacoes;
import com.unex.chamaAi.proposta.application.usecases.ListarPropostas;
import com.unex.chamaAi.proposta.application.usecases.ResponderProposta;
import com.unex.chamaAi.proposta.domain.repositories.AvaliacaoRepository;
import com.unex.chamaAi.proposta.domain.repositories.FotoPropostaRepository;
import com.unex.chamaAi.proposta.domain.repositories.PropostaRepository;
import com.unex.chamaAi.usuario.domain.repositories.UsuarioRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PropostaConfig {

    @Bean
    public EnviarProposta enviarProposta(PropostaRepository propostas, AnuncioRepository anuncios,
                                         UsuarioRepository usuarios, FotoPropostaRepository fotos,
                                         NotificadorProposta notificador) {
        return new EnviarProposta(propostas, anuncios, usuarios, fotos, notificador);
    }

    @Bean
    public ResponderProposta responderProposta(PropostaRepository propostas, AnuncioRepository anuncios,
                                               UsuarioRepository usuarios, FotoPropostaRepository fotos,
                                               AvaliacaoRepository avaliacoes, NotificadorProposta notificador) {
        return new ResponderProposta(propostas, anuncios, usuarios, fotos, avaliacoes, notificador);
    }

    @Bean
    public ListarPropostas listarPropostas(PropostaRepository propostas, AnuncioRepository anuncios,
                                           UsuarioRepository usuarios, FotoPropostaRepository fotos,
                                           AvaliacaoRepository avaliacoes) {
        return new ListarPropostas(propostas, anuncios, usuarios, fotos, avaliacoes);
    }

    @Bean
    public AvaliarServico avaliarServico(PropostaRepository propostas, AnuncioRepository anuncios,
                                         CategoriaRepository categorias, UsuarioRepository usuarios,
                                         AvaliacaoRepository avaliacoes, NotificadorProposta notificador) {
        return new AvaliarServico(propostas, anuncios, categorias, usuarios, avaliacoes, notificador);
    }

    @Bean
    public ListarAvaliacoes listarAvaliacoes(AvaliacaoRepository avaliacoes, UsuarioRepository usuarios) {
        return new ListarAvaliacoes(avaliacoes, usuarios);
    }
}
