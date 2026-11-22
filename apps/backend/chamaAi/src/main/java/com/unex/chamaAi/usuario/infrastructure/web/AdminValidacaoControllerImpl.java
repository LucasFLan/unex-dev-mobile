package com.unex.chamaAi.usuario.infrastructure.web;

import com.unex.chamaAi.usuario.application.usecases.AnalisarValidacao;
import com.unex.chamaAi.usuario.domain.valueobjects.ArquivoValidacao;
import com.unex.chamaAi.usuario.domain.valueobjects.TipoArquivoValidacao;
import com.unex.chamaAi.usuario.infrastructure.web.dto.request.ReprovarValidacaoRequest;
import com.unex.chamaAi.usuario.infrastructure.web.dto.response.EnvioValidacaoResponse;
import com.unex.chamaAi.usuario.infrastructure.web.dto.response.PaginaEnviosValidacaoResponse;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/** O acesso a /admin/** já é restrito ao papel ADMIN no SecurityConfig. */
@RestController
public class AdminValidacaoControllerImpl implements AdminValidacaoController {

    private final AnalisarValidacao analisarValidacao;

    public AdminValidacaoControllerImpl(AnalisarValidacao analisarValidacao) {
        this.analisarValidacao = analisarValidacao;
    }

    @Override
    public PaginaEnviosValidacaoResponse listar(String status, Integer pagina, Integer tamanho) {
        return PaginaEnviosValidacaoResponse.de(analisarValidacao.listar(status, pagina, tamanho));
    }

    @Override
    public EnvioValidacaoResponse detalhar(UUID id) {
        return EnvioValidacaoResponse.de(analisarValidacao.detalhar(id));
    }

    /** Documento pessoal: sem cache em navegador ou proxy. */
    @Override
    public ResponseEntity<byte[]> arquivo(UUID id, TipoArquivoValidacao tipo) {
        ArquivoValidacao arquivo = analisarValidacao.arquivo(id, tipo);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(arquivo.contentType()))
                .cacheControl(CacheControl.noStore())
                .header("X-Content-Type-Options", "nosniff")
                .body(arquivo.conteudo());
    }

    @Override
    @Transactional
    public EnvioValidacaoResponse aprovar(Jwt jwt, UUID id) {
        return EnvioValidacaoResponse.de(analisarValidacao.aprovar(id, usuarioLogado(jwt)));
    }

    @Override
    @Transactional
    public EnvioValidacaoResponse reprovar(Jwt jwt, UUID id, ReprovarValidacaoRequest request) {
        return EnvioValidacaoResponse.de(analisarValidacao.reprovar(id, usuarioLogado(jwt), request.motivo()));
    }

    private static UUID usuarioLogado(Jwt jwt) {
        return UUID.fromString(jwt.getSubject());
    }
}
