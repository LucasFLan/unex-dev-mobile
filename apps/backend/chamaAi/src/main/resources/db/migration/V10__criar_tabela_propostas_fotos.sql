-- Fotos anexadas às propostas. Os bytes ficam em disco (app.propostas.fotos-diretorio/<proposta>/<foto>);
-- aqui só o que é preciso para listar e servir.
CREATE TABLE propostas_fotos (
    id           UUID        PRIMARY KEY,
    proposta_id  UUID        NOT NULL,
    ordem        SMALLINT    NOT NULL,
    content_type VARCHAR(50) NOT NULL,
    criado_em    TIMESTAMPTZ NOT NULL,
    CONSTRAINT fk_propostas_fotos_proposta FOREIGN KEY (proposta_id) REFERENCES propostas (id) ON DELETE CASCADE,
    CONSTRAINT uk_propostas_fotos_ordem UNIQUE (proposta_id, ordem)
);
