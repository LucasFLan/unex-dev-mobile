package com.unex.chamaAi.anuncio.infrastructure.cep;

import com.unex.chamaAi.anuncio.application.ports.ConsultaCep;
import com.unex.chamaAi.anuncio.domain.valueobjects.Coordenadas;
import com.unex.chamaAi.shared.domain.exceptions.ServicoExternoIndisponivelException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Consulta de CEP pela BrasilAPI (gratuita, sem chave). Quando ela não traz o ponto no mapa, tenta o Nominatim
 * (OpenStreetMap) com rua, bairro e cidade. Os resultados ficam em memória: CEP quase nunca muda, e assim o
 * formulário (que consulta ao digitar) e a gravação do anúncio não repetem a chamada.
 *
 * Nada do usuário é enviado: só o CEP e, no Nominatim, o endereço público correspondente a ele.
 */
@Component
public class ConsultaCepHttp implements ConsultaCep {

    private static final Logger LOG = LoggerFactory.getLogger(ConsultaCepHttp.class);
    private static final Duration TEMPO_LIMITE = Duration.ofSeconds(5);
    private static final int MAXIMO_EM_CACHE = 2000;
    /** O Nominatim pede um User-Agent que identifique a aplicação. */
    private static final String USER_AGENT = "ChamaAi/1.0 (consulta de CEP)";

    private final HttpClient http = HttpClient.newBuilder().connectTimeout(TEMPO_LIMITE)
            .followRedirects(HttpClient.Redirect.NORMAL).build();
    private final ObjectMapper json = new ObjectMapper();
    private final String brasilApi;
    private final String nominatim;
    private final Map<String, Optional<EnderecoCep>> cache = Collections.synchronizedMap(
            new LinkedHashMap<>(16, 0.75f, true) {
                @Override
                protected boolean removeEldestEntry(Map.Entry<String, Optional<EnderecoCep>> mais) {
                    return size() > MAXIMO_EM_CACHE;
                }
            });

    public ConsultaCepHttp(@Value("${app.cep.brasilapi-url:https://brasilapi.com.br/api/cep/v2/}") String brasilApi,
                           @Value("${app.cep.nominatim-url:https://nominatim.openstreetmap.org/search}") String nominatim) {
        this.brasilApi = brasilApi;
        this.nominatim = nominatim;
    }

    @Override
    public Optional<EnderecoCep> buscar(String cep) {
        Optional<EnderecoCep> guardado = cache.get(cep);
        if (guardado != null) {
            return guardado;
        }
        Optional<EnderecoCep> encontrado = consultarBrasilApi(cep).map(this::completarCoordenadas);
        cache.put(cep, encontrado);
        return encontrado;
    }

    private Optional<EnderecoCep> consultarBrasilApi(String cep) {
        HttpResponse<String> resposta = get(URI.create(brasilApi + cep));
        if (resposta.statusCode() == 404 || resposta.statusCode() == 400) {
            return Optional.empty();
        }
        if (resposta.statusCode() != 200) {
            throw new ServicoExternoIndisponivelException(
                    "A consulta de CEP está fora do ar. Tente de novo em instantes.", null);
        }
        JsonNode corpo = json.readTree(resposta.body());
        JsonNode coordenadas = corpo.path("location").path("coordinates");
        return Optional.of(new EnderecoCep(cep, texto(corpo, "street"), texto(corpo, "neighborhood"),
                texto(corpo, "city"), texto(corpo, "state"),
                coordenadas(texto(coordenadas, "latitude"), texto(coordenadas, "longitude"))));
    }

    /** Sem ponto na BrasilAPI: procura pelo endereço no OpenStreetMap. Falhar aqui não impede nada. */
    private EnderecoCep completarCoordenadas(EnderecoCep endereco) {
        if (endereco.coordenadas() != null || endereco.cidade() == null) {
            return endereco;
        }
        String rua = endereco.logradouro() == null ? "" : endereco.logradouro();
        String consulta = "?format=jsonv2&limit=1&countrycodes=br&street=" + codificar(rua)
                + "&city=" + codificar(endereco.cidade()) + "&state=" + codificar(endereco.uf() == null ? "" : endereco.uf());
        try {
            HttpResponse<String> resposta = get(URI.create(nominatim + consulta));
            if (resposta.statusCode() == 200) {
                JsonNode lista = json.readTree(resposta.body());
                if (lista.isArray() && !lista.isEmpty()) {
                    Coordenadas ponto = coordenadas(texto(lista.get(0), "lat"), texto(lista.get(0), "lon"));
                    if (ponto != null) {
                        return new EnderecoCep(endereco.cep(), endereco.logradouro(), endereco.bairro(),
                                endereco.cidade(), endereco.uf(), ponto);
                    }
                }
            }
        } catch (RuntimeException e) {
            LOG.debug("Nominatim sem resposta para o CEP {}", endereco.cep(), e);
        }
        return endereco;
    }

    private HttpResponse<String> get(URI uri) {
        HttpRequest requisicao = HttpRequest.newBuilder(uri).timeout(TEMPO_LIMITE)
                .header("Accept", "application/json").header("User-Agent", USER_AGENT).GET().build();
        try {
            return http.send(requisicao, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new ServicoExternoIndisponivelException(
                    "A consulta de CEP está fora do ar. Tente de novo em instantes.", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ServicoExternoIndisponivelException("A consulta de CEP foi interrompida.", e);
        }
    }

    private static Coordenadas coordenadas(String latitude, String longitude) {
        if (latitude == null || longitude == null) {
            return null;
        }
        try {
            return new Coordenadas(Double.parseDouble(latitude), Double.parseDouble(longitude));
        } catch (RuntimeException e) {
            return null;
        }
    }

    /** Texto do campo (números viram texto); vazio vira nulo. */
    private static String texto(JsonNode no, String campo) {
        JsonNode valor = no == null ? null : no.get(campo);
        if (valor == null || valor.isNull()) {
            return null;
        }
        String texto = valor.isValueNode() ? valor.asString() : null;
        return texto == null || texto.isBlank() ? null : texto.trim();
    }

    private static String codificar(String valor) {
        return URLEncoder.encode(valor, StandardCharsets.UTF_8);
    }
}
