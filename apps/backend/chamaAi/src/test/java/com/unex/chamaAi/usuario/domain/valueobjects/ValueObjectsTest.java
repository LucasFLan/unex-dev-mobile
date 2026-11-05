package com.unex.chamaAi.usuario.domain.valueobjects;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class ValueObjectsTest {

    @ParameterizedTest
    @ValueSource(strings = {"529.982.247-25", "52998224725", " 529 982 247 25 "})
    void cpfValidoEhNormalizado(String entrada) {
        Cpf cpf = new Cpf(entrada);
        assertEquals("52998224725", cpf.valor());
        assertEquals("529.982.247-25", cpf.formatado());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"111.111.111-11", "529.982.247-24", "123", "5299822472512"})
    void cpfInvalidoLancaExcecao(String entrada) {
        assertThrows(IllegalArgumentException.class, () -> new Cpf(entrada));
    }

    @Test
    void cpfsComMesmoValorSaoIguais() {
        assertEquals(new Cpf("529.982.247-25"), new Cpf("52998224725"));
    }

    @Test
    void emailEhNormalizado() {
        assertEquals("joao.silva@unex.edu.br", new Email("  Joao.Silva@UNEX.edu.br ").valor());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"joao", "joao@", "@unex.com", "joao@unex", "joao silva@unex.com"})
    void emailInvalidoLancaExcecao(String entrada) {
        assertThrows(IllegalArgumentException.class, () -> new Email(entrada));
    }

    @ParameterizedTest
    @ValueSource(strings = {"Senha123!", "Senha123#", "Senha_123", "Sénha123-"})
    void senhaComplexaEhAceita(String entrada) {
        assertDoesNotThrow(() -> new Senha(entrada));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"Se1!", "senha123!", "SenhaForte!", "Senha1234"})
    void senhaFracaLancaExcecao(String entrada) {
        assertThrows(IllegalArgumentException.class, () -> new Senha(entrada));
    }

    @Test
    void senhaNaoApareceNoToString() {
        assertFalse(new Senha("Senha123!").toString().contains("Senha123!"));
        assertFalse(new SenhaHash("$2a$10$hash").toString().contains("hash"));
    }
}
