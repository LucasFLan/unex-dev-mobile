CREATE TABLE codigos_recuperacao_senha (
    id          UUID         PRIMARY KEY,
    usuario_id  UUID         NOT NULL,
    -- Só o hash (SHA-256) do código fica salvo, nunca o código em si.
    codigo_hash CHAR(64)     NOT NULL,
    expira_em   TIMESTAMPTZ  NOT NULL,
    tentativas  INTEGER      NOT NULL DEFAULT 0,
    usado_em    TIMESTAMPTZ,
    criado_em   TIMESTAMPTZ  NOT NULL,
    CONSTRAINT fk_codigos_recuperacao_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios (id) ON DELETE CASCADE,
    CONSTRAINT ck_codigos_recuperacao_tentativas CHECK (tentativas >= 0)
);

CREATE INDEX ix_codigos_recuperacao_usuario_criado_em ON codigos_recuperacao_senha (usuario_id, criado_em DESC);
