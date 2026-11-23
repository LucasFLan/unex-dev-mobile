-- Serviço concluído: depois de ACEITA, quem contratou marca a proposta como CONCLUIDA.
ALTER TABLE propostas DROP CONSTRAINT ck_propostas_status;
ALTER TABLE propostas ADD CONSTRAINT ck_propostas_status
    CHECK (status IN ('PENDENTE', 'ACEITA', 'RECUSADA', 'NAO_SELECIONADA', 'CANCELADA', 'CONCLUIDA'));
ALTER TABLE propostas ADD COLUMN concluido_em TIMESTAMPTZ;

-- Avaliação de quem contratou para o profissional, uma por serviço concluído.
-- Sobrevive à exclusão do anúncio (que apaga as propostas): por isso guarda o título e a categoria do serviço,
-- e a proposta vira NULL. Se quem avaliou apagar a conta, a avaliação fica, sem autor.
CREATE TABLE avaliacoes (
    id                UUID          PRIMARY KEY,
    proposta_id       UUID,
    avaliador_id      UUID,
    avaliado_id       UUID          NOT NULL,
    nota              SMALLINT      NOT NULL,
    comentario        VARCHAR(500),
    servico_titulo    VARCHAR(120)  NOT NULL,
    servico_categoria VARCHAR(100),
    criado_em         TIMESTAMPTZ   NOT NULL,
    CONSTRAINT fk_avaliacoes_proposta FOREIGN KEY (proposta_id) REFERENCES propostas (id) ON DELETE SET NULL,
    CONSTRAINT fk_avaliacoes_avaliador FOREIGN KEY (avaliador_id) REFERENCES usuarios (id) ON DELETE SET NULL,
    CONSTRAINT fk_avaliacoes_avaliado FOREIGN KEY (avaliado_id) REFERENCES usuarios (id) ON DELETE CASCADE,
    CONSTRAINT uk_avaliacoes_proposta UNIQUE (proposta_id),
    CONSTRAINT ck_avaliacoes_nota CHECK (nota BETWEEN 1 AND 5)
);

CREATE INDEX ix_avaliacoes_avaliado_criado_em ON avaliacoes (avaliado_id, criado_em DESC);

ALTER TABLE notificacoes DROP CONSTRAINT ck_notificacoes_tipo;
ALTER TABLE notificacoes ADD CONSTRAINT ck_notificacoes_tipo
    CHECK (tipo IN ('PROPOSTA_RECEBIDA', 'PROPOSTA_ACEITA', 'PROPOSTA_RECUSADA', 'PROPOSTA_NAO_SELECIONADA',
                    'VALIDACAO_APROVADA', 'VALIDACAO_REPROVADA', 'SERVICO_CONCLUIDO', 'AVALIACAO_RECEBIDA'));
