package com.unex.chamaAi.usuario.infrastructure.security;

import com.unex.chamaAi.usuario.domain.valueobjects.Senha;
import com.unex.chamaAi.usuario.domain.valueobjects.SenhaHash;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CriptografadorSenhaBCryptTest {

    private static final Senha SENHA = new Senha("Senha123!");

    private final CriptografadorSenhaBCrypt criptografador = new CriptografadorSenhaBCrypt();

    @Test
    void criptografarGeraHashBCrypt() {
        SenhaHash hash = criptografador.criptografar(SENHA);
        assertTrue(hash.valor().startsWith("$2"));
        assertFalse(hash.valor().contains(SENHA.valor()));
    }

    @Test
    void confereAceitaSomenteASenhaCorreta() {
        SenhaHash hash = criptografador.criptografar(SENHA);
        assertTrue(criptografador.confere(SENHA, hash));
        assertFalse(criptografador.confere(new Senha("Outra123!"), hash));
    }

    @Test
    void criptografaSenhaNoLimiteDeBytes() {
        Senha senhaNoLimite = new Senha("Aa1!" + "x".repeat(68));
        assertTrue(criptografador.confere(senhaNoLimite, criptografador.criptografar(senhaNoLimite)));
    }
}
