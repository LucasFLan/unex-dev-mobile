package com.unex.chamaAi.usuario.application.usecases;

import com.unex.chamaAi.shared.domain.exceptions.DadoInvalidoException;
import com.unex.chamaAi.usuario.application.ports.EnviadorEmail;
import com.unex.chamaAi.usuario.domain.entities.CodigoRecuperacaoSenha;
import com.unex.chamaAi.usuario.domain.entities.Usuario;
import com.unex.chamaAi.usuario.domain.exceptions.CodigoRecuperacaoInvalidoException;
import com.unex.chamaAi.usuario.domain.repositories.CodigoRecuperacaoSenhaRepository;
import com.unex.chamaAi.usuario.domain.valueobjects.Cpf;
import com.unex.chamaAi.usuario.domain.valueobjects.Email;
import com.unex.chamaAi.usuario.domain.valueobjects.SenhaHash;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class RecuperarSenhaTest {

    private static final String EMAIL = "joao@unex.edu.br";
    private static final String NOVA_SENHA = "NovaSenha456@";

    private final UsuarioRepositoryEmMemoria usuarios = new UsuarioRepositoryEmMemoria();
    private final Codigos codigos = new Codigos();
    private final EmailsEnviados emails = new EmailsEnviados();
    private final RelogioAjustavel relogio = new RelogioAjustavel(Instant.parse("2026-11-22T12:00:00Z"));
    private String proximoCodigo = "123456";

    private final RecuperarSenha recuperar = new RecuperarSenha(usuarios, codigos, new CriptografadorSenhaFake(),
            emails, () -> proximoCodigo, relogio);

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = Usuario.criar("João da Silva", LocalDate.of(2000, 1, 1), new Cpf("529.982.247-25"),
                new Email(EMAIL), new SenhaHash("hash:SenhaAntiga1!"), "71999999999");
        usuarios.salvar(usuario);
    }

    @Test
    void enviaCodigoPorEmailEGuardaSoOHash() {
        recuperar.solicitar(EMAIL);

        assertEquals(1, emails.enviados.size());
        EmailsEnviados.Enviado enviado = emails.enviados.getFirst();
        assertEquals(EMAIL, enviado.destinatario());
        assertTrue(enviado.texto().contains("123456"));
        assertTrue(enviado.texto().startsWith("Olá, João!"));

        CodigoRecuperacaoSenha salvo = codigos.dados.values().iterator().next();
        assertNotEquals("123456", salvo.getCodigoHash());
        assertEquals(64, salvo.getCodigoHash().length());
        assertEquals(relogio.instant().plus(CodigoRecuperacaoSenha.VALIDADE), salvo.getExpiraEm());
    }

    @Test
    void emailDesconhecidoOuInvalidoNaoFazNadaENaoDaErro() {
        recuperar.solicitar("ninguem@unex.edu.br");
        recuperar.solicitar("nao-e-email");
        recuperar.solicitar(null);

        assertTrue(emails.enviados.isEmpty());
        assertTrue(codigos.dados.isEmpty());
    }

    @Test
    void pedidoRepetidoEmMenosDeUmMinutoEhIgnorado() {
        recuperar.solicitar(EMAIL);
        relogio.avancar(Duration.ofSeconds(30));
        recuperar.solicitar(EMAIL);

        assertEquals(1, emails.enviados.size());
    }

    @Test
    void codigoNovoAnulaOAnterior() {
        recuperar.solicitar(EMAIL);
        relogio.avancar(Duration.ofMinutes(2));
        proximoCodigo = "654321";
        recuperar.solicitar(EMAIL);

        assertThrows(CodigoRecuperacaoInvalidoException.class, () -> recuperar.validar(EMAIL, "123456"));
        assertDoesNotThrow(() -> recuperar.validar(EMAIL, "654321"));
    }

    @Test
    void validarNaoConsomeERedefinirTrocaASenhaEConsome() {
        recuperar.solicitar(EMAIL);

        recuperar.validar(EMAIL, " 123 456 ");
        recuperar.redefinir(EMAIL, "123456", NOVA_SENHA);

        assertEquals("hash:" + NOVA_SENHA, usuarios.buscarPorId(usuario.getId()).orElseThrow().getSenha().valor());
        assertThrows(CodigoRecuperacaoInvalidoException.class,
                () -> recuperar.redefinir(EMAIL, "123456", "OutraSenha789#"));
    }

    @Test
    void codigoExpiraEmQuinzeMinutos() {
        recuperar.solicitar(EMAIL);
        relogio.avancar(CodigoRecuperacaoSenha.VALIDADE);

        assertThrows(CodigoRecuperacaoInvalidoException.class, () -> recuperar.validar(EMAIL, "123456"));
    }

    @Test
    void bloqueiaDepoisDeCincoErros() {
        recuperar.solicitar(EMAIL);
        for (int i = 0; i < CodigoRecuperacaoSenha.MAXIMO_TENTATIVAS; i++) {
            assertThrows(CodigoRecuperacaoInvalidoException.class, () -> recuperar.validar(EMAIL, "000000"));
        }

        assertThrows(CodigoRecuperacaoInvalidoException.class, () -> recuperar.validar(EMAIL, "123456"));
    }

    @Test
    void senhaFracaNaoGastaTentativa() {
        recuperar.solicitar(EMAIL);

        assertThrows(DadoInvalidoException.class, () -> recuperar.redefinir(EMAIL, "000000", "fraca"));
        assertEquals(0, codigos.dados.values().iterator().next().getTentativas());
        assertEquals("hash:SenhaAntiga1!", usuarios.buscarPorId(usuario.getId()).orElseThrow().getSenha().valor());
    }

    @Test
    void semCodigoOuEmailDesconhecidoDaAMesmaMensagem() {
        CodigoRecuperacaoInvalidoException semCodigo = assertThrows(CodigoRecuperacaoInvalidoException.class,
                () -> recuperar.validar(EMAIL, "123456"));
        CodigoRecuperacaoInvalidoException desconhecido = assertThrows(CodigoRecuperacaoInvalidoException.class,
                () -> recuperar.validar("ninguem@unex.edu.br", "123456"));
        assertEquals(semCodigo.getMessage(), desconhecido.getMessage());
    }

    @Test
    void falhaNoEnvioNaoMudaAResposta() {
        RecuperarSenha comFalha = new RecuperarSenha(usuarios, codigos, new CriptografadorSenhaFake(),
                (destinatario, assunto, texto) -> {
                    throw new IllegalStateException("SMTP fora do ar");
                }, () -> "999999", relogio);

        assertDoesNotThrow(() -> comFalha.solicitar(EMAIL));
    }

    private static final class Codigos implements CodigoRecuperacaoSenhaRepository {

        final Map<UUID, CodigoRecuperacaoSenha> dados = new LinkedHashMap<>();

        @Override
        public void salvar(CodigoRecuperacaoSenha codigo) {
            dados.put(codigo.getId(), codigo);
        }

        @Override
        public Optional<CodigoRecuperacaoSenha> buscarMaisRecente(UUID usuarioId) {
            return dados.values().stream().filter(c -> c.getUsuarioId().equals(usuarioId))
                    .max(Comparator.comparing(CodigoRecuperacaoSenha::getCriadoEm));
        }
    }

    private static final class EmailsEnviados implements EnviadorEmail {

        record Enviado(String destinatario, String assunto, String texto) {
        }

        final List<Enviado> enviados = new ArrayList<>();

        @Override
        public void enviar(String destinatario, String assunto, String texto) {
            enviados.add(new Enviado(destinatario, assunto, texto));
        }
    }

    private static final class RelogioAjustavel extends Clock {

        private Instant agora;

        RelogioAjustavel(Instant agora) {
            this.agora = agora;
        }

        void avancar(Duration duracao) {
            agora = agora.plus(duracao);
        }

        @Override
        public ZoneId getZone() {
            return ZoneOffset.UTC;
        }

        @Override
        public Clock withZone(ZoneId zone) {
            return this;
        }

        @Override
        public Instant instant() {
            return agora;
        }
    }
}
