package com.unex.chamaAi.usuario.application.usecases;

import com.unex.chamaAi.usuario.domain.services.CriptografadorSenha;
import com.unex.chamaAi.usuario.domain.valueobjects.Senha;
import com.unex.chamaAi.usuario.domain.valueobjects.SenhaHash;

class CriptografadorSenhaFake implements CriptografadorSenha {

    @Override
    public SenhaHash criptografar(Senha senha) {
        return new SenhaHash("hash:" + senha.valor());
    }

    @Override
    public boolean confere(Senha senha, SenhaHash hash) {
        return hash.valor().equals("hash:" + senha.valor());
    }
}
