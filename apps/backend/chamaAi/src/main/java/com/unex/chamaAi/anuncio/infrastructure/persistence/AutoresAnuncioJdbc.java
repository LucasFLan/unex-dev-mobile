package com.unex.chamaAi.anuncio.infrastructure.persistence;

import com.unex.chamaAi.anuncio.application.ports.AutoresAnuncio;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Nome, foto e média de avaliações dos autores numa consulta só (usuarios + avaliacoes). */
@Component
public class AutoresAnuncioJdbc implements AutoresAnuncio {

    private static final String SQL = """
            SELECT u.id, u.nome, u.foto_atualizada_em, AVG(a.nota) AS media, COUNT(a.id) AS total
              FROM usuarios u
              LEFT JOIN avaliacoes a ON a.avaliado_id = u.id
             WHERE u.id IN (:ids)
             GROUP BY u.id, u.nome, u.foto_atualizada_em
            """;

    private final NamedParameterJdbcTemplate jdbc;

    public AutoresAnuncioJdbc(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Map<UUID, AutorAnuncio> buscar(Collection<UUID> autorIds) {
        Map<UUID, AutorAnuncio> porId = new HashMap<>();
        if (autorIds.isEmpty()) {
            return porId;
        }
        jdbc.query(SQL, Map.of("ids", autorIds), linha -> {
            UUID id = linha.getObject("id", UUID.class);
            Timestamp foto = linha.getTimestamp("foto_atualizada_em");
            long total = linha.getLong("total");
            Double media = total == 0 ? null : Math.round(linha.getDouble("media") * 10) / 10.0;
            porId.put(id, new AutorAnuncio(id, linha.getString("nome"), foto == null ? null : foto.toInstant(),
                    media, total));
        });
        return porId;
    }
}
