package com.unex.chamaAi.proposta.infrastructure.web;

import com.unex.chamaAi.proposta.application.usecases.AvaliarServico;
import com.unex.chamaAi.proposta.application.usecases.ListarAvaliacoes;
import com.unex.chamaAi.proposta.infrastructure.web.dto.request.AvaliacaoRequest;
import com.unex.chamaAi.proposta.infrastructure.web.dto.response.AvaliacaoResponse;
import com.unex.chamaAi.proposta.infrastructure.web.dto.response.AvaliacoesDoUsuarioResponse;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class AvaliacaoControllerImpl implements AvaliacaoController {

    private final AvaliarServico avaliarServico;
    private final ListarAvaliacoes listarAvaliacoes;

    public AvaliacaoControllerImpl(AvaliarServico avaliarServico, ListarAvaliacoes listarAvaliacoes) {
        this.avaliarServico = avaliarServico;
        this.listarAvaliacoes = listarAvaliacoes;
    }

    /** Avaliação e notificação são gravadas juntas. */
    @Override
    @Transactional
    public AvaliacaoResponse avaliar(Jwt jwt, UUID id, AvaliacaoRequest request) {
        return AvaliacaoResponse.de(avaliarServico.executar(id, UUID.fromString(jwt.getSubject()), request.nota(),
                request.comentario()));
    }

    @Override
    public AvaliacoesDoUsuarioResponse listar(UUID id, Integer pagina, Integer tamanho) {
        return AvaliacoesDoUsuarioResponse.de(listarAvaliacoes.doUsuario(id, pagina, tamanho));
    }
}
