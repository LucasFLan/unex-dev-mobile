package com.unex.chamaAi.proposta.infrastructure.web;

import com.unex.chamaAi.proposta.application.usecases.EnviarProposta;
import com.unex.chamaAi.proposta.application.usecases.ListarPropostas;
import com.unex.chamaAi.proposta.application.usecases.ResponderProposta;
import com.unex.chamaAi.proposta.infrastructure.web.dto.request.PropostaRequest;
import com.unex.chamaAi.proposta.infrastructure.web.dto.response.PaginaPropostasResponse;
import com.unex.chamaAi.proposta.infrastructure.web.dto.response.PropostaResponse;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class PropostaControllerImpl implements PropostaController {

    private final EnviarProposta enviarProposta;
    private final ListarPropostas listarPropostas;
    private final ResponderProposta responderProposta;

    public PropostaControllerImpl(EnviarProposta enviarProposta, ListarPropostas listarPropostas,
                                  ResponderProposta responderProposta) {
        this.enviarProposta = enviarProposta;
        this.listarPropostas = listarPropostas;
        this.responderProposta = responderProposta;
    }

    /** Proposta e notificação são gravadas juntas: se uma falhar, nenhuma fica. */
    @Override
    @Transactional
    public PropostaResponse enviar(Jwt jwt, UUID id, PropostaRequest request) {
        return PropostaResponse.de(enviarProposta.executar(id, usuarioLogado(jwt), request.paraComando()));
    }

    @Override
    public PaginaPropostasResponse listarRecebidas(Jwt jwt, Integer pagina, Integer tamanho) {
        return PaginaPropostasResponse.de(listarPropostas.recebidas(usuarioLogado(jwt), pagina, tamanho));
    }

    @Override
    public PaginaPropostasResponse listarEnviadas(Jwt jwt, Integer pagina, Integer tamanho) {
        return PaginaPropostasResponse.de(listarPropostas.enviadas(usuarioLogado(jwt), pagina, tamanho));
    }

    /** Aceitar mexe na proposta, no anúncio, nas outras propostas e nas notificações: tudo ou nada. */
    @Override
    @Transactional
    public PropostaResponse aceitar(Jwt jwt, UUID id) {
        return PropostaResponse.de(responderProposta.aceitar(id, usuarioLogado(jwt)));
    }

    @Override
    @Transactional
    public PropostaResponse recusar(Jwt jwt, UUID id) {
        return PropostaResponse.de(responderProposta.recusar(id, usuarioLogado(jwt)));
    }

    @Override
    @Transactional
    public PropostaResponse cancelar(Jwt jwt, UUID id) {
        return PropostaResponse.de(responderProposta.cancelar(id, usuarioLogado(jwt)));
    }

    /** O "sub" do token é o id do usuário (ver JwtEmissorToken). */
    private static UUID usuarioLogado(Jwt jwt) {
        return UUID.fromString(jwt.getSubject());
    }
}
