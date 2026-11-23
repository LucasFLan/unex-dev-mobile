package com.unex.chamaAi.usuario.infrastructure.persistence;

import com.unex.chamaAi.shared.domain.valueobjects.Imagem;
import com.unex.chamaAi.shared.infrastructure.storage.PastaDeImagens;
import com.unex.chamaAi.usuario.domain.repositories.FotoPerfilRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Fotos de perfil em {@code app.perfil.diretorio}: {@code <usuarioId>/<enviadaEm>.jpg}. O nome muda a cada troca,
 * então quem ainda baixa a foto antiga não recebe um arquivo pela metade.
 */
@Repository
public class FotoPerfilRepositoryJpa implements FotoPerfilRepository {

    private final FotoPerfilSpringDataRepository springData;
    private final PastaDeImagens pasta;

    public FotoPerfilRepositoryJpa(FotoPerfilSpringDataRepository springData,
                                   @Value("${app.perfil.diretorio}") String diretorio) {
        this.springData = springData;
        this.pasta = new PastaDeImagens(diretorio);
    }

    @Override
    public Optional<Instant> versao(UUID usuarioId) {
        return springData.findById(usuarioId).map(FotoPerfilJpaEntity::getAtualizadoEm);
    }

    @Override
    public Map<UUID, Instant> versoes(Collection<UUID> usuarioIds) {
        if (usuarioIds.isEmpty()) {
            return Map.of();
        }
        return springData.findAllById(usuarioIds).stream()
                .collect(Collectors.toMap(FotoPerfilJpaEntity::getUsuarioId, FotoPerfilJpaEntity::getAtualizadoEm));
    }

    @Override
    public void salvar(UUID usuarioId, Imagem imagem, Instant enviadaEm) {
        Optional<String> anterior = springData.findById(usuarioId).map(FotoPerfilJpaEntity::getCaminho);
        String caminho = usuarioId + "/" + enviadaEm.toEpochMilli() + imagem.extensao();
        pasta.gravar(caminho, imagem.conteudo());
        springData.saveAndFlush(new FotoPerfilJpaEntity(usuarioId, imagem.contentType(), caminho, enviadaEm));
        anterior.filter(antigo -> !antigo.equals(caminho)).ifPresent(pasta::excluir);
    }

    @Override
    public void remover(UUID usuarioId) {
        springData.findById(usuarioId).ifPresent(foto -> {
            springData.delete(foto);
            springData.flush();
            pasta.excluir(foto.getCaminho());
        });
    }

    @Override
    public Optional<Imagem> ler(UUID usuarioId) {
        return springData.findById(usuarioId)
                .flatMap(foto -> pasta.ler(foto.getCaminho()).map(bytes -> new Imagem(foto.getContentType(), bytes)));
    }
}
