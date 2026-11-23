package com.unex.chamaAi.anuncio.infrastructure.web;

import com.unex.chamaAi.anuncio.application.usecases.GerenciarFotosAnuncio;
import com.unex.chamaAi.anuncio.infrastructure.web.dto.response.AnuncioResponse;
import com.unex.chamaAi.shared.domain.exceptions.DadoInvalidoException;
import com.unex.chamaAi.shared.domain.valueobjects.Imagem;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

@RestController
public class FotoAnuncioControllerImpl implements FotoAnuncioController {

    private final GerenciarFotosAnuncio gerenciarFotos;

    public FotoAnuncioControllerImpl(GerenciarFotosAnuncio gerenciarFotos) {
        this.gerenciarFotos = gerenciarFotos;
    }

    @Override
    @Transactional
    public AnuncioResponse adicionar(Jwt jwt, UUID id, List<MultipartFile> fotos) {
        List<byte[]> arquivos = fotos == null ? List.of() : fotos.stream().map(FotoAnuncioControllerImpl::bytes).toList();
        return AnuncioResponse.de(gerenciarFotos.adicionar(id, usuarioLogado(jwt), arquivos));
    }

    @Override
    @Transactional
    public AnuncioResponse remover(Jwt jwt, UUID id, UUID fotoId) {
        return AnuncioResponse.de(gerenciarFotos.remover(id, fotoId, usuarioLogado(jwt)));
    }

    /** O conteúdo de uma foto nunca muda (trocar = remover e enviar outra): pode ficar em cache. */
    @Override
    public ResponseEntity<byte[]> exibir(Jwt jwt, UUID id, UUID fotoId) {
        Imagem imagem = gerenciarFotos.exibir(id, fotoId, jwt == null ? null : usuarioLogado(jwt));
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(imagem.contentType()))
                .cacheControl(CacheControl.maxAge(Duration.ofDays(7)).cachePrivate())
                .header("X-Content-Type-Options", "nosniff")
                .body(imagem.conteudo());
    }

    private static byte[] bytes(MultipartFile arquivo) {
        try {
            return arquivo.getBytes();
        } catch (IOException e) {
            throw new DadoInvalidoException("Não foi possível ler a imagem enviada.");
        }
    }

    private static UUID usuarioLogado(Jwt jwt) {
        return UUID.fromString(jwt.getSubject());
    }
}
