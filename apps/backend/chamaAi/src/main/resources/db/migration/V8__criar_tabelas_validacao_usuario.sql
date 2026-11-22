-- Cada vez que o usuário manda os documentos para análise vira um envio.
CREATE TABLE envios_validacao (
    id                 UUID          PRIMARY KEY,
    usuario_id         UUID          NOT NULL,
    status             VARCHAR(20)   NOT NULL,
    motivo_reprovacao  VARCHAR(500),
    enviado_em         TIMESTAMPTZ   NOT NULL,
    analisado_em       TIMESTAMPTZ,
    analisado_por      UUID,
    CONSTRAINT fk_envios_validacao_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios (id) ON DELETE CASCADE,
    CONSTRAINT fk_envios_validacao_admin FOREIGN KEY (analisado_por) REFERENCES usuarios (id) ON DELETE SET NULL,
    CONSTRAINT ck_envios_validacao_status CHECK (status IN ('PENDENTE', 'APROVADO', 'REPROVADO'))
);

CREATE INDEX ix_envios_validacao_usuario_enviado_em ON envios_validacao (usuario_id, enviado_em DESC);
CREATE INDEX ix_envios_validacao_status_enviado_em ON envios_validacao (status, enviado_em);

-- Imagens (documento e selfie): os arquivos ficam numa pasta no disco; aqui só o caminho relativo.
CREATE TABLE arquivos_validacao (
    envio_id      UUID         NOT NULL,
    tipo          VARCHAR(20)  NOT NULL,
    content_type  VARCHAR(50)  NOT NULL,
    caminho       VARCHAR(300) NOT NULL,
    PRIMARY KEY (envio_id, tipo),
    CONSTRAINT fk_arquivos_validacao_envio FOREIGN KEY (envio_id) REFERENCES envios_validacao (id) ON DELETE CASCADE,
    CONSTRAINT ck_arquivos_validacao_tipo CHECK (tipo IN ('DOCUMENTO', 'SELFIE'))
);

ALTER TABLE notificacoes DROP CONSTRAINT ck_notificacoes_tipo;
ALTER TABLE notificacoes ADD CONSTRAINT ck_notificacoes_tipo
    CHECK (tipo IN ('PROPOSTA_RECEBIDA', 'PROPOSTA_ACEITA', 'PROPOSTA_RECUSADA', 'PROPOSTA_NAO_SELECIONADA',
                    'VALIDACAO_APROVADA', 'VALIDACAO_REPROVADA'));
