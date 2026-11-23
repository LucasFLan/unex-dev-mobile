package com.unex.chamaAi.proposta.application.usecases;

import com.unex.chamaAi.anuncio.domain.entities.Anuncio;
import com.unex.chamaAi.anuncio.domain.exceptions.AnuncioNaoEncontradoException;
import com.unex.chamaAi.anuncio.domain.repositories.AnuncioRepository;
import com.unex.chamaAi.anuncio.domain.repositories.FiltroAnuncios;
import com.unex.chamaAi.anuncio.domain.valueobjects.Localizacao;
import com.unex.chamaAi.anuncio.domain.valueobjects.TipoAnuncio;
import com.unex.chamaAi.notificacao.application.usecases.ListarNotificacoes;
import com.unex.chamaAi.notificacao.application.usecases.MarcarNotificacoesComoLidas;
import com.unex.chamaAi.notificacao.application.usecases.NotificarEventosDeProposta;
import com.unex.chamaAi.notificacao.domain.entities.Notificacao;
import com.unex.chamaAi.notificacao.domain.exceptions.NotificacaoNaoEncontradaException;
import com.unex.chamaAi.notificacao.domain.repositories.NotificacaoRepository;
import com.unex.chamaAi.proposta.application.dto.EnviarPropostaCommand;
import com.unex.chamaAi.proposta.application.dto.PropostaDetalhada;
import com.unex.chamaAi.proposta.domain.entities.Proposta;
import com.unex.chamaAi.proposta.domain.exceptions.AcessoNegadoPropostaException;
import com.unex.chamaAi.usuario.domain.exceptions.ContaNaoValidadaException;
import com.unex.chamaAi.proposta.domain.exceptions.PropostaDuplicadaException;
import com.unex.chamaAi.proposta.domain.exceptions.PropostaJaRespondidaException;
import com.unex.chamaAi.proposta.domain.exceptions.PropostaNaoEncontradaException;
import com.unex.chamaAi.proposta.domain.exceptions.PropostaParaProprioAnuncioException;
import com.unex.chamaAi.proposta.domain.repositories.PropostaRepository;
import com.unex.chamaAi.proposta.domain.valueobjects.StatusProposta;
import com.unex.chamaAi.notificacao.domain.valueobjects.TipoNotificacao;
import com.unex.chamaAi.shared.domain.exceptions.DadoInvalidoException;
import com.unex.chamaAi.shared.domain.paginacao.Pagina;
import com.unex.chamaAi.shared.domain.paginacao.Paginacao;
import com.unex.chamaAi.usuario.domain.entities.Usuario;
import com.unex.chamaAi.usuario.domain.repositories.UsuarioRepository;
import com.unex.chamaAi.usuario.domain.valueobjects.Cpf;
import com.unex.chamaAi.usuario.domain.valueobjects.Email;
import com.unex.chamaAi.usuario.domain.valueobjects.SenhaHash;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

class PropostaUseCasesTest {

    private final Anuncios anuncios = new Anuncios();
    private final Usuarios usuarios = new Usuarios();
    private final Propostas propostas = new Propostas();
    private final Notificacoes notificacoes = new Notificacoes();

    private final FotosEmMemoria fotos = new FotosEmMemoria();
    private final AvaliacoesEmMemoria avaliacoes = new AvaliacoesEmMemoria();

    private final EnviarProposta enviar = new EnviarProposta(propostas, anuncios, usuarios, fotos,
            new NotificarEventosDeProposta(notificacoes));
    private final ListarPropostas listar = new ListarPropostas(propostas, anuncios, usuarios, fotos, avaliacoes);
    private final ResponderProposta responder = new ResponderProposta(propostas, anuncios, usuarios, fotos, avaliacoes,
            new NotificarEventosDeProposta(notificacoes));
    private final ListarNotificacoes listarNotificacoes = new ListarNotificacoes(notificacoes);
    private final com.unex.chamaAi.anuncio.domain.repositories.CategoriaRepository categorias =
            new com.unex.chamaAi.anuncio.domain.repositories.CategoriaRepository() {
                @Override
                public List<com.unex.chamaAi.anuncio.domain.entities.Categoria> listarTodas() {
                    return List.of();
                }

                @Override
                public Optional<com.unex.chamaAi.anuncio.domain.entities.Categoria> buscarPorId(UUID id) {
                    return Optional.of(new com.unex.chamaAi.anuncio.domain.entities.Categoria(id, "eletricista",
                            "Eletricista"));
                }
            };
    private final AvaliarServico avaliar = new AvaliarServico(propostas, anuncios, categorias, usuarios, avaliacoes,
            new NotificarEventosDeProposta(notificacoes));
    private final ListarAvaliacoes listarAvaliacoes = new ListarAvaliacoes(avaliacoes, usuarios);
    private final MarcarNotificacoesComoLidas marcarComoLidas = new MarcarNotificacoesComoLidas(notificacoes);

