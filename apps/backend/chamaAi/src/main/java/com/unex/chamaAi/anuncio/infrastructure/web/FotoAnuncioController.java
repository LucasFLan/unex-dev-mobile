package com.unex.chamaAi.anuncio.infrastructure.web;

import com.unex.chamaAi.anuncio.infrastructure.web.dto.response.AnuncioResponse;
import com.unex.chamaAi.config.OpenApiConfig;
import com.unex.chamaAi.shared.infrastructure.web.openapi.ExemplosDeErro;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Tag(name = OpenApiConfig.TAG_ANUNCIOS)
public interface FotoAnuncioController {

    @Operation(summary = "Adicionar fotos ao anúncio",
            description = """
                    Só o autor. Envia, em `multipart/form-data`, uma ou mais imagens no campo `fotos` (repita o
                    campo para cada foto). Cada imagem: JPG, PNG ou WEBP, até 5 MB. O anúncio tem no máximo 6 fotos.""")
    @ApiResponse(responseCode = "200", description = "Anúncio com as fotos atualizadas.",
            content = @Content(schema = @Schema(implementation = AnuncioResponse.class)))
    @ApiResponse(responseCode = "400", description = "Nenhuma foto, foto inválida ou mais de 6 no total.",
            content = @Content(mediaType = ExemplosDeErro.PROBLEM_JSON,
                    schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "401", ref = "#/components/responses/NaoAutenticado")
    @ApiResponse(responseCode = "403", description = "O anúncio pertence a outro usuário.", content = @Content(
            mediaType = ExemplosDeErro.PROBLEM_JSON, schema = @Schema(implementation = ProblemDetail.class),
            examples = @ExampleObject(name = "acessoNegado", value = ExemplosDeErro.ACESSO_NEGADO_ANUNCIO)))
    @ApiResponse(responseCode = "404", ref = "#/components/responses/AnuncioNaoEncontrado")
    @PostMapping(value = "/anuncios/{id}/fotos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    AnuncioResponse adicionar(@Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt,
                              @Parameter(description = "Id do anúncio.") @PathVariable UUID id,
                              @Parameter(description = "Fotos (JPG, PNG ou WEBP, até 5 MB cada).")
                              @RequestPart("fotos") List<MultipartFile> fotos);

    @Operation(summary = "Remover foto do anúncio", description = "Só o autor.")
    @ApiResponse(responseCode = "200", description = "Anúncio sem a foto removida.",
            content = @Content(schema = @Schema(implementation = AnuncioResponse.class)))
    @ApiResponse(responseCode = "401", ref = "#/components/responses/NaoAutenticado")
    @ApiResponse(responseCode = "403", description = "O anúncio pertence a outro usuário.", content = @Content(
            mediaType = ExemplosDeErro.PROBLEM_JSON, schema = @Schema(implementation = ProblemDetail.class),
            examples = @ExampleObject(name = "acessoNegado", value = ExemplosDeErro.ACESSO_NEGADO_ANUNCIO)))
    @ApiResponse(responseCode = "404", description = "Anúncio ou foto não encontrados.", content = @Content(
            mediaType = ExemplosDeErro.PROBLEM_JSON, schema = @Schema(implementation = ProblemDetail.class)))
    @DeleteMapping("/anuncios/{id}/fotos/{fotoId}")
    AnuncioResponse remover(@Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt,
                            @Parameter(description = "Id do anúncio.") @PathVariable UUID id,
                            @Parameter(description = "Id da foto.") @PathVariable UUID fotoId);

    @Operation(summary = "Ver foto do anúncio",
            description = "Público para anúncios ativos; o autor também vê as fotos dos próprios anúncios pausados.")
    @ApiResponse(responseCode = "200", description = "A imagem.",
            content = @Content(mediaType = "image/*", schema = @Schema(type = "string", format = "binary")))
    @ApiResponse(responseCode = "404", description = "Foto ou anúncio não encontrados.", content = @Content(
            mediaType = ExemplosDeErro.PROBLEM_JSON, schema = @Schema(implementation = ProblemDetail.class)))
    @SecurityRequirements
    @GetMapping("/anuncios/{id}/fotos/{fotoId}")
    ResponseEntity<byte[]> exibir(@Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt,
                                  @Parameter(description = "Id do anúncio.") @PathVariable UUID id,
                                  @Parameter(description = "Id da foto.") @PathVariable UUID fotoId);
}
