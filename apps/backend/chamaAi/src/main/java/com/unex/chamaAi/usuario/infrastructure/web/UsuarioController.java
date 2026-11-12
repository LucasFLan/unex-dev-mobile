package com.unex.chamaAi.usuario.infrastructure.web;

import com.unex.chamaAi.usuario.application.dto.PerfilUsuario;
import com.unex.chamaAi.usuario.application.dto.UsuarioCadastrado;
import com.unex.chamaAi.usuario.application.usecases.AlterarSenhaUsuario;
import com.unex.chamaAi.usuario.application.usecases.BuscarPerfilUsuario;
import com.unex.chamaAi.usuario.application.usecases.CadastrarUsuario;
import com.unex.chamaAi.usuario.application.usecases.EditarPerfilUsuario;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

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

    @GetMapping("/me")
    public PerfilUsuario visualizarPerfil(@AuthenticationPrincipal Jwt jwt) {
        return buscarPerfilUsuario.executar(usuarioLogado(jwt));
    }

    @PutMapping("/me")
    public PerfilUsuario editarPerfil(@AuthenticationPrincipal Jwt jwt, @RequestBody EditarPerfilRequest request) {
        return editarPerfilUsuario.executar(usuarioLogado(jwt), request.paraComando());
    }

    @PatchMapping("/me/senha")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void alterarSenha(@AuthenticationPrincipal Jwt jwt, @RequestBody AlterarSenhaRequest request) {
        alterarSenhaUsuario.executar(usuarioLogado(jwt), request.paraComando());
    }

    /** O "sub" do token é o id do usuário (ver JwtEmissorToken). */
    private static UUID usuarioLogado(Jwt jwt) {
        return UUID.fromString(jwt.getSubject());
    }
}
