-- Endereço a partir do CEP. Tudo opcional: anúncios antigos só têm cidade e UF.
-- O público vê bairro, cidade, UF e o ponto arredondado (~1 km); o resto só o autor e, depois do aceite, a outra
-- pessoa da proposta.
ALTER TABLE anuncios
    ADD COLUMN cep                 VARCHAR(8),
    ADD COLUMN logradouro          VARCHAR(150),
    ADD COLUMN numero              VARCHAR(20),
    ADD COLUMN complemento         VARCHAR(100),
    ADD COLUMN bairro              VARCHAR(100),
    ADD COLUMN latitude            DOUBLE PRECISION,
    ADD COLUMN longitude           DOUBLE PRECISION,
    ADD COLUMN raio_atendimento_km SMALLINT,
    ADD CONSTRAINT ck_anuncios_coordenadas CHECK ((latitude IS NULL) = (longitude IS NULL)),
    ADD CONSTRAINT ck_anuncios_raio CHECK (raio_atendimento_km IS NULL OR raio_atendimento_km BETWEEN 1 AND 200);
