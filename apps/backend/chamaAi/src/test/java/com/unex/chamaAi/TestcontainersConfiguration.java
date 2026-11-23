package com.unex.chamaAi;

import com.unex.chamaAi.anuncio.application.ports.ConsultaCep;
import com.unex.chamaAi.anuncio.domain.valueobjects.Coordenadas;
import com.unex.chamaAi.shared.domain.exceptions.ServicoExternoIndisponivelException;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.Optional;

@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

    /** Com ponto no mapa. */
    public static final String CEP_SALVADOR = "40140110";
    /** Existe, mas sem ponto no mapa. */
    public static final String CEP_SEM_PONTO = "01001000";
    /** Simula o serviço de CEP fora do ar. */
    public static final String CEP_FORA_DO_AR = "99999999";

    @Bean
    @ServiceConnection
    PostgreSQLContainer postgres() {
        return new PostgreSQLContainer(DockerImageName.parse("postgres:15"));
    }

    /** Os testes não acessam a internet: CEPs conhecidos respondem fixo, o resto "não existe". */
    @Bean
    @Primary
    ConsultaCep consultaCepFalsa() {
        return cep -> switch (cep) {
            case CEP_SALVADOR -> Optional.of(new ConsultaCep.EnderecoCep(cep, "Avenida Sete de Setembro", "Barra",
                    "Salvador", "BA", new Coordenadas(-13.0123456, -38.5234567)));
            case CEP_SEM_PONTO -> Optional.of(new ConsultaCep.EnderecoCep(cep, "Praça da Sé", "Sé", "São Paulo", "SP",
                    null));
            case CEP_FORA_DO_AR -> throw new ServicoExternoIndisponivelException("A consulta de CEP está fora do ar.",
                    null);
            default -> Optional.empty();
        };
    }
}
