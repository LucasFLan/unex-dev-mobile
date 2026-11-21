package com.unex.chamaAi.notificacao.infrastructure.persistence;

import com.unex.chamaAi.notificacao.domain.entities.Notificacao;
import com.unex.chamaAi.notificacao.domain.repositories.NotificacaoRepository;
import com.unex.chamaAi.notificacao.domain.valueobjects.TipoNotificacao;
import com.unex.chamaAi.shared.domain.paginacao.Pagina;
import com.unex.chamaAi.shared.domain.paginacao.Paginacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class NotificacaoRepositoryJpa implements NotificacaoRepository {

    /** id como desempate garante ordem estável entre páginas quando criado_em coincide. */
    private static final Sort MAIS_RECENTES = Sort.by(Sort.Order.desc("criadoEm"), Sort.Order.desc("id"));

    private final NotificacaoSpringDataRepository springData;

    public NotificacaoRepositoryJpa(NotificacaoSpringDataRepository springData) {
        this.springData = springData;
    }

    @Override
    public void salvar(Notificacao notificacao) {
        springData.saveAndFlush(new NotificacaoJpaEntity(notificacao.getId(), notificacao.getUsuarioId(),
                notificacao.getTipo().name(), notificacao.getTitulo(), notificacao.getMensagem(),
                notificacao.getAnuncioId(), notificacao.getPropostaId(), notificacao.isLida(),
                notificacao.getCriadoEm()));
    }

    @Override
    public Optional<Notificacao> buscarPorId(UUID id) {
        return springData.findById(id).map(NotificacaoRepositoryJpa::paraDominio);
    }

    @Override
    public Pagina<Notificacao> buscarPorUsuario(UUID usuarioId, Paginacao paginacao) {
        Page<NotificacaoJpaEntity> pagina = springData.findByUsuarioId(usuarioId,
                PageRequest.of(paginacao.pagina(), paginacao.tamanho(), MAIS_RECENTES));
        return new Pagina<>(pagina.getContent().stream().map(NotificacaoRepositoryJpa::paraDominio).toList(),
                pagina.getNumber(), pagina.getSize(), pagina.getTotalElements(), pagina.getTotalPages());
    }

    @Override
    public long contarNaoLidas(UUID usuarioId) {
        return springData.countByUsuarioIdAndLidaFalse(usuarioId);
    }

    @Override
    public void marcarTodasComoLidas(UUID usuarioId) {
        springData.marcarTodasComoLidas(usuarioId);
    }

    private static Notificacao paraDominio(NotificacaoJpaEntity entidade) {
        return Notificacao.reconstituir(entidade.getId(), entidade.getUsuarioId(),
                TipoNotificacao.valueOf(entidade.getTipo()), entidade.getTitulo(), entidade.getMensagem(),
                entidade.getAnuncioId(), entidade.getPropostaId(), entidade.isLida(), entidade.getCriadoEm());
    }
}
