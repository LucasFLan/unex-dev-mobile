package com.unex.chamaAi.notificacao.infrastructure.web;

import com.unex.chamaAi.notificacao.application.usecases.ListarNotificacoes;
import com.unex.chamaAi.notificacao.application.usecases.MarcarNotificacoesComoLidas;
import com.unex.chamaAi.notificacao.infrastructure.web.dto.response.ContagemNotificacoesResponse;
import com.unex.chamaAi.notificacao.infrastructure.web.dto.response.PaginaNotificacoesResponse;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class NotificacaoControllerImpl implements NotificacaoController {

    private final ListarNotificacoes listarNotificacoes;
    private final MarcarNotificacoesComoLidas marcarComoLidas;

    public NotificacaoControllerImpl(ListarNotificacoes listarNotificacoes,
                                     MarcarNotificacoesComoLidas marcarComoLidas) {
        this.listarNotificacoes = listarNotificacoes;
        this.marcarComoLidas = marcarComoLidas;
    }

    @Override
    public PaginaNotificacoesResponse listar(Jwt jwt, Integer pagina, Integer tamanho) {
        return PaginaNotificacoesResponse.de(listarNotificacoes.executar(usuarioLogado(jwt), pagina, tamanho));
    }

    @Override
    public ContagemNotificacoesResponse contarNaoLidas(Jwt jwt) {
        return new ContagemNotificacoesResponse(listarNotificacoes.contarNaoLidas(usuarioLogado(jwt)));
    }

    @Override
    public void marcarTodasComoLidas(Jwt jwt) {
        marcarComoLidas.todas(usuarioLogado(jwt));
    }

    @Override
    public void marcarComoLida(Jwt jwt, UUID id) {
        marcarComoLidas.uma(id, usuarioLogado(jwt));
    }

    /** O "sub" do token é o id do usuário (ver JwtEmissorToken). */
    private static UUID usuarioLogado(Jwt jwt) {
        return UUID.fromString(jwt.getSubject());
    }
}
