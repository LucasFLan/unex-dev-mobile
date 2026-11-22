package com.unex.chamaAi.usuario.infrastructure.web;

import com.unex.chamaAi.shared.domain.exceptions.DadoInvalidoException;
import com.unex.chamaAi.usuario.application.usecases.ValidarConta;
import com.unex.chamaAi.usuario.infrastructure.web.dto.response.MinhaValidacaoResponse;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
public class ValidacaoContaControllerImpl implements ValidacaoContaController {

    private final ValidarConta validarConta;

    public ValidacaoContaControllerImpl(ValidarConta validarConta) {
        this.validarConta = validarConta;
    }

    @Override
    public MinhaValidacaoResponse consultar(Jwt jwt) {
        return MinhaValidacaoResponse.de(validarConta.consultar(usuarioLogado(jwt)));
    }

    /** Envio, imagens e volta do usuário para PENDENTE são gravados juntos. */
    @Override
    @Transactional
    public MinhaValidacaoResponse enviar(Jwt jwt, MultipartFile documento, MultipartFile selfie) {
        return MinhaValidacaoResponse.de(validarConta.enviar(usuarioLogado(jwt), bytes(documento), bytes(selfie)));
    }

    private static byte[] bytes(MultipartFile arquivo) {
        try {
            return arquivo == null ? null : arquivo.getBytes();
        } catch (IOException e) {
            throw new DadoInvalidoException("Não foi possível ler a imagem enviada.");
        }
    }

    /** O "sub" do token é o id do usuário (ver JwtEmissorToken). */
    private static UUID usuarioLogado(Jwt jwt) {
        return UUID.fromString(jwt.getSubject());
    }
}
