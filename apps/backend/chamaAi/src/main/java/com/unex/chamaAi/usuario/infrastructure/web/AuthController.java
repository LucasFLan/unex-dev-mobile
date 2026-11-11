package com.unex.chamaAi.usuario.infrastructure.web;

import com.unex.chamaAi.usuario.application.dto.TokenAcesso;
import com.unex.chamaAi.usuario.application.usecases.AutenticarUsuario;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AutenticarUsuario autenticarUsuario;

    public AuthController(AutenticarUsuario autenticarUsuario) {
        this.autenticarUsuario = autenticarUsuario;
    }

    @PostMapping("/login")
    public TokenAcesso login(@RequestBody LoginRequest request) {
        return autenticarUsuario.executar(request.paraComando());
    }
}
