package com.unex.chamaAi.usuario.infrastructure.web;

import com.unex.chamaAi.usuario.application.dto.PerfilUsuario;
import com.unex.chamaAi.usuario.application.dto.UsuarioCadastrado;
import com.unex.chamaAi.usuario.application.usecases.AlterarSenhaUsuario;
import com.unex.chamaAi.usuario.application.usecases.BuscarPerfilUsuario;
import com.unex.chamaAi.usuario.application.usecases.CadastrarUsuario;
import com.unex.chamaAi.usuario.application.usecases.EditarPerfilUsuario;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

// TODO: quando o Spring Security entrar, trocar /{id} por /me (id vindo do usuário autenticado).
@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final CadastrarUsuario cadastrarUsuario;
    private final BuscarPerfilUsuario buscarPerfilUsuario;
    private final EditarPerfilUsuario editarPerfilUsuario;
    private final AlterarSenhaUsuario alterarSenhaUsuario;

    public UsuarioController(CadastrarUsuario cadastrarUsuario, BuscarPerfilUsuario buscarPerfilUsuario,
                             EditarPerfilUsuario editarPerfilUsuario, AlterarSenhaUsuario alterarSenhaUsuario) {
        this.cadastrarUsuario = cadastrarUsuario;
        this.buscarPerfilUsuario = buscarPerfilUsuario;
        this.editarPerfilUsuario = editarPerfilUsuario;
        this.alterarSenhaUsuario = alterarSenhaUsuario;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioCadastrado cadastrar(@RequestBody CadastrarUsuarioRequest request) {
        return cadastrarUsuario.executar(request.paraComando());
    }

    @GetMapping("/{id}")
    public PerfilUsuario visualizarPerfil(@PathVariable UUID id) {
        return buscarPerfilUsuario.executar(id);
    }

    @PutMapping("/{id}")
    public PerfilUsuario editarPerfil(@PathVariable UUID id, @RequestBody EditarPerfilRequest request) {
        return editarPerfilUsuario.executar(id, request.paraComando());
    }

    @PatchMapping("/{id}/senha")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void alterarSenha(@PathVariable UUID id, @RequestBody AlterarSenhaRequest request) {
        alterarSenhaUsuario.executar(id, request.paraComando());
    }
}
