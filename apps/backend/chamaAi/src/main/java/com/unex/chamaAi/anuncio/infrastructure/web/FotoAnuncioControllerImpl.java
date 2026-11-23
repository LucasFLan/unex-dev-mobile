package com.unex.chamaAi.anuncio.infrastructure.web;

import com.unex.chamaAi.anuncio.application.usecases.GerenciarFotosAnuncio;
import com.unex.chamaAi.anuncio.domain.valueobjects.FotoAnuncio;
import com.unex.chamaAi.anuncio.infrastructure.web.dto.response.AnuncioResponse;
import com.unex.chamaAi.shared.domain.exceptions.DadoInvalidoException;
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
        List<byte[]> arquivos = fotos == null ? List.of()
                : fotos.stream().filter(foto -> foto != null && !foto.isEmpty()).map(FotoAnuncioControllerImpl::bytes)
                .toList();
        UUID autor = usuarioLogado(jwt);
        return AnuncioResponse.de(gerenciarFotos.adicionar(id, autor, arquivos), autor);
    }

    @Override
    @Transactional
    public AnuncioResponse remover(Jwt jwt, UUID id, UUID fotoId) {
        UUID autor = usuarioLogado(jwt);
        return AnuncioResponse.de(gerenciarFotos.remover(id, fotoId, autor), autor);
    }

    /** As fotos não mudam depois de enviadas: o navegador pode guardar por um bom tempo. */
    @Override
    public ResponseEntity<byte[]> exibir(Jwt jwt, UUID id, UUID fotoId) {
        FotoAnuncio foto = gerenciarFotos.exibir(id, fotoId, jwt == null ? null : usuarioLogado(jwt));
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(foto.contentType()))
                .cacheControl(CacheControl.maxAge(Duration.ofDays(7)).cachePrivate())
                .header("X-Content-Type-Options", "nosniff")
                .body(foto.conteudo());
    }

    private static byte[] bytes(MultipartFile arquivo) {
        try {
            return arquivo.getBytes();
        } catch (IOException e) {
            throw new DadoInvalidoException("Não foi possível ler uma das fotos enviadas.");
        }
    }

    private static UUID usuarioLogado(Jwt jwt) {
        return UUID.fromString(jwt.getSubject());
    }
}
