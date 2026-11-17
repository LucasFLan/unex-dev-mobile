package com.unex.chamaAi.usuario.infrastructure.web;

import com.unex.chamaAi.usuario.application.usecases.AutenticarUsuario;
import com.unex.chamaAi.usuario.infrastructure.web.dto.request.LoginRequest;
import com.unex.chamaAi.usuario.infrastructure.web.dto.response.TokenAcessoResponse;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthControllerImpl implements AuthController {

    private final AutenticarUsuario autenticarUsuario;

    public AuthControllerImpl(AutenticarUsuario autenticarUsuario) {
        this.autenticarUsuario = autenticarUsuario;
    }

    @Override
    public TokenAcessoResponse login(LoginRequest request) {
        return TokenAcessoResponse.de(autenticarUsuario.executar(request.paraComando()));
    }
}
