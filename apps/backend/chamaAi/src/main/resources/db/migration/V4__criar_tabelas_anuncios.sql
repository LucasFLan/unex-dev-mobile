CREATE TABLE categorias (
    id   UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    slug VARCHAR(60) NOT NULL,
    nome VARCHAR(80) NOT NULL,
    CONSTRAINT uk_categorias_slug UNIQUE (slug),
    CONSTRAINT uk_categorias_nome UNIQUE (nome)
);

INSERT INTO categorias (slug, nome) VALUES
    ('eletricista', 'Eletricista'),
    ('encanador', 'Encanador'),
    ('pedreiro', 'Pedreiro'),
    ('pintor', 'Pintor'),
    ('marceneiro', 'Marceneiro'),
    ('serralheiro', 'Serralheiro'),
    ('gesseiro', 'Gesseiro'),
    ('vidraceiro', 'Vidraceiro'),
    ('chaveiro', 'Chaveiro'),
    ('montador-de-moveis', 'Montador de móveis'),
    ('ar-condicionado', 'Ar-condicionado e refrigeração'),
    ('limpeza', 'Limpeza e diarista'),
    ('jardinagem', 'Jardinagem'),
    ('outros', 'Outros');

CREATE TABLE anuncios (
    id            UUID          PRIMARY KEY,
    autor_id      UUID          NOT NULL,
    categoria_id  UUID          NOT NULL,
    tipo          VARCHAR(20)   NOT NULL,
    titulo        VARCHAR(120)  NOT NULL,
    descricao     TEXT          NOT NULL,
    preco         NUMERIC(10,2),
    cidade        VARCHAR(100)  NOT NULL,
    uf            CHAR(2)       NOT NULL,
    ativo         BOOLEAN       NOT NULL,
    criado_em     TIMESTAMPTZ   NOT NULL,
    atualizado_em TIMESTAMPTZ   NOT NULL,
    CONSTRAINT fk_anuncios_autor FOREIGN KEY (autor_id) REFERENCES usuarios (id) ON DELETE CASCADE,
    CONSTRAINT fk_anuncios_categoria FOREIGN KEY (categoria_id) REFERENCES categorias (id),
    CONSTRAINT ck_anuncios_tipo CHECK (tipo IN ('OFERTA', 'PEDIDO')),
    CONSTRAINT ck_anuncios_preco CHECK (preco IS NULL OR preco > 0)
);

CREATE INDEX ix_anuncios_autor ON anuncios (autor_id);
CREATE INDEX ix_anuncios_ativos_criado_em ON anuncios (criado_em DESC) WHERE ativo;
CREATE INDEX ix_anuncios_categoria ON anuncios (categoria_id);
CREATE INDEX ix_anuncios_uf_cidade ON anuncios (uf, lower(cidade));
