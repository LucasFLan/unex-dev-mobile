package com.unex.chamaAi.usuario.application.usecases;

import com.unex.chamaAi.usuario.application.dto.MinhaValidacao;
import com.unex.chamaAi.usuario.domain.entities.EnvioValidacao;
import com.unex.chamaAi.usuario.domain.entities.Usuario;
import com.unex.chamaAi.usuario.domain.exceptions.ContaJaValidadaException;
import com.unex.chamaAi.usuario.domain.exceptions.DocumentosEmAnaliseException;
import com.unex.chamaAi.usuario.domain.exceptions.UsuarioNaoEncontradoException;
import com.unex.chamaAi.usuario.domain.repositories.EnvioValidacaoRepository;
import com.unex.chamaAi.usuario.domain.repositories.UsuarioRepository;
import com.unex.chamaAi.usuario.domain.valueobjects.ArquivoValidacao;
import com.unex.chamaAi.usuario.domain.valueobjects.StatusValidacao;
import com.unex.chamaAi.usuario.domain.valueobjects.TipoArquivoValidacao;

import java.time.Clock;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Lado do usuário: mandar a foto do documento e a selfie, e acompanhar a análise. */
public class ValidarConta {

    private final UsuarioRepository usuarios;
    private final EnvioValidacaoRepository envios;
    private final Clock relogio;

    public ValidarConta(UsuarioRepository usuarios, EnvioValidacaoRepository envios, Clock relogio) {
        this.usuarios = usuarios;
        this.envios = envios;
        this.relogio = relogio;
    }

    /**
     * Manda os documentos para a fila do admin. Só é possível sem envio em análise; depois de uma reprovação,
     * o usuário pode reenviar e volta para PENDENTE.
     */
    public MinhaValidacao enviar(UUID usuarioId, byte[] documento, byte[] selfie) {
        Usuario usuario = usuarios.buscarPorId(usuarioId).orElseThrow(UsuarioNaoEncontradoException::new);
        if (usuario.isAprovado()) {
            throw new ContaJaValidadaException();
        }
        if (envios.buscarMaisRecente(usuarioId).filter(EnvioValidacao::isPendente).isPresent()) {
            throw new DocumentosEmAnaliseException();
        }
        List<ArquivoValidacao> arquivos = List.of(
                ArquivoValidacao.de(TipoArquivoValidacao.DOCUMENTO, documento),
                ArquivoValidacao.de(TipoArquivoValidacao.SELFIE, selfie));

        if (usuario.getStatusValidacao() == StatusValidacao.REPROVADO) {
            usuario.voltarParaAnalise();
            usuarios.salvar(usuario);
        }
        EnvioValidacao envio = EnvioValidacao.criar(usuarioId, relogio.instant());
        envios.salvar(envio);
        envios.salvarArquivos(envio.getId(), arquivos);
        return situacao(usuario, Optional.of(envio));
    }

    public MinhaValidacao consultar(UUID usuarioId) {
        Usuario usuario = usuarios.buscarPorId(usuarioId).orElseThrow(UsuarioNaoEncontradoException::new);
        return situacao(usuario, envios.buscarMaisRecente(usuarioId));
    }

    private static MinhaValidacao situacao(Usuario usuario, Optional<EnvioValidacao> envio) {
        EnvioValidacao ultimo = envio.orElse(null);
        String situacao;
        if (usuario.isAprovado()) {
            situacao = MinhaValidacao.APROVADO;
        } else if (ultimo == null) {
            situacao = MinhaValidacao.NAO_ENVIADO;
        } else if (ultimo.isPendente()) {
            situacao = MinhaValidacao.EM_ANALISE;
        } else if (ultimo.getStatus() == StatusValidacao.REPROVADO) {
            situacao = MinhaValidacao.REPROVADO;
        } else {
            situacao = MinhaValidacao.NAO_ENVIADO;
        }
        return new MinhaValidacao(situacao, ultimo == null ? null : ultimo.getEnviadoEm(),
                ultimo == null ? null : ultimo.getAnalisadoEm().orElse(null),
                MinhaValidacao.REPROVADO.equals(situacao) ? ultimo.getMotivoReprovacao().orElse(null) : null);
    }
}
