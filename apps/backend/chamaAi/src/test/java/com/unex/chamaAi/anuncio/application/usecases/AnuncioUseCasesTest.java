package com.unex.chamaAi.anuncio.application.usecases;

import com.unex.chamaAi.anuncio.application.dto.AnuncioDetalhado;
import com.unex.chamaAi.anuncio.application.dto.DadosAnuncioCommand;
import com.unex.chamaAi.anuncio.application.dto.ListarAnunciosQuery;
import com.unex.chamaAi.anuncio.domain.entities.Anuncio;
import com.unex.chamaAi.anuncio.domain.entities.Categoria;
import com.unex.chamaAi.anuncio.domain.exceptions.AcessoNegadoAnuncioException;
import com.unex.chamaAi.anuncio.domain.exceptions.AnuncioNaoEncontradoException;
import com.unex.chamaAi.anuncio.domain.exceptions.CategoriaInvalidaException;
import com.unex.chamaAi.anuncio.domain.valueobjects.Localizacao;
import com.unex.chamaAi.anuncio.domain.valueobjects.TipoAnuncio;
import com.unex.chamaAi.shared.domain.exceptions.DadoInvalidoException;
import com.unex.chamaAi.shared.domain.paginacao.Pagina;
import com.unex.chamaAi.usuario.domain.exceptions.ContaNaoValidadaException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AnuncioUseCasesTest {

    private static final UUID AUTOR = UUID.randomUUID();
    private static final UUID OUTRO = UUID.randomUUID();

    private final RepositoriosEmMemoria.Anuncios anuncios = new RepositoriosEmMemoria.Anuncios();
    private final RepositoriosEmMemoria.Categorias categorias = new RepositoriosEmMemoria.Categorias();

    private final RepositoriosEmMemoria.Usuarios usuarios = new RepositoriosEmMemoria.Usuarios();
    private final CriarAnuncio criar = new CriarAnuncio(anuncios, categorias, usuarios, cep -> java.util.Optional.empty(),
            com.unex.chamaAi.anuncio.application.ports.AutoresAnuncio.NENHUM);
    private final BuscarAnuncio buscar = new BuscarAnuncio(anuncios, categorias, com.unex.chamaAi.anuncio.application.ports.AutoresAnuncio.NENHUM);
    private final ListarAnuncios listar = new ListarAnuncios(anuncios, categorias, com.unex.chamaAi.anuncio.application.ports.AutoresAnuncio.NENHUM);
    private final ListarMeusAnuncios listarMeus = new ListarMeusAnuncios(anuncios, categorias, com.unex.chamaAi.anuncio.application.ports.AutoresAnuncio.NENHUM);
    private final EditarAnuncio editar = new EditarAnuncio(anuncios, categorias, cep -> java.util.Optional.empty(),
            com.unex.chamaAi.anuncio.application.ports.AutoresAnuncio.NENHUM);
    private final ExcluirAnuncio excluir = new ExcluirAnuncio(anuncios);
    private final AlterarAtivacaoAnuncio alterarAtivacao = new AlterarAtivacaoAnuncio(anuncios, categorias, com.unex.chamaAi.anuncio.application.ports.AutoresAnuncio.NENHUM);
    private final ListarCategorias listarCategorias = new ListarCategorias(categorias);

    private Categoria eletricista;
    private Categoria pintor;

    @BeforeEach
    void setUp() {
        eletricista = categorias.adicionar("Eletricista");
        pintor = categorias.adicionar("Pintor");
    }

    private DadosAnuncioCommand comando(String tipo, UUID categoriaId, String cidade, String uf) {
        return new DadosAnuncioCommand(tipo, categoriaId, "Serviço elétrico", "Instalação de chuveiro",
                new BigDecimal("120.00"), cidade, uf);
    }

    private AnuncioDetalhado criarOferta(UUID autor) {
        return criar.executar(autor, comando("OFERTA", eletricista.id(), "Salvador", "BA"));
    }

    private void adicionarInativo(UUID autor) {
        anuncios.salvar(Anuncio.reconstituir(UUID.randomUUID(), autor, eletricista.id(), TipoAnuncio.OFERTA,
                "Inativo", "desc", null, new Localizacao("Salvador", "BA"), false, Instant.now(), Instant.now()));
    }

    @Test
    void criaAnuncioComCategoriaDetalhada() {
        AnuncioDetalhado criado = criarOferta(AUTOR);

        assertEquals("OFERTA", criado.tipo());
        assertEquals(new BigDecimal("120.00"), criado.preco());
        assertEquals("Eletricista", criado.categoria().nome());
        assertEquals(AUTOR, criado.autorId());
        assertTrue(criado.ativo());
        assertEquals(1, anuncios.dados.size());
    }

    @Test
    void contaSemValidacaoNaoPublica() {
        usuarios.pendentes.add(OUTRO);

        assertThrows(ContaNaoValidadaException.class, () -> criarOferta(OUTRO));
        assertTrue(anuncios.dados.isEmpty());
    }

    @Test
    void precoNuloSignificaACombinar() {
        var semPreco = new DadosAnuncioCommand("PEDIDO", pintor.id(), "Pintar sala", "Sala de 20m²", null,
                "Salvador", "BA");
        assertNull(criar.executar(AUTOR, semPreco).preco());
    }

    @Test
    void rejeitaCategoriaInexistenteOuAusente() {
        assertThrows(CategoriaInvalidaException.class,
                () -> criar.executar(AUTOR, comando("OFERTA", UUID.randomUUID(), "Salvador", "BA")));
        assertThrows(DadoInvalidoException.class,
                () -> criar.executar(AUTOR, comando("OFERTA", null, "Salvador", "BA")));
        assertTrue(anuncios.dados.isEmpty());
    }

    @Test
    void rejeitaTipoInvalido() {
        assertThrows(DadoInvalidoException.class,
                () -> criar.executar(AUTOR, comando("VENDA", eletricista.id(), "Salvador", "BA")));
    }

    @Test
    void buscaEscondeAnuncioInativoDeQuemNaoEhAutor() {
        adicionarInativo(AUTOR);
        UUID id = anuncios.dados.keySet().iterator().next();

        assertEquals(id, buscar.executar(id, AUTOR).id());
        assertThrows(AnuncioNaoEncontradoException.class, () -> buscar.executar(id, OUTRO));
        assertThrows(AnuncioNaoEncontradoException.class, () -> buscar.executar(id, null));
        assertThrows(AnuncioNaoEncontradoException.class, () -> buscar.executar(UUID.randomUUID(), AUTOR));
    }

    @Test
    void listagemPublicaFiltraEIgnoraInativos() {
        criarOferta(AUTOR);
        criar.executar(AUTOR, comando("PEDIDO", pintor.id(), "Feira de Santana", "BA"));
        criar.executar(OUTRO, comando("OFERTA", pintor.id(), "Recife", "PE"));
        adicionarInativo(AUTOR);

        assertEquals(3, listar.executar(new ListarAnunciosQuery(null, null, null, null, null, null)).totalItens());
        assertEquals(1, listar.executar(new ListarAnunciosQuery("pedido", null, null, null, null, null)).totalItens());
        assertEquals(2, listar.executar(new ListarAnunciosQuery(null, pintor.id(), null, null, null, null)).totalItens());
        assertEquals(2, listar.executar(new ListarAnunciosQuery(null, null, null, "ba", null, null)).totalItens());
        assertEquals(1, listar.executar(new ListarAnunciosQuery(null, null, "SALVADOR", null, null, null)).totalItens());

        Pagina<AnuncioDetalhado> primeira = listar.executar(new ListarAnunciosQuery(null, null, null, null, 0, 2));
        assertEquals(2, primeira.itens().size());
        assertEquals(2, primeira.totalPaginas());
    }

    @Test
    void listagemValidaFiltros() {
        assertThrows(DadoInvalidoException.class,
                () -> listar.executar(new ListarAnunciosQuery("VENDA", null, null, null, null, null)));
        assertThrows(DadoInvalidoException.class,
                () -> listar.executar(new ListarAnunciosQuery(null, null, null, "XX", null, null)));
        assertThrows(DadoInvalidoException.class,
                () -> listar.executar(new ListarAnunciosQuery(null, null, null, null, 0, 500)));
    }

    @Test
    void meusAnunciosIncluiInativosESoDoAutor() {
        criarOferta(AUTOR);
        adicionarInativo(AUTOR);
        criarOferta(OUTRO);

        assertEquals(2, listarMeus.executar(AUTOR, null, null).totalItens());
    }

    @Test
    void autorEditaOProprioAnuncio() {
        UUID id = criarOferta(AUTOR).id();

        AnuncioDetalhado editado = editar.executar(id, AUTOR,
                new DadosAnuncioCommand("PEDIDO", pintor.id(), "Pintura de muro", "Muro de 10m", null, "Recife", "PE"));

        assertEquals("PEDIDO", editado.tipo());
        assertEquals("Pintor", editado.categoria().nome());
        assertNull(editado.preco());
        assertEquals("PE", editado.uf());
    }

    @Test
    void outroUsuarioNaoEdita() {
        UUID id = criarOferta(AUTOR).id();

        assertThrows(AcessoNegadoAnuncioException.class,
                () -> editar.executar(id, OUTRO, comando("OFERTA", eletricista.id(), "Salvador", "BA")));
        assertThrows(AnuncioNaoEncontradoException.class,
                () -> editar.executar(UUID.randomUUID(), AUTOR, comando("OFERTA", eletricista.id(), "Salvador", "BA")));
    }

    @Test
    void autorOuAdminExcluem() {
        UUID doAutor = criarOferta(AUTOR).id();
        UUID outroAnuncio = criarOferta(AUTOR).id();

        assertThrows(AcessoNegadoAnuncioException.class, () -> excluir.executar(doAutor, OUTRO, false));

        excluir.executar(doAutor, AUTOR, false);
        excluir.executar(outroAnuncio, OUTRO, true);

        assertTrue(anuncios.dados.isEmpty());
        assertThrows(AnuncioNaoEncontradoException.class, () -> excluir.executar(doAutor, AUTOR, false));
    }

    @Test
    void autorDesativaEReativaOProprioAnuncio() {
        UUID id = criarOferta(AUTOR).id();

        assertFalse(alterarAtivacao.executar(id, AUTOR, false).ativo());
        assertThrows(AnuncioNaoEncontradoException.class, () -> buscar.executar(id, OUTRO));
        assertEquals(0, listar.executar(new ListarAnunciosQuery(null, null, null, null, null, null)).totalItens());
        assertEquals(1, listarMeus.executar(AUTOR, null, null).totalItens());

        assertFalse(alterarAtivacao.executar(id, AUTOR, false).ativo());
        assertTrue(alterarAtivacao.executar(id, AUTOR, true).ativo());
        assertEquals(1, listar.executar(new ListarAnunciosQuery(null, null, null, null, null, null)).totalItens());
    }

    @Test
    void outroUsuarioNaoAlteraAtivacao() {
        UUID ativo = criarOferta(AUTOR).id();
        UUID inativo = criarOferta(AUTOR).id();
        alterarAtivacao.executar(inativo, AUTOR, false);

        assertThrows(AcessoNegadoAnuncioException.class, () -> alterarAtivacao.executar(ativo, OUTRO, false));
        assertThrows(AnuncioNaoEncontradoException.class, () -> alterarAtivacao.executar(inativo, OUTRO, true));
        assertThrows(AnuncioNaoEncontradoException.class,
                () -> alterarAtivacao.executar(UUID.randomUUID(), AUTOR, true));
        assertTrue(buscar.executar(ativo, OUTRO).ativo());
    }

    @Test
    void listaCategoriasPorNome() {
        assertEquals("Eletricista", listarCategorias.executar().getFirst().nome());
        assertEquals(2, listarCategorias.executar().size());
    }
}
