package com.unex.chamaAi.usuario.infrastructure.web;

import com.unex.chamaAi.config.OpenApiConfig;
import com.unex.chamaAi.shared.infrastructure.web.openapi.ExemplosDeErro;
import com.unex.chamaAi.usuario.infrastructure.web.dto.response.MinhaValidacaoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = OpenApiConfig.TAG_VALIDACAO)
public interface ValidacaoContaController {

    @Operation(summary = "Ver minha validação",
            description = """
                    Situação da validação da conta do usuário do token. Enquanto não estiver `APROVADO`, o usuário
                    navega normalmente, mas não publica anúncios nem envia propostas (403).""")
    @ApiResponse(responseCode = "200", description = "Situação da validação.", content = @Content(
            schema = @Schema(implementation = MinhaValidacaoResponse.class),
            examples = @ExampleObject(name = "emAnalise", value = """
                    {"situacao": "EM_ANALISE", "podeEnviar": false, "enviadoEm": "2026-11-22T14:00:00.123456Z",
                     "analisadoEm": null, "motivoReprovacao": null}""")))
    @ApiResponse(responseCode = "401", ref = "#/components/responses/NaoAutenticado")
    @GetMapping("/usuarios/me/validacao")
    MinhaValidacaoResponse consultar(@Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt);

    @Operation(summary = "Enviar documentos para validação",
            description = """
                    Envia, em `multipart/form-data`, a foto do documento (RG ou CNH) no campo `documento` e uma selfie
                    segurando o documento no campo `selfie`. Cada imagem: JPG, PNG ou WEBP, até 5 MB.

                    Só é possível quando a situação é `NAO_ENVIADO` ou `REPROVADO` (reenvio). O envio entra na fila
                    do admin e o usuário é notificado da decisão.""")
    @ApiResponse(responseCode = "201", description = "Documentos enviados; situação passa a `EM_ANALISE`.", content = @Content(
            schema = @Schema(implementation = MinhaValidacaoResponse.class),
            examples = @ExampleObject(name = "enviado", value = """
                    {"situacao": "EM_ANALISE", "podeEnviar": false, "enviadoEm": "2026-11-22T14:00:00.123456Z",
                     "analisadoEm": null, "motivoReprovacao": null}""")))
    @ApiResponse(responseCode = "400", description = "Imagem faltando, grande demais ou em formato não aceito.", content = @Content(
            mediaType = ExemplosDeErro.PROBLEM_JSON, schema = @Schema(implementation = ProblemDetail.class),
            examples = @ExampleObject(name = "formatoInvalido", value = ExemplosDeErro.IMAGEM_VALIDACAO_INVALIDA)))
    @ApiResponse(responseCode = "401", ref = "#/components/responses/NaoAutenticado")
    @ApiResponse(responseCode = "409", description = "Já existe um envio em análise.", content = @Content(
            mediaType = ExemplosDeErro.PROBLEM_JSON, schema = @Schema(implementation = ProblemDetail.class),
            examples = @ExampleObject(name = "emAnalise", value = ExemplosDeErro.DOCUMENTOS_EM_ANALISE)))
    @ApiResponse(responseCode = "422", description = "A conta já está validada.", content = @Content(
            mediaType = ExemplosDeErro.PROBLEM_JSON, schema = @Schema(implementation = ProblemDetail.class),
            examples = @ExampleObject(name = "jaValidada", value = ExemplosDeErro.CONTA_JA_VALIDADA)))
    @PostMapping(value = "/usuarios/me/validacao", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    MinhaValidacaoResponse enviar(@Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt,
                                  @Parameter(description = "Foto do RG ou da CNH (JPG, PNG ou WEBP, até 5 MB).")
                                  @RequestPart("documento") MultipartFile documento,
                                  @Parameter(description = "Selfie segurando o documento (JPG, PNG ou WEBP, até 5 MB).")
                                  @RequestPart("selfie") MultipartFile selfie);
}
