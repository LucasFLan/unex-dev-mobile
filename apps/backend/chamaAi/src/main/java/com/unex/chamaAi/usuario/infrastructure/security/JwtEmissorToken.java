package com.unex.chamaAi.usuario.infrastructure.security;

import com.unex.chamaAi.config.JwtConfig;
import com.unex.chamaAi.config.SecurityConfig;
import com.unex.chamaAi.usuario.application.dto.TokenAcesso;
import com.unex.chamaAi.usuario.application.ports.EmissorToken;
import com.unex.chamaAi.usuario.domain.entities.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Component
public class JwtEmissorToken implements EmissorToken {

    private final JwtEncoder encoder;
    private final Duration validade;

    public JwtEmissorToken(JwtEncoder encoder, @Value("${chamaai.jwt.validade:PT1H}") Duration validade) {
        this.encoder = encoder;
        this.validade = validade;
    }

    @Override
    public TokenAcesso emitir(Usuario usuario) {
        Instant agora = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(JwtConfig.EMISSOR)
                .subject(usuario.getId().toString())
                .issuedAt(agora)
                .expiresAt(agora.plus(validade))
                .claim(SecurityConfig.CLAIM_PAPEL, usuario.getPapel().name())
                .build();
        JwsHeader cabecalho = JwsHeader.with(MacAlgorithm.HS256).build();

        String token = encoder.encode(JwtEncoderParameters.from(cabecalho, claims)).getTokenValue();
        return new TokenAcesso(token, "Bearer", validade.toSeconds());
    }
}
