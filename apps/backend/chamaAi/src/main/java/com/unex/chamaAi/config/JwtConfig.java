package com.unex.chamaAi.config;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

@Configuration
public class JwtConfig {

    public static final String EMISSOR = "chamaAi";

    private static final Logger log = LoggerFactory.getLogger(JwtConfig.class);
    private static final int TAMANHO_MINIMO_BYTES = 32;

    @Bean
    public SecretKey chaveJwt(@Value("${chamaai.jwt.segredo:}") String segredo) {
        if (segredo.isBlank()) {
            log.warn("JWT_SECRET não definido: usando uma chave aleatória. Os tokens deixam de valer quando a "
                    + "aplicação reinicia. Defina JWT_SECRET em qualquer ambiente que não seja o local.");
            byte[] aleatoria = new byte[TAMANHO_MINIMO_BYTES];
            new SecureRandom().nextBytes(aleatoria);
            return new SecretKeySpec(aleatoria, "HmacSHA256");
        }
        byte[] bytes = segredo.getBytes(StandardCharsets.UTF_8);
        if (bytes.length < TAMANHO_MINIMO_BYTES) {
            throw new IllegalStateException("JWT_SECRET deve ter no mínimo " + TAMANHO_MINIMO_BYTES + " bytes.");
        }
        return new SecretKeySpec(bytes, "HmacSHA256");
    }

    @Bean
    public JwtEncoder jwtEncoder(SecretKey chaveJwt) {
        return new NimbusJwtEncoder(new ImmutableSecret<>(chaveJwt));
    }

    @Bean
    public JwtDecoder jwtDecoder(SecretKey chaveJwt) {
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(chaveJwt).macAlgorithm(MacAlgorithm.HS256).build();
        decoder.setJwtValidator(JwtValidators.createDefaultWithIssuer(EMISSOR));
        return decoder;
    }
}
