package com.unex.chamaAi.usuario.infrastructure.web;

import com.unex.chamaAi.usuario.application.usecases.AutenticarUsuario;
import com.unex.chamaAi.usuario.application.usecases.RecuperarSenha;
import com.unex.chamaAi.usuario.infrastructure.web.dto.request.LoginRequest;
import com.unex.chamaAi.usuario.infrastructure.web.dto.request.RedefinirSenhaRequest;
import com.unex.chamaAi.usuario.infrastructure.web.dto.request.SolicitarRecuperacaoSenhaRequest;
import com.unex.chamaAi.usuario.infrastructure.web.dto.request.ValidarCodigoRecuperacaoRequest;
import com.unex.chamaAi.usuario.infrastructure.web.dto.response.TokenAcessoResponse;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthControllerImpl implements AuthController {

    private final AutenticarUsuario autenticarUsuario;
    private final RecuperarSenha recuperarSenha;

    public AuthControllerImpl(AutenticarUsuario autenticarUsuario, RecuperarSenha recuperarSenha) {
        this.autenticarUsuario = autenticarUsuario;
        this.recuperarSenha = recuperarSenha;
    }

    @Override
    public TokenAcessoResponse login(LoginRequest request) {
        return TokenAcessoResponse.de(autenticarUsuario.executar(request.paraComando()));
    }

    @Override
    public void solicitarRecuperacaoSenha(SolicitarRecuperacaoSenhaRequest request) {
        recuperarSenha.solicitar(request.email());
    }

    @Override
    public void validarCodigoRecuperacao(ValidarCodigoRecuperacaoRequest request) {
        recuperarSenha.validar(request.email(), request.codigo());
    }

    @Override
    public void redefinirSenha(RedefinirSenhaRequest request) {
        recuperarSenha.redefinir(request.email(), request.codigo(), request.novaSenha());
    }
}
