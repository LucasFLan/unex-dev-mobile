# DER do ChamaAí

Modelo entidade-relacionamento do banco (PostgreSQL), montado a partir das migrações Flyway **V1 a V14**
(`apps/backend/chamaAi/src/main/resources/db/migration`). O GitHub desenha o diagrama abaixo.

```mermaid
erDiagram
    usuarios {
        UUID id PK
        TEXT nome
        DATE data_nascimento
        VARCHAR cpf UK "11 dígitos"
        TEXT email UK
        TEXT senha_hash
        TEXT telefone "opcional"
        BOOLEAN verificado
        VARCHAR papel "USUARIO | ADMIN"
        VARCHAR status_validacao "PENDENTE | APROVADO | REPROVADO"
        TIMESTAMPTZ foto_atualizada_em "nulo = sem foto"
    }

    categorias {
        UUID id PK
        VARCHAR slug UK
        VARCHAR nome UK
    }

    anuncios {
        UUID id PK
        UUID autor_id FK
        UUID categoria_id FK
        VARCHAR tipo "OFERTA | PEDIDO"
        VARCHAR titulo
        TEXT descricao
        NUMERIC preco "nulo = a combinar"
        VARCHAR cidade
        CHAR uf
        VARCHAR cep "opcional"
        VARCHAR logradouro
        VARCHAR numero
        VARCHAR complemento
        VARCHAR bairro
        DOUBLE latitude
        DOUBLE longitude
        SMALLINT raio_atendimento_km "1 a 200, só OFERTA"
        BOOLEAN ativo
        TIMESTAMPTZ criado_em
        TIMESTAMPTZ atualizado_em
    }

    anuncios_fotos {
        UUID id PK
        UUID anuncio_id FK
        VARCHAR content_type
        TIMESTAMPTZ criado_em "define a ordem"
    }

    propostas {
        UUID id PK
        UUID anuncio_id FK
        UUID remetente_id FK
        UUID destinatario_id FK
        NUMERIC valor "nulo = a combinar"
        VARCHAR mensagem
        VARCHAR status "PENDENTE | ACEITA | RECUSADA | NAO_SELECIONADA | CANCELADA | CONCLUIDA"
        TIMESTAMPTZ criado_em
        TIMESTAMPTZ respondido_em
        TIMESTAMPTZ concluido_em
    }

    propostas_fotos {
        UUID id PK
        UUID proposta_id FK
        SMALLINT ordem "único por proposta"
        VARCHAR content_type
        TIMESTAMPTZ criado_em
    }

    avaliacoes {
        UUID id PK
        UUID proposta_id FK,UK "nulo se a proposta sumir"
        UUID avaliador_id FK "nulo se a conta for apagada"
        UUID avaliado_id FK
        SMALLINT nota "1 a 5"
        VARCHAR comentario
        VARCHAR servico_titulo
        VARCHAR servico_categoria
        TIMESTAMPTZ criado_em
    }

    notificacoes {
        UUID id PK
        UUID usuario_id FK
        UUID anuncio_id FK "opcional"
        UUID proposta_id FK "opcional"
        VARCHAR tipo
        VARCHAR titulo
        VARCHAR mensagem
        BOOLEAN lida
        TIMESTAMPTZ criado_em
    }

    envios_validacao {
        UUID id PK
        UUID usuario_id FK
        UUID analisado_por FK "admin, opcional"
        VARCHAR status "PENDENTE | APROVADO | REPROVADO"
        VARCHAR motivo_reprovacao
        TIMESTAMPTZ enviado_em
        TIMESTAMPTZ analisado_em
    }

    arquivos_validacao {
        UUID envio_id PK,FK
        VARCHAR tipo PK "DOCUMENTO | SELFIE"
        VARCHAR content_type
        VARCHAR caminho
    }

    codigos_recuperacao_senha {
        UUID id PK
        UUID usuario_id FK
        CHAR codigo_hash "SHA-256"
        TIMESTAMPTZ expira_em
        INTEGER tentativas
        TIMESTAMPTZ usado_em
        TIMESTAMPTZ criado_em
    }

    usuarios ||--o{ anuncios : "publica"
    categorias ||--o{ anuncios : "classifica"
    anuncios ||--o{ anuncios_fotos : "tem até 6"

    anuncios ||--o{ propostas : "recebe"
    usuarios ||--o{ propostas : "envia (remetente)"
    usuarios ||--o{ propostas : "recebe (destinatário)"
    propostas ||--o{ propostas_fotos : "tem até 5"

    propostas |o--o| avaliacoes : "gera ao concluir"
    usuarios |o--o{ avaliacoes : "avalia"
    usuarios ||--o{ avaliacoes : "é avaliado"

    usuarios ||--o{ notificacoes : "recebe"
    anuncios |o--o{ notificacoes : "referencia"
    propostas |o--o{ notificacoes : "referencia"

    usuarios ||--o{ envios_validacao : "envia documentos"
    usuarios |o--o{ envios_validacao : "analisa (admin)"
    envios_validacao ||--|{ arquivos_validacao : "documento e selfie"

    usuarios ||--o{ codigos_recuperacao_senha : "pede"
```

## Regras que o diagrama não mostra

| Tabela | Regra |
|---|---|
| `usuarios` | `cpf` e `email` únicos. Só `status_validacao = APROVADO` publica anúncio e envia proposta. A foto de perfil fica em disco (`uploads/perfil`); o banco guarda só `foto_atualizada_em`. |
| `anuncios` | `preco > 0` ou nulo ("a combinar"). `latitude` e `longitude` vêm juntas ou nenhuma. Excluir o usuário apaga os anúncios dele (cascade). |
| `anuncios_fotos` | Até 6 por anúncio (regra da aplicação). Arquivos em `uploads/anuncios/<anuncio>/<foto>`. Excluir o anúncio apaga as fotos. |
| `propostas` | `remetente_id <> destinatario_id`. **Uma em andamento por pessoa e anúncio**: índice único parcial em `(anuncio_id, remetente_id)` com `status IN ('PENDENTE','ACEITA')` (V14). Depois de encerrada, pode haver outra. |
| `propostas_fotos` | Até 5 por proposta; `(proposta_id, ordem)` único. Arquivos em `uploads/propostas`. |
| `avaliacoes` | Uma por proposta (`proposta_id` único), nota de 1 a 5, só depois de `CONCLUIDA`. Guarda título e categoria do serviço para continuar valendo se o anúncio for excluído. |
| `notificacoes` | `tipo`: `PROPOSTA_RECEBIDA`, `PROPOSTA_ACEITA`, `PROPOSTA_RECUSADA`, `PROPOSTA_NAO_SELECIONADA`, `VALIDACAO_APROVADA`, `VALIDACAO_REPROVADA`, `SERVICO_CONCLUIDO`, `AVALIACAO_RECEBIDA`. |
| `envios_validacao` / `arquivos_validacao` | Cada envio tem o documento e a selfie (chave composta `envio_id` + `tipo`). Arquivos em `uploads/validacao`. |
| `codigos_recuperacao_senha` | Só o hash do código é salvo; `tentativas >= 0`. |
