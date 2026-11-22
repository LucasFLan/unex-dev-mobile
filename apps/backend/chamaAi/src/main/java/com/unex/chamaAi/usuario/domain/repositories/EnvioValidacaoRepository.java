package com.unex.chamaAi.usuario.domain.repositories;

import com.unex.chamaAi.shared.domain.paginacao.Pagina;
import com.unex.chamaAi.shared.domain.paginacao.Paginacao;
import com.unex.chamaAi.usuario.domain.entities.EnvioValidacao;
import com.unex.chamaAi.usuario.domain.valueobjects.ArquivoValidacao;
import com.unex.chamaAi.usuario.domain.valueobjects.StatusValidacao;
import com.unex.chamaAi.usuario.domain.valueobjects.TipoArquivoValidacao;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EnvioValidacaoRepository {

    /** Insere ou atualiza os dados do envio (sem as imagens). */
    void salvar(EnvioValidacao envio);

    /** Grava as imagens de um envio novo. */
    void salvarArquivos(UUID envioId, List<ArquivoValidacao> arquivos);

    Optional<EnvioValidacao> buscarPorId(UUID id);

    /** Envio mais recente do usuário. */
    Optional<EnvioValidacao> buscarMaisRecente(UUID usuarioId);

    /** Envios com o status informado; os pendentes vêm do mais antigo para o mais novo (ordem da fila). */
    Pagina<EnvioValidacao> buscarPorStatus(StatusValidacao status, Paginacao paginacao);

    Optional<ArquivoValidacao> buscarArquivo(UUID envioId, TipoArquivoValidacao tipo);
}