    private Usuario joao;
    private Usuario maria;
    private Usuario pedro;
    private Anuncio pedidoDoJoao;

    @BeforeEach
    void setUp() {
        joao = usuarios.adicionar("João", "529.982.247-25", "joao@unex.edu.br");
        maria = usuarios.adicionar("Maria", "111.444.777-35", "maria@unex.edu.br");
        pedro = usuarios.adicionar("Pedro", "390.533.447-05", "pedro@unex.edu.br");
        pedidoDoJoao = anuncios.adicionar(joao.getId(), "Trocar chuveiro", true);
    }

    private PropostaDetalhada mariaPropoe(String valor, String mensagem) {
        return enviar.executar(pedidoDoJoao.getId(), maria.getId(),
                new EnviarPropostaCommand(valor == null ? null : new BigDecimal(valor), mensagem));
    }

    @Test
    void enviaPropostaENotificaOAutorDoAnuncio() {
        PropostaDetalhada proposta = mariaPropoe("180", "  Posso ir amanhã.  ");

        assertEquals(new BigDecimal("180.00"), proposta.valor());
        assertEquals("Posso ir amanhã.", proposta.mensagem());
        assertEquals("Maria", proposta.remetente().nome());
        assertEquals("Trocar chuveiro", proposta.anuncio().titulo());
        assertEquals(joao.getId(), proposta.destinatario().id());

        Pagina<?> doJoao = listarNotificacoes.executar(joao.getId(), null, null);
        assertEquals(1, doJoao.totalItens());
        Notificacao notificacao = notificacoes.dados.values().iterator().next();
        assertEquals(joao.getId(), notificacao.getUsuarioId());
        assertEquals(proposta.id(), notificacao.getPropostaId());
        assertEquals(pedidoDoJoao.getId(), notificacao.getAnuncioId());
        assertFalse(notificacao.isLida());
        assertTrue(notificacao.getMensagem().startsWith("Maria enviou uma proposta de R$"), notificacao.getMensagem());
        assertTrue(notificacao.getMensagem().contains("180,00"), notificacao.getMensagem());
        assertTrue(notificacao.getMensagem().endsWith("\"Trocar chuveiro\": Posso ir amanhã."));

        assertEquals(0, listarNotificacoes.contarNaoLidas(maria.getId()));
    }

    @Test
    void contaSemValidacaoNaoEnviaProposta() {
        Usuario novo = Usuario.criar("Novo", LocalDate.of(2000, 1, 1), new Cpf("123.456.789-09"),
                new Email("novo@unex.edu.br"), new SenhaHash("hash"), null);
        usuarios.salvar(novo);

        assertThrows(ContaNaoValidadaException.class, () -> enviar.executar(pedidoDoJoao.getId(), novo.getId(),
                new EnviarPropostaCommand(null, "Oi")));
        assertTrue(propostas.dados.isEmpty());
    }

    @Test
    void valorNuloEhACombinar() {
        assertNull(mariaPropoe(null, "Vamos combinar o valor?").valor());
        assertTrue(notificacoes.dados.values().iterator().next().getMensagem().contains("(valor a combinar)"));
    }

