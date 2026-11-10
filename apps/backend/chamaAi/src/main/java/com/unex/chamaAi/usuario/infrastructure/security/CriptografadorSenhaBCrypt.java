package com.unex.chamaAi.usuario.infrastructure.security;

import com.unex.chamaAi.usuario.domain.services.CriptografadorSenha;
import com.unex.chamaAi.usuario.domain.valueobjects.Senha;
import com.unex.chamaAi.usuario.domain.valueobjects.SenhaHash;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class CriptografadorSenhaBCrypt implements CriptografadorSenha {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public SenhaHash criptografar(Senha senha) {
        return new SenhaHash(encoder.encode(senha.valor()));
    }

    @Override
    public boolean confere(Senha senha, SenhaHash hash) {
        return encoder.matches(senha.valor(), hash.valor());
    }
}
