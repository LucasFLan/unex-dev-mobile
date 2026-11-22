package com.unex.chamaAi.usuario.infrastructure.persistence;

import com.unex.chamaAi.usuario.domain.entities.CodigoRecuperacaoSenha;
import com.unex.chamaAi.usuario.domain.repositories.CodigoRecuperacaoSenhaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class CodigoRecuperacaoSenhaRepositoryJpa implements CodigoRecuperacaoSenhaRepository {

    private final CodigoRecuperacaoSenhaSpringDataRepository springData;

    public CodigoRecuperacaoSenhaRepositoryJpa(CodigoRecuperacaoSenhaSpringDataRepository springData) {
        this.springData = springData;
    }

    @Override
    public void salvar(CodigoRecuperacaoSenha codigo) {
        springData.saveAndFlush(new CodigoRecuperacaoSenhaJpaEntity(codigo.getId(), codigo.getUsuarioId(),
                codigo.getCodigoHash(), codigo.getExpiraEm(), codigo.getTentativas(), codigo.getUsadoEm(),
                codigo.getCriadoEm()));
    }

    @Override
    public Optional<CodigoRecuperacaoSenha> buscarMaisRecente(UUID usuarioId) {
        return springData.findFirstByUsuarioIdOrderByCriadoEmDesc(usuarioId)
                .map(entidade -> CodigoRecuperacaoSenha.reconstituir(entidade.getId(), entidade.getUsuarioId(),
                        entidade.getCodigoHash(), entidade.getExpiraEm(), entidade.getTentativas(),
                        entidade.getUsadoEm(), entidade.getCriadoEm()));
    }
}
