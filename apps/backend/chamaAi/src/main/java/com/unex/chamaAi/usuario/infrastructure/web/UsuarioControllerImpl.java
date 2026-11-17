package com.unex.chamaAi.usuario.infrastructure.web;

import com.unex.chamaAi.usuario.application.usecases.AlterarSenhaUsuario;
import com.unex.chamaAi.usuario.application.usecases.BuscarPerfilUsuario;
import com.unex.chamaAi.usuario.application.usecases.CadastrarUsuario;
import com.unex.chamaAi.usuario.application.usecases.EditarPerfilUsuario;
import com.unex.chamaAi.usuario.infrastructure.web.dto.request.AlterarSenhaRequest;
import com.unex.chamaAi.usuario.infrastructure.web.dto.request.CadastrarUsuarioRequest;
import com.unex.chamaAi.usuario.infrastructure.web.dto.request.EditarPerfilRequest;
import com.unex.chamaAi.usuario.infrastructure.web.dto.response.PerfilUsuarioResponse;
import com.unex.chamaAi.usuario.infrastructure.web.dto.response.UsuarioCadastradoResponse;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class UsuarioControllerImpl implements UsuarioController {

    private final CadastrarUsuario cadastrarUsuario;
    private final BuscarPerfilUsuario buscarPerfilUsuario;
    private final EditarPerfilUsuario editarPerfilUsuario;
    private final AlterarSenhaUsuario alterarSenhaUsuario;

    public UsuarioControllerImpl(CadastrarUsuario cadastrarUsuario, BuscarPerfilUsuario buscarPerfilUsuario,
                                 EditarPerfilUsuario editarPerfilUsuario, AlterarSenhaUsuario alterarSenhaUsuario) {
        this.cadastrarUsuario = cadastrarUsuario;
        this.buscarPerfilUsuario = buscarPerfilUsuario;
        this.editarPerfilUsuario = editarPerfilUsuario;
        this.alterarSenhaUsuario = alterarSenhaUsuario;
    }

    @Override
    public UsuarioCadastradoResponse cadastrar(CadastrarUsuarioRequest request) {
        return UsuarioCadastradoResponse.de(cadastrarUsuario.executar(request.paraComando()));
    }

    @Override
    public PerfilUsuarioResponse visualizarPerfil(Jwt jwt) {
        return PerfilUsuarioResponse.de(buscarPerfilUsuario.executar(usuarioLogado(jwt)));
    }

    @Override
    public PerfilUsuarioResponse editarPerfil(Jwt jwt, EditarPerfilRequest request) {
        return PerfilUsuarioResponse.de(editarPerfilUsuario.executar(usuarioLogado(jwt), request.paraComando()));
    }

    @Override
    public void alterarSenha(Jwt jwt, AlterarSenhaRequest request) {
        alterarSenhaUsuario.executar(usuarioLogado(jwt), request.paraComando());
    }

    /** O "sub" do token é o id do usuário (ver JwtEmissorToken). */
    private static UUID usuarioLogado(Jwt jwt) {
        return UUID.fromString(jwt.getSubject());
    }
}
