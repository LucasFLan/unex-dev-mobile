package com.unex.chamaAi.usuario.infrastructure.persistence;

import com.unex.chamaAi.shared.domain.paginacao.Pagina;
import com.unex.chamaAi.shared.domain.paginacao.Paginacao;
import com.unex.chamaAi.usuario.domain.entities.EnvioValidacao;
import com.unex.chamaAi.usuario.domain.repositories.EnvioValidacaoRepository;
import com.unex.chamaAi.usuario.domain.valueobjects.ArquivoValidacao;
import com.unex.chamaAi.usuario.domain.valueobjects.StatusValidacao;
import com.unex.chamaAi.usuario.domain.valueobjects.TipoArquivoValidacao;
import com.unex.chamaAi.usuario.infrastructure.storage.ArmazenamentoLocalArquivosValidacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class EnvioValidacaoRepositoryJpa implements EnvioValidacaoRepository {

    private final EnvioValidacaoSpringDataRepository envios;
    private final ArquivoValidacaoSpringDataRepository arquivos;
    private final ArmazenamentoLocalArquivosValidacao armazenamento;

    public EnvioValidacaoRepositoryJpa(EnvioValidacaoSpringDataRepository envios,
                                       ArquivoValidacaoSpringDataRepository arquivos,
                                       ArmazenamentoLocalArquivosValidacao armazenamento) {
        this.envios = envios;
        this.arquivos = arquivos;
        this.armazenamento = armazenamento;
    }

    @Override
    public void salvar(EnvioValidacao envio) {
        envios.saveAndFlush(new EnvioValidacaoJpaEntity(envio.getId(), envio.getUsuarioId(), envio.getStatus().name(),
                envio.getMotivoReprovacao().orElse(null), envio.getEnviadoEm(), envio.getAnalisadoEm().orElse(null),
                envio.getAnalisadoPor().orElse(null)));
    }

    @Override
    public void salvarArquivos(UUID envioId, List<ArquivoValidacao> lista) {
        arquivos.saveAllAndFlush(lista.stream()
                .map(arquivo -> new ArquivoValidacaoJpaEntity(envioId, arquivo.tipo().name(), arquivo.contentType(),
                        armazenamento.gravar(envioId, arquivo)))
                .toList());
    }

    @Override
    public Optional<EnvioValidacao> buscarPorId(UUID id) {
        return envios.findById(id).map(EnvioValidacaoRepositoryJpa::paraDominio);
    }

    @Override
    public Optional<EnvioValidacao> buscarMaisRecente(UUID usuarioId) {
        return envios.findFirstByUsuarioIdOrderByEnviadoEmDesc(usuarioId).map(EnvioValidacaoRepositoryJpa::paraDominio);
    }

    @Override
    public Pagina<EnvioValidacao> buscarPorStatus(StatusValidacao status, Paginacao paginacao) {
        // Fila: quem enviou primeiro é analisado primeiro. Já analisados: os mais recentes primeiro.
        Sort ordem = status == StatusValidacao.PENDENTE
                ? Sort.by(Sort.Order.asc("enviadoEm"), Sort.Order.asc("id"))
                : Sort.by(Sort.Order.desc("analisadoEm"), Sort.Order.desc("id"));
        Page<EnvioValidacaoJpaEntity> pagina = envios.findByStatus(status.name(),
                PageRequest.of(paginacao.pagina(), paginacao.tamanho(), ordem));
        return new Pagina<>(pagina.getContent().stream().map(EnvioValidacaoRepositoryJpa::paraDominio).toList(),
                pagina.getNumber(), pagina.getSize(), pagina.getTotalElements(), pagina.getTotalPages());
    }

    @Override
    public Optional<ArquivoValidacao> buscarArquivo(UUID envioId, TipoArquivoValidacao tipo) {
        return arquivos.findById(new ArquivoValidacaoJpaEntity.Chave(envioId, tipo.name()))
                .flatMap(entidade -> armazenamento.ler(entidade.getCaminho()))
                .map(conteudo -> ArquivoValidacao.de(tipo, conteudo));
    }

    private static EnvioValidacao paraDominio(EnvioValidacaoJpaEntity entidade) {
        return EnvioValidacao.reconstituir(entidade.getId(), entidade.getUsuarioId(),
                StatusValidacao.valueOf(entidade.getStatus()), entidade.getMotivoReprovacao(), entidade.getEnviadoEm(),
                entidade.getAnalisadoEm(), entidade.getAnalisadoPor());
    }
}
