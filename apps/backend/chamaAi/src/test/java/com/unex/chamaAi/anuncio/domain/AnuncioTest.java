package com.unex.chamaAi.anuncio.domain;

import com.unex.chamaAi.anuncio.domain.entities.Anuncio;
import com.unex.chamaAi.anuncio.domain.valueobjects.Localizacao;
import com.unex.chamaAi.anuncio.domain.valueobjects.Preco;
import com.unex.chamaAi.anuncio.domain.valueobjects.TipoAnuncio;
import com.unex.chamaAi.shared.domain.exceptions.DadoInvalidoException;
import com.unex.chamaAi.shared.domain.paginacao.Paginacao;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AnuncioTest {

    private static final UUID AUTOR = UUID.randomUUID();
    private static final UUID CATEGORIA = UUID.randomUUID();
    private static final Localizacao SALVADOR = new Localizacao("Salvador", "BA");

    private Anuncio novo(String titulo, String descricao) {
        return Anuncio.criar(AUTOR, CATEGORIA, TipoAnuncio.OFERTA, titulo, descricao, null, SALVADOR);
    }

    @Test
    void criaAnuncioAtivoComDatasEPrecoACombinar() {
        Anuncio anuncio = novo("  Eletricista residencial  ", " Instalações e reparos ");

        assertNotNull(anuncio.getId());
        assertEquals("Eletricista residencial", anuncio.getTitulo());
        assertEquals("Instalações e reparos", anuncio.getDescricao());
        assertTrue(anuncio.isAtivo());
        assertTrue(anuncio.getPreco().isEmpty());
        assertEquals(anuncio.getCriadoEm(), anuncio.getAtualizadoEm());
        assertTrue(anuncio.pertenceA(AUTOR));
        assertFalse(anuncio.pertenceA(UUID.randomUUID()));
    }

    @Test
    void validaTituloEDescricao() {
        assertThrows(DadoInvalidoException.class, () -> novo(null, "desc"));
        assertThrows(DadoInvalidoException.class, () -> novo("ab", "desc"));
        assertThrows(DadoInvalidoException.class, () -> novo("x".repeat(121), "desc"));
        assertThrows(DadoInvalidoException.class, () -> novo("Título ok", "   "));
        assertThrows(DadoInvalidoException.class, () -> novo("Título ok", "x".repeat(5001)));
    }

    @Test
    void exigeCategoriaTipoELocalizacao() {
        assertThrows(DadoInvalidoException.class,
                () -> Anuncio.criar(AUTOR, null, TipoAnuncio.OFERTA, "Título", "desc", null, SALVADOR));
        assertThrows(DadoInvalidoException.class,
                () -> Anuncio.criar(AUTOR, CATEGORIA, null, "Título", "desc", null, SALVADOR));
        assertThrows(DadoInvalidoException.class,
                () -> Anuncio.criar(AUTOR, CATEGORIA, TipoAnuncio.OFERTA, "Título", "desc", null, null));
    }

    @Test
    void editarAtualizaCamposEDataDeAtualizacao() throws InterruptedException {
        Anuncio anuncio = novo("Eletricista", "desc");
        Thread.sleep(2);

        anuncio.editar(CATEGORIA, TipoAnuncio.PEDIDO, "Preciso de eletricista", "Trocar tomadas",
                new Preco(new BigDecimal("150")), new Localizacao("Lauro de Freitas", "ba"));

        assertEquals(TipoAnuncio.PEDIDO, anuncio.getTipo());
        assertEquals("Preciso de eletricista", anuncio.getTitulo());
        assertEquals(new BigDecimal("150.00"), anuncio.getPreco().orElseThrow().valor());
        assertEquals("BA", anuncio.getLocalizacao().uf());
        assertTrue(anuncio.getAtualizadoEm().isAfter(anuncio.getCriadoEm()));
    }

    @Test
    void anuncioInativoSoEhVisivelParaOAutor() {
        Anuncio inativo = Anuncio.reconstituir(UUID.randomUUID(), AUTOR, CATEGORIA, TipoAnuncio.OFERTA, "Título",
                "desc", null, SALVADOR, false, java.time.Instant.now(), java.time.Instant.now());

        assertTrue(inativo.visivelPara(AUTOR));
        assertFalse(inativo.visivelPara(UUID.randomUUID()));
        assertFalse(inativo.visivelPara(null));
    }

    @Test
    void precoDeveSerPositivoComAteDuasCasas() {
        assertEquals(new BigDecimal("10.50"), new Preco(new BigDecimal("10.5")).valor());
        assertEquals(new BigDecimal("10.00"), new Preco(new BigDecimal("10.000")).valor());
        assertThrows(DadoInvalidoException.class, () -> new Preco(BigDecimal.ZERO));
        assertThrows(DadoInvalidoException.class, () -> new Preco(new BigDecimal("-1")));
        assertThrows(DadoInvalidoException.class, () -> new Preco(new BigDecimal("1.999")));
        assertThrows(DadoInvalidoException.class, () -> new Preco(new BigDecimal("100000000")));
        assertNull(Preco.deOuNulo(null));
    }

    @Test
    void localizacaoNormalizaEValidaUf() {
        Localizacao local = new Localizacao("  Feira de Santana ", " ba ");
        assertEquals("Feira de Santana", local.cidade());
        assertEquals("BA", local.uf());
        assertThrows(DadoInvalidoException.class, () -> new Localizacao("Salvador", "XX"));
        assertThrows(DadoInvalidoException.class, () -> new Localizacao(" ", "BA"));
        assertThrows(DadoInvalidoException.class, () -> new Localizacao("Salvador", null));
    }

    @Test
    void tipoAceitaMaiusculasOuMinusculas() {
        assertEquals(TipoAnuncio.PEDIDO, TipoAnuncio.de(" pedido "));
        assertThrows(DadoInvalidoException.class, () -> TipoAnuncio.de("VENDA"));
        assertThrows(DadoInvalidoException.class, () -> TipoAnuncio.de(null));
    }

    @Test
    void paginacaoValidaLimites() {
        assertEquals(new Paginacao(0, 20), Paginacao.de(null, null));
        assertThrows(DadoInvalidoException.class, () -> Paginacao.de(-1, 10));
        assertThrows(DadoInvalidoException.class, () -> Paginacao.de(0, 0));
        assertThrows(DadoInvalidoException.class, () -> Paginacao.de(0, 51));
    }
}
