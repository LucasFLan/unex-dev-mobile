package com.unex.chamaAi.usuario.application.usecases;

import com.unex.chamaAi.shared.domain.exceptions.DadoInvalidoException;
import com.unex.chamaAi.shared.domain.paginacao.Pagina;
import com.unex.chamaAi.shared.domain.paginacao.Paginacao;
import com.unex.chamaAi.usuario.application.dto.EnvioParaAnalise;
import com.unex.chamaAi.usuario.application.dto.MinhaValidacao;
import com.unex.chamaAi.usuario.application.ports.NotificadorValidacao;
import com.unex.chamaAi.usuario.domain.entities.EnvioValidacao;
import com.unex.chamaAi.usuario.domain.entities.Usuario;
import com.unex.chamaAi.usuario.domain.exceptions.ContaJaValidadaException;
import com.unex.chamaAi.usuario.domain.exceptions.DocumentosEmAnaliseException;
import com.unex.chamaAi.usuario.domain.exceptions.EnvioValidacaoJaAnalisadoException;
import com.unex.chamaAi.usuario.domain.repositories.EnvioValidacaoRepository;
import com.unex.chamaAi.usuario.domain.valueobjects.ArquivoValidacao;
import com.unex.chamaAi.usuario.domain.valueobjects.Cpf;
import com.unex.chamaAi.usuario.domain.valueobjects.Email;
import com.unex.chamaAi.usuario.domain.valueobjects.SenhaHash;
import com.unex.chamaAi.usuario.domain.valueobjects.StatusValidacao;
import com.unex.chamaAi.usuario.domain.valueobjects.TipoArquivoValidacao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ValidacaoContaTest {

    /** Início de um JPEG e de um PNG: o tipo é conferido pelos primeiros bytes. */
    static final byte[] JPEG = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0, 0, 0x10, 'J', 'F', 'I', 'F'};
    static final byte[] PNG = {(byte) 0x89, 'P', 'N', 'G', 0x0D, 0x0A, 0x1A, 0x0A, 0, 0};

    private static final UUID ADMIN = UUID.randomUUID();

    private final UsuarioRepositoryEmMemoria usuarios = new UsuarioRepositoryEmMemoria();
    private final Envios envios = new Envios();
    private final Avisos avisos = new Avisos();
    private final Clock relogio = Clock.fixed(Instant.parse("2026-11-22T12:00:00Z"), ZoneOffset.UTC);

    private final ValidarConta validar = new ValidarConta(usuarios, envios, relogio);
    private final AnalisarValidacao analisar = new AnalisarValidacao(usuarios, envios, avisos, relogio);

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = Usuario.criar("João da Silva", LocalDate.of(2000, 1, 1), new Cpf("529.982.247-25"),
                new Email("joao@unex.edu.br"), new SenhaHash("hash"), "71999999999");
        usuarios.salvar(usuario);
    }

    private StatusValidacao statusDoUsuario() {
        return usuarios.buscarPorId(usuario.getId()).orElseThrow().getStatusValidacao();
    }

    @Test
    void contaNovaAindaNaoEnviou() {
        MinhaValidacao situacao = validar.consultar(usuario.getId());

        assertEquals(MinhaValidacao.NAO_ENVIADO, situacao.situacao());
        assertTrue(situacao.podeEnviar());
    }

    @Test
    void enviaDocumentoESelfieEEntraNaFila() {
        MinhaValidacao situacao = validar.enviar(usuario.getId(), JPEG, PNG);

        assertEquals(MinhaValidacao.EM_ANALISE, situacao.situacao());
        assertFalse(situacao.podeEnviar());
        EnvioValidacao envio = envios.dados.values().iterator().next();
        assertEquals("image/jpeg", envios.arquivos.get(envio.getId() + "DOCUMENTO").contentType());
        assertEquals("image/png", envios.arquivos.get(envio.getId() + "SELFIE").contentType());
        assertEquals(1, analisar.listar(null, null, null).totalItens());
    }

    @Test
    void naoAceitaImagemInvalidaNemFaltando() {
        byte[] pdf = "%PDF-1.7".getBytes();

        assertThrows(DadoInvalidoException.class, () -> validar.enviar(usuario.getId(), pdf, PNG));
        assertThrows(DadoInvalidoException.class, () -> validar.enviar(usuario.getId(), JPEG, null));
        assertThrows(DadoInvalidoException.class,
                () -> validar.enviar(usuario.getId(), new byte[ArquivoValidacao.TAMANHO_MAXIMO_BYTES + 1], PNG));
        assertTrue(envios.dados.isEmpty());
    }

    @Test
    void naoReenviaEnquantoEmAnalise() {
        validar.enviar(usuario.getId(), JPEG, PNG);

        assertThrows(DocumentosEmAnaliseException.class, () -> validar.enviar(usuario.getId(), JPEG, PNG));
    }

    @Test
    void aprovarValidaAContaENotifica() {
        validar.enviar(usuario.getId(), JPEG, PNG);
        UUID envioId = envios.dados.keySet().iterator().next();

        EnvioParaAnalise aprovado = analisar.aprovar(envioId, ADMIN);

        assertEquals("APROVADO", aprovado.status());
        assertEquals("João da Silva", aprovado.usuario().nome());
        assertEquals(StatusValidacao.APROVADO, statusDoUsuario());
        assertEquals(MinhaValidacao.APROVADO, validar.consultar(usuario.getId()).situacao());
        assertEquals(List.of("aprovada:" + usuario.getId()), avisos.registro);
        assertThrows(ContaJaValidadaException.class, () -> validar.enviar(usuario.getId(), JPEG, PNG));
        assertThrows(EnvioValidacaoJaAnalisadoException.class, () -> analisar.reprovar(envioId, ADMIN, "Motivo qualquer"));
    }

    @Test
    void contaValidadaDepoisDoEnvioSoFechaOEnvio() {
        validar.enviar(usuario.getId(), JPEG, PNG);
        usuario.promoverParaAdmin(); // Virou admin (e foi aprovado) com o envio ainda na fila.
        usuarios.salvar(usuario);
        UUID envioId = envios.dados.keySet().iterator().next();

        EnvioParaAnalise aprovado = analisar.aprovar(envioId, ADMIN);

        assertEquals("APROVADO", aprovado.status());
        assertEquals(StatusValidacao.APROVADO, statusDoUsuario());
        assertEquals(0, analisar.listar(null, null, null).totalItens());
        assertTrue(avisos.registro.isEmpty());
    }

    @Test
    void reprovarEnvioDeContaJaValidadaMantemAConta() {
        validar.enviar(usuario.getId(), JPEG, PNG);
        usuario.promoverParaAdmin();
        usuarios.salvar(usuario);
        UUID envioId = envios.dados.keySet().iterator().next();

        assertEquals("REPROVADO", analisar.reprovar(envioId, ADMIN, "Foto desfocada").status());
        assertEquals(StatusValidacao.APROVADO, statusDoUsuario());
        assertTrue(avisos.registro.isEmpty());
    }

    @Test
    void reprovarExigeMotivoEPermiteReenviar() {
        validar.enviar(usuario.getId(), JPEG, PNG);
        UUID envioId = envios.dados.keySet().iterator().next();

        assertThrows(DadoInvalidoException.class, () -> analisar.reprovar(envioId, ADMIN, "  "));
        analisar.reprovar(envioId, ADMIN, "Foto desfocada");

        MinhaValidacao situacao = validar.consultar(usuario.getId());
        assertEquals(MinhaValidacao.REPROVADO, situacao.situacao());
        assertEquals("Foto desfocada", situacao.motivoReprovacao());
        assertTrue(situacao.podeEnviar());
        assertEquals(StatusValidacao.REPROVADO, statusDoUsuario());
        assertEquals(List.of("reprovada:" + usuario.getId() + ":Foto desfocada"), avisos.registro);

        assertEquals(MinhaValidacao.EM_ANALISE, validar.enviar(usuario.getId(), JPEG, PNG).situacao());
        assertEquals(StatusValidacao.PENDENTE, statusDoUsuario());
        assertEquals(1, analisar.listar("PENDENTE", null, null).totalItens());
        assertEquals(1, analisar.listar("reprovado", null, null).totalItens());
        assertThrows(DadoInvalidoException.class, () -> analisar.listar("OUTRO", null, null));
    }

    private static final class Envios implements EnvioValidacaoRepository {

        final Map<UUID, EnvioValidacao> dados = new LinkedHashMap<>();
        final Map<String, ArquivoValidacao> arquivos = new HashMap<>();

        @Override
        public void salvar(EnvioValidacao envio) {
            dados.put(envio.getId(), envio);
        }

        @Override
        public void salvarArquivos(UUID envioId, List<ArquivoValidacao> lista) {
            lista.forEach(arquivo -> arquivos.put(envioId + arquivo.tipo().name(), arquivo));
        }

        @Override
        public Optional<EnvioValidacao> buscarPorId(UUID id) {
            return Optional.ofNullable(dados.get(id));
        }

        @Override
        public Optional<EnvioValidacao> buscarMaisRecente(UUID usuarioId) {
            return dados.values().stream().filter(e -> e.getUsuarioId().equals(usuarioId))
                    .reduce((primeiro, segundo) -> segundo);
        }

        @Override
        public Pagina<EnvioValidacao> buscarPorStatus(StatusValidacao status, Paginacao paginacao) {
            List<EnvioValidacao> lista = dados.values().stream().filter(e -> e.getStatus() == status)
                    .sorted(Comparator.comparing(EnvioValidacao::getEnviadoEm)).toList();
            return new Pagina<>(lista, 0, paginacao.tamanho(), lista.size(), 1);
        }

        @Override
        public Optional<ArquivoValidacao> buscarArquivo(UUID envioId, TipoArquivoValidacao tipo) {
            return Optional.ofNullable(arquivos.get(envioId + tipo.name()));
        }
    }

    private static final class Avisos implements NotificadorValidacao {

        final List<String> registro = new ArrayList<>();

        @Override
        public void contaAprovada(UUID usuarioId) {
            registro.add("aprovada:" + usuarioId);
        }

        @Override
        public void contaReprovada(UUID usuarioId, String motivo) {
            registro.add("reprovada:" + usuarioId + ":" + motivo);
        }
    }
}
