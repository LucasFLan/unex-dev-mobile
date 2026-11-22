package com.unex.chamaAi.usuario.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ArquivoValidacaoSpringDataRepository
        extends JpaRepository<ArquivoValidacaoJpaEntity, ArquivoValidacaoJpaEntity.Chave> {
}
