-- A imagem fica em disco (app.perfil.fotos-diretorio); o banco guarda só quando ela foi trocada.
-- NULL = usuário sem foto.
ALTER TABLE usuarios
    ADD COLUMN foto_atualizada_em TIMESTAMPTZ;
