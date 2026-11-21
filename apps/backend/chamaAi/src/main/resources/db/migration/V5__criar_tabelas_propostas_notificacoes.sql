CREATE TABLE propostas (
    id              UUID           PRIMARY KEY,
    anuncio_id      UUID           NOT NULL,
    remetente_id    UUID           NOT NULL,
    destinatario_id UUID           NOT NULL,
    valor           NUMERIC(10,2),
    mensagem        VARCHAR(1000)  NOT NULL,
    criado_em       TIMESTAMPTZ    NOT NULL,
    CONSTRAINT fk_propostas_anuncio FOREIGN KEY (anuncio_id) REFERENCES anuncios (id) ON DELETE CASCADE,
    CONSTRAINT fk_propostas_remetente FOREIGN KEY (remetente_id) REFERENCES usuarios (id) ON DELETE CASCADE,
    CONSTRAINT fk_propostas_destinatario FOREIGN KEY (destinatario_id) REFERENCES usuarios (id) ON DELETE CASCADE,
    -- Uma proposta por pessoa em cada anúncio.
    CONSTRAINT uk_propostas_anuncio_remetente UNIQUE (anuncio_id, remetente_id),
    CONSTRAINT ck_propostas_valor CHECK (valor IS NULL OR valor > 0),
    CONSTRAINT ck_propostas_remetente CHECK (remetente_id <> destinatario_id)
);

CREATE INDEX ix_propostas_destinatario_criado_em ON propostas (destinatario_id, criado_em DESC);
CREATE INDEX ix_propostas_remetente_criado_em ON propostas (remetente_id, criado_em DESC);

CREATE TABLE notificacoes (
    id          UUID          PRIMARY KEY,
    usuario_id  UUID          NOT NULL,
    tipo        VARCHAR(40)   NOT NULL,
    titulo      VARCHAR(150)  NOT NULL,
    mensagem    VARCHAR(1000) NOT NULL,
    anuncio_id  UUID,
    proposta_id UUID,
    lida        BOOLEAN       NOT NULL,
    criado_em   TIMESTAMPTZ   NOT NULL,
    CONSTRAINT fk_notificacoes_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios (id) ON DELETE CASCADE,
    CONSTRAINT fk_notificacoes_anuncio FOREIGN KEY (anuncio_id) REFERENCES anuncios (id) ON DELETE SET NULL,
    CONSTRAINT fk_notificacoes_proposta FOREIGN KEY (proposta_id) REFERENCES propostas (id) ON DELETE SET NULL,
    CONSTRAINT ck_notificacoes_tipo CHECK (tipo IN ('PROPOSTA_RECEBIDA'))
);

CREATE INDEX ix_notificacoes_usuario_criado_em ON notificacoes (usuario_id, criado_em DESC);
CREATE INDEX ix_notificacoes_usuario_nao_lidas ON notificacoes (usuario_id) WHERE NOT lida;
