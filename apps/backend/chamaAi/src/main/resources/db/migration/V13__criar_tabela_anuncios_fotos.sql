-- Fotos dos anúncios (até 6). Os bytes ficam em disco (app.anuncios.fotos-diretorio/<anuncio>/<foto>);
-- aqui só o que é preciso para listar e servir, na ordem em que foram enviadas.
CREATE TABLE anuncios_fotos (
    id           UUID        PRIMARY KEY,
    anuncio_id   UUID        NOT NULL,
    content_type VARCHAR(50) NOT NULL,
    criado_em    TIMESTAMPTZ NOT NULL,
    CONSTRAINT fk_anuncios_fotos_anuncio FOREIGN KEY (anuncio_id) REFERENCES anuncios (id) ON DELETE CASCADE
);

CREATE INDEX ix_anuncios_fotos_anuncio_criado_em ON anuncios_fotos (anuncio_id, criado_em, id);
