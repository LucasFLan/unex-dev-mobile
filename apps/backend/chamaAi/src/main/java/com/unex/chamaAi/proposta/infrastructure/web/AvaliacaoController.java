package com.unex.chamaAi.proposta.infrastructure.web;

import com.unex.chamaAi.config.OpenApiConfig;
import com.unex.chamaAi.proposta.infrastructure.web.dto.request.AvaliacaoRequest;
import com.unex.chamaAi.proposta.infrastructure.web.dto.response.AvaliacaoResponse;
import com.unex.chamaAi.proposta.infrastructure.web.dto.response.AvaliacoesDoUsuarioResponse;
import com.unex.chamaAi.shared.infrastructure.web.openapi.ExemplosDeErro;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@Tag(name = OpenApiConfig.TAG_PROPOSTAS)
public interface AvaliacaoController {

    @Operation(summary = "Avaliar serviço concluído",
            description = """
                    Quem contratou avalia o profissional depois de marcar o serviço como concluído
                    (`PATCH /propostas/{id}/concluir`). Uma avaliação por serviço; o profissional é notificado.

                    Regras:
                    - **nota**: obrigatória, de 1 a 5.
                    - **comentario**: opcional, até 500 caracteres.""",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, content = @Content(
                    examples = @ExampleObject(name = "avaliacao", value = """
                            {"nota": 5, "comentario": "Pontual e caprichoso, recomendo."}"""))))
    @ApiResponse(responseCode = "201", description = "Avaliação registrada.", content = @Content(
            schema = @Schema(implementation = AvaliacaoResponse.class)))
    @ApiResponse(responseCode = "400", description = "Nota fora de 1 a 5 ou comentário longo demais.", content = @Content(
            mediaType = ExemplosDeErro.PROBLEM_JSON, schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "401", ref = "#/components/responses/NaoAutenticado")
    @ApiResponse(responseCode = "403", description = "Só quem contratou avalia.", content = @Content(
            mediaType = ExemplosDeErro.PROBLEM_JSON, schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "404", description = "Proposta inexistente ou de outras pessoas.", content = @Content(
            mediaType = ExemplosDeErro.PROBLEM_JSON, schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "409", description = "O serviço já foi avaliado.", content = @Content(
            mediaType = ExemplosDeErro.PROBLEM_JSON, schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "422", description = "O serviço ainda não foi marcado como concluído.", content = @Content(
            mediaType = ExemplosDeErro.PROBLEM_JSON, schema = @Schema(implementation = ProblemDetail.class)))
    @PostMapping("/propostas/{id}/avaliacao")
    @ResponseStatus(HttpStatus.CREATED)
    AvaliacaoResponse avaliar(@Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt,
                              @Parameter(description = "Id da proposta.") @PathVariable UUID id,
                              @RequestBody AvaliacaoRequest request);

    @Operation(summary = "Avaliações de um usuário",
            description = "Média, total e as avaliações recebidas por um profissional (mais recentes primeiro). "
                    + "Exige login.")
    @ApiResponse(responseCode = "200", description = "Reputação do usuário.", content = @Content(
            schema = @Schema(implementation = AvaliacoesDoUsuarioResponse.class)))
    @ApiResponse(responseCode = "401", ref = "#/components/responses/NaoAutenticado")
    @ApiResponse(responseCode = "404", ref = "#/components/responses/UsuarioNaoEncontrado")
    @GetMapping("/usuarios/{id}/avaliacoes")
    AvaliacoesDoUsuarioResponse listar(@Parameter(description = "Id do usuário.") @PathVariable UUID id,
                                       @Parameter(description = "Página (começa em 0).")
                                       @RequestParam(required = false) Integer pagina,
                                       @Parameter(description = "Itens por página (1 a 50; padrão 20).")
                                       @RequestParam(required = false) Integer tamanho);
}
