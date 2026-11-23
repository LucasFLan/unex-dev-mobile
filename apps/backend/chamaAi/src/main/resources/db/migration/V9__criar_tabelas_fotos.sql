-- Foto de perfil: no máximo uma por usuário. O arquivo fica no disco; aqui só o caminho relativo.
CREATE TABLE fotos_perfil (
    usuario_id     UUID          PRIMARY KEY,
    content_type   VARCHAR(50)   NOT NULL,
    caminho        VARCHAR(300)  NOT NULL,
    atualizado_em  TIMESTAMPTZ   NOT NULL,
    CONSTRAINT fk_fotos_perfil_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios (id) ON DELETE CASCADE
);

-- Fotos dos anúncios (até 6 por anúncio), na ordem em que foram enviadas.
CREATE TABLE fotos_anuncio (
    id            UUID          PRIMARY KEY,
    anuncio_id    UUID          NOT NULL,
    content_type  VARCHAR(50)   NOT NULL,
    caminho       VARCHAR(300)  NOT NULL,
    criado_em     TIMESTAMPTZ   NOT NULL,
    CONSTRAINT fk_fotos_anuncio_anuncio FOREIGN KEY (anuncio_id) REFERENCES anuncios (id) ON DELETE CASCADE
);

CREATE INDEX ix_fotos_anuncio_anuncio_criado_em ON fotos_anuncio (anuncio_id, criado_em, id);
