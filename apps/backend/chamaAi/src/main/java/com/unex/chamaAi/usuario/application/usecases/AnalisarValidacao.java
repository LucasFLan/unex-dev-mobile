package com.unex.chamaAi.usuario.application.usecases;

import com.unex.chamaAi.shared.domain.exceptions.DadoInvalidoException;
import com.unex.chamaAi.shared.domain.paginacao.Pagina;
import com.unex.chamaAi.shared.domain.paginacao.Paginacao;
import com.unex.chamaAi.usuario.application.dto.EnvioParaAnalise;
import com.unex.chamaAi.usuario.application.ports.NotificadorValidacao;
import com.unex.chamaAi.usuario.domain.entities.EnvioValidacao;
import com.unex.chamaAi.usuario.domain.entities.Usuario;
import com.unex.chamaAi.usuario.domain.exceptions.EnvioValidacaoNaoEncontradoException;
import com.unex.chamaAi.usuario.domain.exceptions.UsuarioNaoEncontradoException;
import com.unex.chamaAi.usuario.domain.repositories.EnvioValidacaoRepository;
import com.unex.chamaAi.usuario.domain.repositories.UsuarioRepository;
import com.unex.chamaAi.usuario.domain.valueobjects.ArquivoValidacao;
import com.unex.chamaAi.usuario.domain.valueobjects.StatusValidacao;
import com.unex.chamaAi.usuario.domain.valueobjects.TipoArquivoValidacao;

import java.time.Clock;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/** Lado do admin: fila de envios, imagens e decisão. As rotas já exigem papel ADMIN. */
public class AnalisarValidacao {

    private final UsuarioRepository usuarios;
    private final EnvioValidacaoRepository envios;
    private final NotificadorValidacao notificador;
    private final Clock relogio;

    public AnalisarValidacao(UsuarioRepository usuarios, EnvioValidacaoRepository envios,
                             NotificadorValidacao notificador, Clock relogio) {
        this.usuarios = usuarios;
        this.envios = envios;
        this.notificador = notificador;
        this.relogio = relogio;
    }

    /** status nulo = PENDENTE (a fila). */
    public Pagina<EnvioParaAnalise> listar(String status, Integer pagina, Integer tamanho) {
        StatusValidacao filtro = filtro(status);
        Map<UUID, Optional<Usuario>> cache = new HashMap<>();
        return envios.buscarPorStatus(filtro, Paginacao.de(pagina, tamanho))
                .mapear(envio -> EnvioParaAnalise.de(envio,
                        cache.computeIfAbsent(envio.getUsuarioId(), usuarios::buscarPorId).orElse(null)));
    }

    public EnvioParaAnalise detalhar(UUID envioId) {
        EnvioValidacao envio = envio(envioId);
        return EnvioParaAnalise.de(envio, usuarios.buscarPorId(envio.getUsuarioId()).orElse(null));
    }

    public ArquivoValidacao arquivo(UUID envioId, TipoArquivoValidacao tipo) {
        return envios.buscarArquivo(envioId, tipo).orElseThrow(EnvioValidacaoNaoEncontradoException::new);
    }

    /**
     * Aprova o envio e valida a conta. Se a conta já estava validada por outro caminho (ex.: virou admin
     * depois de enviar), só fecha o envio: nada muda na conta e ninguém é notificado de novo.
     */
    public EnvioParaAnalise aprovar(UUID envioId, UUID adminId) {
        EnvioValidacao envio = envio(envioId);
        Usuario usuario = usuarios.buscarPorId(envio.getUsuarioId()).orElseThrow(UsuarioNaoEncontradoException::new);

        envio.aprovar(adminId, relogio.instant());
        envios.salvar(envio);
        if (!usuario.isAprovado()) {
            usuario.aprovar();
            usuarios.salvar(usuario);
            notificador.contaAprovada(usuario.getId());
        }
        return EnvioParaAnalise.de(envio, usuario);
    }

    /** Reprova o envio. Conta já validada por outro caminho continua validada: só o envio é fechado. */
    public EnvioParaAnalise reprovar(UUID envioId, UUID adminId, String motivo) {
        EnvioValidacao envio = envio(envioId);
        Usuario usuario = usuarios.buscarPorId(envio.getUsuarioId()).orElseThrow(UsuarioNaoEncontradoException::new);

        envio.reprovar(adminId, motivo, relogio.instant());
        envios.salvar(envio);
        if (!usuario.isAprovado()) {
            usuario.reprovar();
            usuarios.salvar(usuario);
            notificador.contaReprovada(usuario.getId(), envio.getMotivoReprovacao().orElse(""));
        }
        return EnvioParaAnalise.de(envio, usuario);
    }

    private static StatusValidacao filtro(String status) {
        if (status == null || status.isBlank()) {
            return StatusValidacao.PENDENTE;
        }
        try {
            return StatusValidacao.valueOf(status.trim().toUpperCase(java.util.Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new DadoInvalidoException("Status inválido. Use PENDENTE, APROVADO ou REPROVADO.");
        }
    }

    private EnvioValidacao envio(UUID envioId) {
        return envios.buscarPorId(envioId).orElseThrow(EnvioValidacaoNaoEncontradoException::new);
    }
}