    private static final byte[] JPEG = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0, 0, 0x10};
    private static final byte[] PNG = {(byte) 0x89, 'P', 'N', 'G', 0x0D, 0x0A, 0x1A, 0x0A, 0, 0};

    private PropostaDetalhada mariaPropoeComFotos(List<byte[]> fotosAnexadas) {
        return enviar.executar(pedidoDoJoao.getId(), maria.getId(),
                new EnviarPropostaCommand(null, "Segue foto do serviço", fotosAnexadas));
    }

    @Test
    void anexaFotosNaPropostaNaOrdemDoEnvio() {
        PropostaDetalhada proposta = mariaPropoeComFotos(List.of(JPEG, PNG));

        assertEquals(2, proposta.fotos().size());
        assertEquals("image/jpeg", listar.foto(proposta.id(), proposta.fotos().get(0), joao.getId()).contentType());
        assertEquals("image/png", listar.foto(proposta.id(), proposta.fotos().get(1), maria.getId()).contentType());
        // A listagem também traz as fotos.
        assertEquals(proposta.fotos(), listar.recebidas(joao.getId(), null, null).itens().getFirst().fotos());
    }

    @Test
    void propostaSemFotoTemListaVazia() {
        assertTrue(mariaPropoe(null, "Sem foto").fotos().isEmpty());
    }

    @Test
    void recusaMaisDeCincoFotosEArquivoQueNaoEhImagem() {
        assertThrows(DadoInvalidoException.class,
                () -> mariaPropoeComFotos(List.of(JPEG, JPEG, JPEG, JPEG, JPEG, JPEG)));
        assertThrows(DadoInvalidoException.class,
                () -> mariaPropoeComFotos(List.of("não sou imagem".getBytes())));
        assertTrue(propostas.dados.isEmpty(), "Nada é gravado quando uma foto é recusada");
    }

    @Test
    void fotoDaPropostaSoEhVistaPelosEnvolvidos() {
        PropostaDetalhada proposta = mariaPropoeComFotos(List.of(JPEG));
        UUID fotoId = proposta.fotos().getFirst();

        assertThrows(PropostaNaoEncontradaException.class, () -> listar.foto(proposta.id(), fotoId, pedro.getId()));
        assertThrows(com.unex.chamaAi.proposta.domain.exceptions.FotoPropostaNaoEncontradaException.class,
                () -> listar.foto(proposta.id(), UUID.randomUUID(), joao.getId()));
    }

    @Test
    void naoPermitePropostaNoProprioAnuncio() {
        assertThrows(PropostaParaProprioAnuncioException.class, () -> enviar.executar(pedidoDoJoao.getId(),
                joao.getId(), new EnviarPropostaCommand(null, "Oi")));
        assertTrue(notificacoes.dados.isEmpty());
    }

    @Test
    void naoPermitePropostaDuplicada() {
        mariaPropoe("100", "Primeira");
        assertThrows(PropostaDuplicadaException.class, () -> mariaPropoe("90", "Segunda"));
        assertEquals(1, propostas.dados.size());
        assertEquals(1, notificacoes.dados.size());
    }

    @Test
    void anuncioInativoOuInexistenteNaoExisteParaQuemPropoe() {
        Anuncio inativo = anuncios.adicionar(joao.getId(), "Pausado", false);

        assertThrows(AnuncioNaoEncontradoException.class, () -> enviar.executar(inativo.getId(), maria.getId(),
                new EnviarPropostaCommand(null, "Oi")));
        assertThrows(AnuncioNaoEncontradoException.class, () -> enviar.executar(UUID.randomUUID(), maria.getId(),
                new EnviarPropostaCommand(null, "Oi")));
    }

    @Test
    void validaMensagemEValor() {
        assertThrows(DadoInvalidoException.class, () -> mariaPropoe(null, "   "));
        assertThrows(DadoInvalidoException.class, () -> mariaPropoe(null, "x".repeat(1001)));
        assertThrows(DadoInvalidoException.class, () -> mariaPropoe("0", "Oi"));
        assertThrows(DadoInvalidoException.class, () -> mariaPropoe("10.555", "Oi"));
        assertTrue(propostas.dados.isEmpty());
        assertTrue(notificacoes.dados.isEmpty());
    }

    @Test
    void mensagemLongaEhResumidaNaNotificacao() {
        mariaPropoe(null, "a".repeat(500));
        String mensagem = notificacoes.dados.values().iterator().next().getMensagem();
        assertTrue(mensagem.endsWith("a".repeat(200) + "…"), mensagem);
    }

    @Test
    void listaRecebidasEEnviadas() {
        mariaPropoe("100", "Proposta");

        assertEquals(1, listar.recebidas(joao.getId(), null, null).totalItens());
        assertEquals(0, listar.enviadas(joao.getId(), null, null).totalItens());
        assertEquals(1, listar.enviadas(maria.getId(), null, null).totalItens());
        assertEquals(0, listar.recebidas(maria.getId(), null, null).totalItens());
    }

    @Test
    void marcaNotificacoesComoLidas() {
        mariaPropoe("100", "Proposta");
        UUID id = notificacoes.dados.keySet().iterator().next();

        assertThrows(NotificacaoNaoEncontradaException.class, () -> marcarComoLidas.uma(id, maria.getId()));
        assertEquals(1, listarNotificacoes.contarNaoLidas(joao.getId()));

        marcarComoLidas.uma(id, joao.getId());
        assertEquals(0, listarNotificacoes.contarNaoLidas(joao.getId()));

        Anuncio outro = anuncios.adicionar(joao.getId(), "Outro pedido", true);
        enviar.executar(outro.getId(), maria.getId(), new EnviarPropostaCommand(null, "Oi"));
        assertEquals(1, listarNotificacoes.contarNaoLidas(joao.getId()));
        marcarComoLidas.todas(joao.getId());
        assertEquals(0, listarNotificacoes.contarNaoLidas(joao.getId()));
    }

    private PropostaDetalhada propor(Anuncio anuncio, Usuario remetente) {
        return enviar.executar(anuncio.getId(), remetente.getId(), new EnviarPropostaCommand(null, "Proposta"));
    }

    private List<Notificacao> notificacoesDe(Usuario usuario) {
        return notificacoes.dados.values().stream().filter(n -> n.pertenceA(usuario.getId())).toList();
    }

    @Test
    void contatoSoAparecaDepoisDoAceite() {
        PropostaDetalhada pendente = mariaPropoe("100", "Proposta");
        assertNull(pendente.remetente().telefone());
        assertNull(pendente.destinatario().telefone());
        assertEquals("PENDENTE", pendente.status());
        assertNull(pendente.respondidoEm());

        PropostaDetalhada aceita = responder.aceitar(pendente.id(), joao.getId());

        assertEquals("ACEITA", aceita.status());
        assertNotNull(aceita.respondidoEm());
        assertEquals("71900000000", aceita.remetente().telefone());
        assertEquals("71900000000", aceita.destinatario().telefone());
    }

    @Test
    void aceitarPedidoPausaOPedidoENaoSelecionaAsOutras() {
        PropostaDetalhada daMaria = propor(pedidoDoJoao, maria);
        PropostaDetalhada doPedro = propor(pedidoDoJoao, pedro);

        responder.aceitar(daMaria.id(), joao.getId());

        assertFalse(anuncios.dados.get(pedidoDoJoao.getId()).isAtivo());
        assertEquals(StatusProposta.ACEITA, propostas.dados.get(daMaria.id()).getStatus());
        assertEquals(StatusProposta.NAO_SELECIONADA, propostas.dados.get(doPedro.id()).getStatus());
        assertEquals(TipoNotificacao.PROPOSTA_ACEITA, notificacoesDe(maria).getFirst().getTipo());
        assertEquals(TipoNotificacao.PROPOSTA_NAO_SELECIONADA, notificacoesDe(pedro).getFirst().getTipo());
        assertTrue(notificacoesDe(maria).getFirst().getMensagem().startsWith("João aceitou sua proposta"));
    }

    @Test
    void aceitarOfertaNaoMexeNaOfertaNemNasOutras() {
        Anuncio oferta = anuncios.adicionar(joao.getId(), "Pintor", true, TipoAnuncio.OFERTA);
        PropostaDetalhada daMaria = propor(oferta, maria);
        PropostaDetalhada doPedro = propor(oferta, pedro);

        responder.aceitar(daMaria.id(), joao.getId());

        assertTrue(anuncios.dados.get(oferta.getId()).isAtivo());
        assertEquals(StatusProposta.PENDENTE, propostas.dados.get(doPedro.id()).getStatus());
        assertTrue(notificacoesDe(pedro).isEmpty());
    }

    // ---- Conclusão e avaliação ----

    /** Pedido do João, proposta da Maria (profissional) aceita: o João é quem contratou. */
    private PropostaDetalhada servicoAceitoNoPedido() {
        PropostaDetalhada proposta = mariaPropoe("150", "Troco hoje");
        responder.aceitar(proposta.id(), joao.getId());
        return proposta;
    }

    @Test
    void clienteDoPedidoConcluiEProfissionalEhAvisado() {
        PropostaDetalhada proposta = servicoAceitoNoPedido();

        PropostaDetalhada concluida = responder.concluir(proposta.id(), joao.getId());

        assertEquals("CONCLUIDA", concluida.status());
        assertNotNull(concluida.concluidoEm());
        assertTrue(notificacoesDe(maria).stream().anyMatch(n -> n.getTipo() == TipoNotificacao.SERVICO_CONCLUIDO));
        // O contato continua liberado depois de concluído.
        assertNotNull(concluida.endereco());
    }

    @Test
    void soQuemContratouConclui() {
        PropostaDetalhada proposta = servicoAceitoNoPedido();

        assertThrows(AcessoNegadoPropostaException.class, () -> responder.concluir(proposta.id(), maria.getId()));
        assertThrows(PropostaNaoEncontradaException.class, () -> responder.concluir(proposta.id(), pedro.getId()));
    }

    @Test
    void soPropostaAceitaPodeSerConcluidaEUmaVezSo() {
        PropostaDetalhada pendente = mariaPropoe(null, "Oi");
        assertThrows(com.unex.chamaAi.proposta.domain.exceptions.ServicoNaoConcluivelException.class,
                () -> responder.concluir(pendente.id(), joao.getId()));

        responder.aceitar(pendente.id(), joao.getId());
        responder.concluir(pendente.id(), joao.getId());
        assertThrows(com.unex.chamaAi.proposta.domain.exceptions.ServicoNaoConcluivelException.class,
                () -> responder.concluir(pendente.id(), joao.getId()));
    }

    @Test
    void naOfertaQuemContrataEhQuemPediuOrcamento() {
        Anuncio oferta = anuncios.adicionar(joao.getId(), "Pintor", true, TipoAnuncio.OFERTA);
        PropostaDetalhada orcamento = propor(oferta, maria);
        responder.aceitar(orcamento.id(), joao.getId());

        // O João é o profissional: não conclui o próprio serviço.
        assertThrows(AcessoNegadoPropostaException.class, () -> responder.concluir(orcamento.id(), joao.getId()));
        responder.concluir(orcamento.id(), maria.getId());

        var avaliacao = avaliar.executar(orcamento.id(), maria.getId(), 4, "Bom trabalho");
        assertEquals(4, avaliacao.nota());
        assertEquals(joao.getId(), avaliacoes.dados.values().iterator().next().getAvaliadoId());
    }

    @Test
    void avaliaDepoisDeConcluirEAMediaEntraNoPerfil() {
        PropostaDetalhada proposta = servicoAceitoNoPedido();
        responder.concluir(proposta.id(), joao.getId());

        var avaliacao = avaliar.executar(proposta.id(), joao.getId(), 5, "  Pontual e caprichosa.  ");

        assertEquals(5, avaliacao.nota());
        assertEquals("Pontual e caprichosa.", avaliacao.comentario());
        assertEquals("Trocar chuveiro", avaliacao.servicoTitulo());
        assertEquals("Eletricista", avaliacao.servicoCategoria());
        assertEquals("João", avaliacao.autor().nome());
        assertTrue(notificacoesDe(maria).stream().anyMatch(n -> n.getTipo() == TipoNotificacao.AVALIACAO_RECEBIDA
                && n.getMensagem().contains("5 estrelas")));

        var perfil = listarAvaliacoes.doUsuario(maria.getId(), null, null);
        assertEquals(5.0, perfil.resumo().media());
        assertEquals(1, perfil.resumo().total());
        assertEquals(1, perfil.avaliacoes().itens().size());
        // A proposta passa a mostrar a avaliação.
        assertEquals(5, listar.enviadas(maria.getId(), null, null).itens().getFirst().avaliacao().nota());
    }

    @Test
    void regrasDaAvaliacao() {
        PropostaDetalhada proposta = servicoAceitoNoPedido();

        // Antes de concluir.
        assertThrows(com.unex.chamaAi.proposta.domain.exceptions.AvaliacaoIndisponivelException.class,
                () -> avaliar.executar(proposta.id(), joao.getId(), 5, null));
        responder.concluir(proposta.id(), joao.getId());

        // O profissional não se avalia; quem está fora não vê a proposta.
        assertThrows(AcessoNegadoPropostaException.class, () -> avaliar.executar(proposta.id(), maria.getId(), 5, null));
        assertThrows(PropostaNaoEncontradaException.class, () -> avaliar.executar(proposta.id(), pedro.getId(), 5, null));
        // Nota fora de 1 a 5, sem nota, comentário longo demais.
        assertThrows(DadoInvalidoException.class, () -> avaliar.executar(proposta.id(), joao.getId(), 0, null));
        assertThrows(DadoInvalidoException.class, () -> avaliar.executar(proposta.id(), joao.getId(), 6, null));
        assertThrows(DadoInvalidoException.class, () -> avaliar.executar(proposta.id(), joao.getId(), null, null));
        assertThrows(DadoInvalidoException.class,
                () -> avaliar.executar(proposta.id(), joao.getId(), 3, "x".repeat(501)));

        avaliar.executar(proposta.id(), joao.getId(), 3, null);
        assertThrows(com.unex.chamaAi.proposta.domain.exceptions.AvaliacaoDuplicadaException.class,
                () -> avaliar.executar(proposta.id(), joao.getId(), 5, null));
    }

    @Test
    void enderecoSoAparecePraQuemEstaNaPropostaDepoisDoAceite() {
        PropostaDetalhada pendente = mariaPropoe(null, "Oi");
        assertNull(pendente.endereco());

        assertNotNull(responder.aceitar(pendente.id(), joao.getId()).endereco());
    }

    @Test
    void recusarNotificaQuemEnviou() {
        PropostaDetalhada proposta = mariaPropoe(null, "Proposta");

        assertEquals("RECUSADA", responder.recusar(proposta.id(), joao.getId()).status());
        assertTrue(anuncios.dados.get(pedidoDoJoao.getId()).isAtivo());
        assertEquals(TipoNotificacao.PROPOSTA_RECUSADA, notificacoesDe(maria).getFirst().getTipo());
    }

    @Test
    void soOAutorDoAnuncioResponde() {
        PropostaDetalhada proposta = mariaPropoe(null, "Proposta");

        assertThrows(AcessoNegadoPropostaException.class, () -> responder.aceitar(proposta.id(), maria.getId()));
        assertThrows(PropostaNaoEncontradaException.class, () -> responder.recusar(proposta.id(), pedro.getId()));
        assertThrows(PropostaNaoEncontradaException.class, () -> responder.aceitar(UUID.randomUUID(), joao.getId()));
    }

    @Test
    void respostaEhUnica() {
        PropostaDetalhada proposta = mariaPropoe(null, "Proposta");
        responder.recusar(proposta.id(), joao.getId());

        assertThrows(PropostaJaRespondidaException.class, () -> responder.aceitar(proposta.id(), joao.getId()));
        assertThrows(PropostaJaRespondidaException.class, () -> responder.cancelar(proposta.id(), maria.getId()));
    }

    @Test
    void soQuemEnviouCancela() {
        PropostaDetalhada proposta = mariaPropoe(null, "Proposta");

        assertThrows(AcessoNegadoPropostaException.class, () -> responder.cancelar(proposta.id(), joao.getId()));
        assertEquals("CANCELADA", responder.cancelar(proposta.id(), maria.getId()).status());
        assertThrows(PropostaJaRespondidaException.class, () -> responder.aceitar(proposta.id(), joao.getId()));
    }

    private static <T> Pagina<T> paginar(List<T> todos, Paginacao paginacao) {
        int inicio = Math.min(paginacao.pagina() * paginacao.tamanho(), todos.size());
        int fim = Math.min(inicio + paginacao.tamanho(), todos.size());
        int totalPaginas = (int) Math.ceil(todos.size() / (double) paginacao.tamanho());
        return new Pagina<>(todos.subList(inicio, fim), paginacao.pagina(), paginacao.tamanho(), todos.size(),
                totalPaginas);
    }

    private static class Anuncios implements AnuncioRepository {

        final Map<UUID, Anuncio> dados = new LinkedHashMap<>();

        Anuncio adicionar(UUID autorId, String titulo, boolean ativo) {
            return adicionar(autorId, titulo, ativo, TipoAnuncio.PEDIDO);
        }

        Anuncio adicionar(UUID autorId, String titulo, boolean ativo, TipoAnuncio tipo) {
            Anuncio anuncio = Anuncio.reconstituir(UUID.randomUUID(), autorId, UUID.randomUUID(), tipo,
                    titulo, "Descrição", null, new Localizacao("Salvador", "BA"), ativo, Instant.now(), Instant.now());
            salvar(anuncio);
            return anuncio;
        }

        @Override
        public void salvar(Anuncio anuncio) {
            dados.put(anuncio.getId(), anuncio);
        }

        @Override
        public Optional<Anuncio> buscarPorId(UUID id) {
            return Optional.ofNullable(dados.get(id));
        }

        @Override
        public Pagina<Anuncio> buscarAtivos(FiltroAnuncios filtro, Paginacao paginacao) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Pagina<Anuncio> buscarPorAutor(UUID autorId, Paginacao paginacao) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void excluir(UUID id) {
            dados.remove(id);
        }
    }

    private static class Usuarios implements UsuarioRepository {

        final Map<UUID, Usuario> dados = new LinkedHashMap<>();

        Usuario adicionar(String nome, String cpf, String email) {
            Usuario usuario = Usuario.criar(nome, LocalDate.of(2000, 1, 1), new Cpf(cpf), new Email(email),
                    new SenhaHash("hash"), "71900000000");
            usuario.aprovar();
            salvar(usuario);
            return usuario;
        }

        @Override
        public boolean existePorCpf(Cpf cpf) {
            return dados.values().stream().anyMatch(u -> u.getCpf().equals(cpf));
        }

        @Override
        public boolean existePorEmail(Email email) {
            return dados.values().stream().anyMatch(u -> u.getEmail().equals(email));
        }

        @Override
        public Optional<Usuario> buscarPorId(UUID id) {
            return Optional.ofNullable(dados.get(id));
        }

        @Override
        public Optional<Usuario> buscarPorCpf(String cpf) {
            return dados.values().stream().filter(u -> u.getCpf().equals(new Cpf(cpf))).findFirst();
        }

        @Override
        public Optional<Usuario> buscarPorEmail(Email email) {
            return dados.values().stream().filter(u -> u.getEmail().equals(email)).findFirst();
        }

        @Override
        public void salvar(Usuario usuario) {
            dados.put(usuario.getId(), usuario);
        }
    }

    private static class Propostas implements PropostaRepository {

        final Map<UUID, Proposta> dados = new LinkedHashMap<>();

        @Override
        public void salvar(Proposta proposta) {
            dados.put(proposta.getId(), proposta);
        }

        @Override
        public Optional<Proposta> buscarPorId(UUID id) {
            return Optional.ofNullable(dados.get(id));
        }

        @Override
        public List<Proposta> buscarPendentesPorAnuncio(UUID anuncioId) {
            return dados.values().stream().filter(p -> p.getAnuncioId().equals(anuncioId) && p.isPendente())
                    .toList();
        }

        @Override
        public boolean existePorAnuncioERemetente(UUID anuncioId, UUID remetenteId) {
            return dados.values().stream().anyMatch(p -> p.getAnuncioId().equals(anuncioId)
                    && p.getRemetenteId().equals(remetenteId));
        }

        @Override
        public Pagina<Proposta> buscarPorDestinatario(UUID destinatarioId, Paginacao paginacao) {
            return filtrar(p -> p.getDestinatarioId().equals(destinatarioId), paginacao);
        }

        @Override
        public Pagina<Proposta> buscarPorRemetente(UUID remetenteId, Paginacao paginacao) {
            return filtrar(p -> p.getRemetenteId().equals(remetenteId), paginacao);
        }

        private Pagina<Proposta> filtrar(Predicate<Proposta> condicao, Paginacao paginacao) {
            return paginar(dados.values().stream().filter(condicao)
                    .sorted(Comparator.comparing(Proposta::getCriadoEm).reversed()).toList(), paginacao);
        }
    }

    private static class Notificacoes implements NotificacaoRepository {

        final Map<UUID, Notificacao> dados = new LinkedHashMap<>();

        @Override
        public void salvar(Notificacao notificacao) {
            dados.put(notificacao.getId(), notificacao);
        }

        @Override
        public Optional<Notificacao> buscarPorId(UUID id) {
            return Optional.ofNullable(dados.get(id));
        }

        @Override
        public Pagina<Notificacao> buscarPorUsuario(UUID usuarioId, Paginacao paginacao) {
            return paginar(dados.values().stream().filter(n -> n.pertenceA(usuarioId))
                    .sorted(Comparator.comparing(Notificacao::getCriadoEm).reversed()).toList(), paginacao);
        }

        @Override
        public long contarNaoLidas(UUID usuarioId) {
            return dados.values().stream().filter(n -> n.pertenceA(usuarioId) && !n.isLida()).count();
        }

        @Override
        public void marcarTodasComoLidas(UUID usuarioId) {
            dados.values().stream().filter(n -> n.pertenceA(usuarioId)).forEach(Notificacao::marcarComoLida);
        }
    }
}
