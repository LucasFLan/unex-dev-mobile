package com.unex.chamaAi.usuario.infrastructure.web;

import com.unex.chamaAi.usuario.application.usecases.CadastrarUsuario;
import com.unex.chamaAi.usuario.application.usecases.UsuarioCadastrado;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final CadastrarUsuario cadastrarUsuario;

    public UsuarioController(CadastrarUsuario cadastrarUsuario) {
        this.cadastrarUsuario = cadastrarUsuario;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioCadastrado cadastrar(@RequestBody CadastrarUsuarioRequest request) {
        return cadastrarUsuario.executar(request.paraComando());
    }
}
