package com.unex.chamaAi.anuncio.infrastructure.web;

import com.unex.chamaAi.anuncio.application.ports.ConsultaCep;
import com.unex.chamaAi.anuncio.domain.exceptions.CepNaoEncontradoException;
import com.unex.chamaAi.anuncio.domain.valueobjects.Endereco;
import com.unex.chamaAi.config.OpenApiConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/** Consulta de CEP para preencher o endereço no formulário do anúncio. */
@Tag(name = OpenApiConfig.TAG_ANUNCIOS)
@RestController
public class CepController {

    private final ConsultaCep consultaCep;

    public CepController(ConsultaCep consultaCep) {
        this.consultaCep = consultaCep;
    }

    @Schema(description = "Endereço de um CEP. `latitude`/`longitude` nulas quando não há ponto para ele.")
    public record CepResponse(
            @Schema(example = "40140110") String cep,
            @Schema(example = "Avenida Sete de Setembro", nullable = true) String logradouro,
            @Schema(example = "Barra", nullable = true) String bairro,
            @Schema(example = "Salvador", nullable = true) String cidade,
            @Schema(example = "BA", nullable = true) String uf,
            @Schema(example = "-13.01", nullable = true) Double latitude,
            @Schema(example = "-38.53", nullable = true) Double longitude) {
    }

    @Operation(summary = "Consultar CEP",
            description = "Devolve logradouro, bairro, cidade, UF e (quando houver) o ponto aproximado. Exige login.")
    @ApiResponse(responseCode = "200", description = "CEP encontrado.")
    @ApiResponse(responseCode = "400", description = "CEP sem 8 dígitos.")
    @ApiResponse(responseCode = "401", ref = "#/components/responses/NaoAutenticado")
    @ApiResponse(responseCode = "404", description = "CEP não encontrado.")
    @ApiResponse(responseCode = "503", description = "Serviço de CEP fora do ar.")
    @GetMapping("/cep/{cep}")
    public CepResponse consultar(@Parameter(description = "CEP, com ou sem hífen.", example = "40140-110")
                                 @PathVariable String cep) {
        String digitos = Endereco.normalizarCep(cep);
        var endereco = consultaCep.buscar(digitos).orElseThrow(CepNaoEncontradoException::new);
        // O ponto vai arredondado: é para mostrar a região no formulário, não o endereço exato.
        var regiao = endereco.coordenadas() == null ? null : endereco.coordenadas().aproximada();
        return new CepResponse(endereco.cep(), endereco.logradouro(), endereco.bairro(), endereco.cidade(),
                endereco.uf(), regiao == null ? null : regiao.latitude(), regiao == null ? null : regiao.longitude());
    }
}
