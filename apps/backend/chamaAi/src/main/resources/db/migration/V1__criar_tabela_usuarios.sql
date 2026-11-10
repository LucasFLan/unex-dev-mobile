CREATE TABLE usuarios (
    id              UUID        PRIMARY KEY,
    nome            TEXT        NOT NULL,
    data_nascimento DATE        NOT NULL,
    cpf             VARCHAR(11) NOT NULL,
    email           TEXT        NOT NULL,
    senha_hash      TEXT        NOT NULL,
    telefone        TEXT,
    verificado      BOOLEAN     NOT NULL,
    CONSTRAINT uk_usuarios_cpf UNIQUE (cpf),
    CONSTRAINT uk_usuarios_email UNIQUE (email)
);
