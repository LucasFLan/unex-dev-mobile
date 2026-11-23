package com.unex.chamaAi.usuario.domain.repositories;

import com.unex.chamaAi.shared.domain.valueobjects.Imagem;

import java.time.Instant;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/** Foto de perfil dos usuários (no máximo uma por usuário). */
public interface FotoPerfilRepository {

    /** Quando a foto atual foi enviada; vazio se o usuário não tem foto. Serve de versão na URL. */
    Optional<Instant> versao(UUID usuarioId);

    /** Versões das fotos de vários usuários de uma vez; quem não tem foto fica de fora do mapa. */
    Map<UUID, Instant> versoes(Collection<UUID> usuarioIds);

    /** Troca a foto do usuário (a anterior é apagada). */
    void salvar(UUID usuarioId, Imagem imagem, Instant enviadaEm);

    void remover(UUID usuarioId);

    Optional<Imagem> ler(UUID usuarioId);
}
