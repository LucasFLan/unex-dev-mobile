ALTER TABLE propostas
    ADD COLUMN status        VARCHAR(20) NOT NULL DEFAULT 'PENDENTE',
    ADD COLUMN respondido_em TIMESTAMPTZ,
    ADD CONSTRAINT ck_propostas_status
        CHECK (status IN ('PENDENTE', 'ACEITA', 'RECUSADA', 'NAO_SELECIONADA', 'CANCELADA'));

CREATE INDEX ix_propostas_anuncio_pendentes ON propostas (anuncio_id) WHERE status = 'PENDENTE';

ALTER TABLE notificacoes DROP CONSTRAINT ck_notificacoes_tipo;
ALTER TABLE notificacoes ADD CONSTRAINT ck_notificacoes_tipo
    CHECK (tipo IN ('PROPOSTA_RECEBIDA', 'PROPOSTA_ACEITA', 'PROPOSTA_RECUSADA', 'PROPOSTA_NAO_SELECIONADA'));
