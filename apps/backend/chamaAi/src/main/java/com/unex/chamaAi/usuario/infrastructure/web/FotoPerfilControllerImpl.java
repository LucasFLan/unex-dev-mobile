package com.unex.chamaAi.usuario.infrastructure.web;

import com.unex.chamaAi.shared.domain.exceptions.DadoInvalidoException;
import com.unex.chamaAi.shared.domain.valueobjects.Imagem;
import com.unex.chamaAi.usuario.application.usecases.GerenciarFotoPerfil;
import com.unex.chamaAi.usuario.infrastructure.web.dto.response.PerfilUsuarioResponse;
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
public class FotoPerfilControllerImpl implements FotoPerfilController {

    private final GerenciarFotoPerfil gerenciarFotoPerfil;

    public FotoPerfilControllerImpl(GerenciarFotoPerfil gerenciarFotoPerfil) {
        this.gerenciarFotoPerfil = gerenciarFotoPerfil;
    }

    @Override
    @Transactional
    public PerfilUsuarioResponse enviar(Jwt jwt, MultipartFile foto) {
        return PerfilUsuarioResponse.de(gerenciarFotoPerfil.enviar(usuarioLogado(jwt), bytes(foto)));
    }

    @Override
    @Transactional
    public PerfilUsuarioResponse remover(Jwt jwt) {
        return PerfilUsuarioResponse.de(gerenciarFotoPerfil.remover(usuarioLogado(jwt)));
    }

    /** Cache curto: o link com "?v=" muda quando a foto é trocada, mas o caminho sem "v" também funciona. */
    @Override
    public ResponseEntity<byte[]> exibir(UUID id) {
        Imagem imagem = gerenciarFotoPerfil.exibir(id);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(imagem.contentType()))
                .cacheControl(CacheControl.maxAge(Duration.ofHours(1)).cachePublic())
                .header("X-Content-Type-Options", "nosniff")
                .body(imagem.conteudo());
    }

    static byte[] bytes(MultipartFile arquivo) {
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
