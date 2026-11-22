package com.unex.chamaAi.usuario.application.usecases;

import com.unex.chamaAi.shared.domain.exceptions.DadoInvalidoException;
import com.unex.chamaAi.usuario.application.ports.EnviadorEmail;
import com.unex.chamaAi.usuario.application.ports.GeradorCodigo;
import com.unex.chamaAi.usuario.domain.entities.CodigoRecuperacaoSenha;
import com.unex.chamaAi.usuario.domain.entities.Usuario;
import com.unex.chamaAi.usuario.domain.exceptions.CodigoRecuperacaoInvalidoException;
import com.unex.chamaAi.usuario.domain.repositories.CodigoRecuperacaoSenhaRepository;
import com.unex.chamaAi.usuario.domain.repositories.UsuarioRepository;
import com.unex.chamaAi.usuario.domain.services.CriptografadorSenha;
import com.unex.chamaAi.usuario.domain.valueobjects.Email;
import com.unex.chamaAi.usuario.domain.valueobjects.Senha;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.HexFormat;
import java.util.Optional;

public class RecuperarSenha {

    /** Intervalo mínimo entre dois envios para o mesmo usuário. */
    public static final Duration INTERVALO_REENVIO = Duration.ofSeconds(60);

    private static final Logger LOG = LoggerFactory.getLogger(RecuperarSenha.class);

    private final UsuarioRepository usuarios;
    private final CodigoRecuperacaoSenhaRepository codigos;
    private final CriptografadorSenha criptografador;
    private final EnviadorEmail enviadorEmail;
    private final GeradorCodigo geradorCodigo;
    private final Clock relogio;

    public RecuperarSenha(UsuarioRepository usuarios, CodigoRecuperacaoSenhaRepository codigos,
                          CriptografadorSenha criptografador, EnviadorEmail enviadorEmail,
                          GeradorCodigo geradorCodigo, Clock relogio) {
        this.usuarios = usuarios;
        this.codigos = codigos;
        this.criptografador = criptografador;
        this.enviadorEmail = enviadorEmail;
        this.geradorCodigo = geradorCodigo;
        this.relogio = relogio;
    }

    /** Passo 1. Envia um código novo (anulando o anterior). E-mail desconhecido ou inválido: não faz nada. */
    public void solicitar(String email) {
        Optional<Usuario> encontrado = buscarUsuario(email);
        if (encontrado.isEmpty()) {
            return;
        }
        Usuario usuario = encontrado.get();
        Instant agora = relogio.instant();

        Optional<CodigoRecuperacaoSenha> anterior = codigos.buscarMaisRecente(usuario.getId());
        if (anterior.isPresent() && anterior.get().getCriadoEm().plus(INTERVALO_REENVIO).isAfter(agora)) {
            return; // Pediu agora há pouco: o código anterior continua valendo.
        }
        anterior.ifPresent(codigo -> {
            codigo.invalidar(agora);
            codigos.salvar(codigo);
        });

        String codigo = geradorCodigo.gerar();
        codigos.salvar(CodigoRecuperacaoSenha.criar(usuario.getId(), hash(codigo), agora));
        enviarEmail(usuario, codigo);
    }

    /** Passo 2. Só confere; o código continua valendo para o passo 3. */
    public void validar(String email, String codigo) {
        codigoConferido(email, codigo);
    }

    /** Passo 3. Troca a senha e consome o código. */
    public void redefinir(String email, String codigo, String novaSenha) {
        Senha senha = new Senha(novaSenha); // Regras da senha primeiro: não gasta tentativa à toa.
        CodigoRecuperacaoSenha conferido = codigoConferido(email, codigo);
        Usuario usuario = usuarios.buscarPorId(conferido.getUsuarioId())
                .orElseThrow(CodigoRecuperacaoInvalidoException::new);

        usuario.alterarSenha(criptografador.criptografar(senha));
        usuarios.salvar(usuario);
        conferido.usar(relogio.instant());
        codigos.salvar(conferido);
    }

    private CodigoRecuperacaoSenha codigoConferido(String email, String codigo) {
        Usuario usuario = buscarUsuario(email).orElseThrow(CodigoRecuperacaoInvalidoException::new);
        CodigoRecuperacaoSenha atual = codigos.buscarMaisRecente(usuario.getId())
                .orElseThrow(CodigoRecuperacaoInvalidoException::new);

        boolean confere = atual.conferir(hash(normalizar(codigo)), relogio.instant());
        if (!confere) {
            codigos.salvar(atual); // Guarda a tentativa errada.
            throw new CodigoRecuperacaoInvalidoException();
        }
        return atual;
    }

    private Optional<Usuario> buscarUsuario(String email) {
        try {
            return usuarios.buscarPorEmail(new Email(email));
        } catch (DadoInvalidoException e) {
            return Optional.empty();
        }
    }

    private void enviarEmail(Usuario usuario, String codigo) {
        String texto = """
                Olá, %s!

                Recebemos um pedido para redefinir a senha da sua conta no ChamaAí.
                Use este código para continuar:

                    %s

                O código vale por %d minutos e só pode ser usado uma vez.
                Se não foi você que pediu, ignore este e-mail: sua senha continua a mesma.

                Equipe ChamaAí""".formatted(primeiroNome(usuario.getNome()), codigo,
                CodigoRecuperacaoSenha.VALIDADE.toMinutes());
        try {
            enviadorEmail.enviar(usuario.getEmail().valor(), "Seu código para redefinir a senha - ChamaAí", texto);
        } catch (RuntimeException e) {
            // A resposta não pode mudar por causa do envio (revelaria que o e-mail existe): só registra.
            LOG.error("Falha ao enviar o código de recuperação de senha.", e);
        }
    }

    private static String primeiroNome(String nome) {
        return nome.trim().split("\\s+")[0];
    }

    private static String normalizar(String codigo) {
        return codigo == null ? "" : codigo.replaceAll("\\s", "");
    }

    static String hash(String codigo) {
        try {
            byte[] bytes = MessageDigest.getInstance("SHA-256").digest(codigo.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(bytes);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 indisponível.", e);
        }
    }
}
