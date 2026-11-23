package com.unex.chamaAi.usuario.infrastructure.web;

import com.unex.chamaAi.usuario.application.usecases.AlterarSenhaUsuario;
import com.unex.chamaAi.usuario.application.usecases.AprovarUsuario;
import com.unex.chamaAi.usuario.application.usecases.BuscarPerfilUsuario;
import com.unex.chamaAi.usuario.application.usecases.CadastrarUsuario;
import com.unex.chamaAi.usuario.application.usecases.EditarPerfilUsuario;
import com.unex.chamaAi.usuario.application.usecases.GerenciarFotoPerfil;
import com.unex.chamaAi.usuario.application.usecases.ReprovarUsuario;
import com.unex.chamaAi.shared.domain.exceptions.DadoInvalidoException;
import com.unex.chamaAi.usuario.domain.valueobjects.FotoPerfil;
import com.unex.chamaAi.usuario.infrastructure.web.dto.request.AlterarSenhaRequest;
import com.unex.chamaAi.usuario.infrastructure.web.dto.request.CadastrarUsuarioRequest;
import com.unex.chamaAi.usuario.infrastructure.web.dto.request.EditarPerfilRequest;
import com.unex.chamaAi.usuario.infrastructure.web.dto.response.PerfilUsuarioResponse;
import com.unex.chamaAi.usuario.infrastructure.web.dto.response.UsuarioCadastradoResponse;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Duration;

import java.util.UUID;

@RestController
public class UsuarioControllerImpl implements UsuarioController {

    private final CadastrarUsuario cadastrarUsuario;
    private final BuscarPerfilUsuario buscarPerfilUsuario;
    private final EditarPerfilUsuario editarPerfilUsuario;
    private final AlterarSenhaUsuario alterarSenhaUsuario;
    private final AprovarUsuario aprovarUsuario;
    private final ReprovarUsuario reprovarUsuario;
    private final GerenciarFotoPerfil gerenciarFotoPerfil;

    public UsuarioControllerImpl(CadastrarUsuario cadastrarUsuario, BuscarPerfilUsuario buscarPerfilUsuario,
                                 EditarPerfilUsuario editarPerfilUsuario, AlterarSenhaUsuario alterarSenhaUsuario,
                                 AprovarUsuario aprovarUsuario, ReprovarUsuario reprovarUsuario,
                                 GerenciarFotoPerfil gerenciarFotoPerfil) {
        this.cadastrarUsuario = cadastrarUsuario;
        this.buscarPerfilUsuario = buscarPerfilUsuario;
        this.editarPerfilUsuario = editarPerfilUsuario;
        this.alterarSenhaUsuario = alterarSenhaUsuario;
        this.aprovarUsuario = aprovarUsuario;
        this.reprovarUsuario = reprovarUsuario;
        this.gerenciarFotoPerfil = gerenciarFotoPerfil;
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

    @Override
    @Transactional
    public PerfilUsuarioResponse alterarFoto(Jwt jwt, MultipartFile foto) {
        return PerfilUsuarioResponse.de(gerenciarFotoPerfil.alterar(usuarioLogado(jwt), bytes(foto)));
    }

    @Override
    @Transactional
    public PerfilUsuarioResponse removerFoto(Jwt jwt) {
        return PerfilUsuarioResponse.de(gerenciarFotoPerfil.remover(usuarioLogado(jwt)));
    }

    /** Foto pública entre usuários logados: cache curto e só no navegador (não em proxies compartilhados). */
    @Override
    public ResponseEntity<byte[]> foto(UUID id) {
        FotoPerfil foto = gerenciarFotoPerfil.buscar(id);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(foto.contentType()))
                .cacheControl(CacheControl.maxAge(Duration.ofMinutes(5)).cachePrivate())
                .header("X-Content-Type-Options", "nosniff")
                .body(foto.conteudo());
    }

    @Override
    public void aprovar(UUID id) {
        aprovarUsuario.executar(id);
    }

    @Override
    public void reprovar(UUID id) {
        reprovarUsuario.executar(id);
    }

    private static byte[] bytes(MultipartFile arquivo) {
        try {
            return arquivo == null ? null : arquivo.getBytes();
        } catch (IOException e) {
            throw new DadoInvalidoException("Não foi possível ler a imagem enviada.");
        }
    }

    private static UUID usuarioLogado(Jwt jwt) {
        return UUID.fromString(jwt.getSubject());
    }
}