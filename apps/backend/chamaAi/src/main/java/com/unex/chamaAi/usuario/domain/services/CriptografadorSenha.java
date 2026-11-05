package com.unex.chamaAi.usuario.domain.services;

import com.unex.chamaAi.usuario.domain.valueobjects.Senha;
import com.unex.chamaAi.usuario.domain.valueobjects.SenhaHash;

public interface CriptografadorSenha {

    SenhaHash criptografar(Senha senha);

    boolean confere(Senha senha, SenhaHash hash);
}
