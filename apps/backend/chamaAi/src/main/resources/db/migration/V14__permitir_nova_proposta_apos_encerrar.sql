-- Antes: uma proposta por pessoa e anúncio, para sempre. Agora: uma em aberto (PENDENTE ou ACEITA) por vez;
-- depois de concluída, recusada, cancelada ou não selecionada, a pessoa pode contratar/propor de novo.
ALTER TABLE propostas DROP CONSTRAINT uk_propostas_anuncio_remetente;

CREATE UNIQUE INDEX uk_propostas_anuncio_remetente_aberta
    ON propostas (anuncio_id, remetente_id)
    WHERE status IN ('PENDENTE', 'ACEITA');
