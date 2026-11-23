package com.unex.chamaAi.usuario.infrastructure.web;

import com.unex.chamaAi.config.OpenApiConfig;
import com.unex.chamaAi.shared.infrastructure.web.openapi.ExemplosDeErro;
import com.unex.chamaAi.usuario.infrastructure.web.dto.response.PerfilUsuarioResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Tag(name = OpenApiConfig.TAG_USUARIOS)
public interface FotoPerfilController {

    @Operation(summary = "Trocar minha foto de perfil",
            description = "Envia, em `multipart/form-data`, a nova foto no campo `foto` (JPG, PNG ou WEBP, até 5 MB).")
    @ApiResponse(responseCode = "200", description = "Perfil com o novo `fotoPerfil`.",
            content = @Content(schema = @Schema(implementation = PerfilUsuarioResponse.class)))
    @ApiResponse(responseCode = "400", description = "Imagem faltando, grande demais ou em formato não aceito.",
            content = @Content(mediaType = ExemplosDeErro.PROBLEM_JSON,
                    schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "401", ref = "#/components/responses/NaoAutenticado")
    @PutMapping(value = "/usuarios/me/foto", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    PerfilUsuarioResponse enviar(@Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt,
                                 @Parameter(description = "Foto (JPG, PNG ou WEBP, até 5 MB).")
                                 @RequestPart("foto") MultipartFile foto);

    @Operation(summary = "Remover minha foto de perfil")
    @ApiResponse(responseCode = "200", description = "Perfil sem foto (`fotoPerfil` nulo).",
            content = @Content(schema = @Schema(implementation = PerfilUsuarioResponse.class)))
    @ApiResponse(responseCode = "401", ref = "#/components/responses/NaoAutenticado")
    @DeleteMapping("/usuarios/me/foto")
    PerfilUsuarioResponse remover(@Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt);

    @Operation(summary = "Ver a foto de perfil de um usuário",
            description = "Público. Use o caminho de `fotoPerfil`: o parâmetro `v` muda a cada troca de foto.")
    @ApiResponse(responseCode = "200", description = "A imagem.",
            content = @Content(mediaType = "image/*", schema = @Schema(type = "string", format = "binary")))
    @ApiResponse(responseCode = "404", description = "O usuário não tem foto de perfil.",
            content = @Content(mediaType = ExemplosDeErro.PROBLEM_JSON,
                    schema = @Schema(implementation = ProblemDetail.class)))
    @SecurityRequirements
    @GetMapping("/usuarios/{id}/foto")
    ResponseEntity<byte[]> exibir(@Parameter(description = "Id do usuário.") @PathVariable UUID id);
}
