package com.unex.chamaAi.notificacao.infrastructure.web;

import com.unex.chamaAi.config.OpenApiConfig;
import com.unex.chamaAi.notificacao.infrastructure.web.dto.response.ContagemNotificacoesResponse;
import com.unex.chamaAi.notificacao.infrastructure.web.dto.response.PaginaNotificacoesResponse;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@Tag(name = OpenApiConfig.TAG_NOTIFICACOES)
public interface NotificacaoController {

    String PAGINA_EXEMPLO = """
            {"itens": [{"id": "5b6c7d8e-9f0a-4b1c-8d2e-3f4a5b6c7d8e", "tipo": "PROPOSTA_RECEBIDA",
              "titulo": "Nova proposta recebida",
              "mensagem": "Maria Souza enviou uma proposta de R$ 180,00 para \\"Preciso trocar um chuveiro\\": Posso fazer amanhã pela manhã, levo o material.",
              "anuncioId": "7a1c9e2b-4d3f-4b8a-9e6d-2c5f8a1b3d4e", "propostaId": "c4d5e6f7-1a2b-4c3d-8e9f-0a1b2c3d4e5f",
              "lida": false, "criadoEm": "2026-11-20T21:30:00.123456Z"}],
             "pagina": 0, "tamanho": 20, "totalItens": 1, "totalPaginas": 1}""";

    @Operation(summary = "Listar minhas notificações",
            description = """
                    Notificações do usuário do token, da mais recente para a mais antiga, lidas e não lidas.
                    Listar **não** marca como lidas: use `PATCH /usuarios/me/notificacoes/lidas`.""")
    @ApiResponse(responseCode = "200", description = "Página com as notificações.", content = @Content(
            schema = @Schema(implementation = PaginaNotificacoesResponse.class),
            examples = @ExampleObject(name = "pagina", value = PAGINA_EXEMPLO)))
    @ApiResponse(responseCode = "400", description = "Paginação inválida.", content = @Content(
            mediaType = ExemplosDeErro.PROBLEM_JSON, schema = @Schema(implementation = ProblemDetail.class),
            examples = @ExampleObject(name = "tamanhoInvalido", value = ExemplosDeErro.TAMANHO_PAGINA_INVALIDO)))
    @ApiResponse(responseCode = "401", ref = "#/components/responses/NaoAutenticado")
    @GetMapping("/usuarios/me/notificacoes")
    PaginaNotificacoesResponse listar(
            @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt,
            @Parameter(description = "Página, começando em 0. Padrão: 0.", example = "0")
            @RequestParam(required = false) Integer pagina,
            @Parameter(description = "Itens por página, de 1 a 50. Padrão: 20.", example = "20")
            @RequestParam(required = false) Integer tamanho);

    @Operation(summary = "Contar notificações não lidas",
            description = "Quantidade para mostrar no ícone de notificações.")
    @ApiResponse(responseCode = "200", description = "Contagem de não lidas.", content = @Content(
            schema = @Schema(implementation = ContagemNotificacoesResponse.class),
            examples = @ExampleObject(name = "contagem", value = "{\"naoLidas\": 2}")))
    @ApiResponse(responseCode = "401", ref = "#/components/responses/NaoAutenticado")
    @GetMapping("/usuarios/me/notificacoes/nao-lidas")
    ContagemNotificacoesResponse contarNaoLidas(@Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt);

    @Operation(summary = "Marcar todas como lidas",
            description = "Marca todas as notificações do usuário do token como lidas. Não é erro se não houver nenhuma.")
    @ApiResponse(responseCode = "204", description = "Notificações marcadas como lidas.")
    @ApiResponse(responseCode = "401", ref = "#/components/responses/NaoAutenticado")
    @PatchMapping("/usuarios/me/notificacoes/lidas")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void marcarTodasComoLidas(@Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt);

    @Operation(summary = "Marcar notificação como lida",
            description = "Marca uma notificação do usuário do token como lida. Notificação de outra pessoa responde 404.")
    @ApiResponse(responseCode = "204", description = "Notificação marcada como lida.")
    @ApiResponse(responseCode = "401", ref = "#/components/responses/NaoAutenticado")
    @ApiResponse(responseCode = "404", description = "Notificação inexistente ou de outro usuário.", content = @Content(
            mediaType = ExemplosDeErro.PROBLEM_JSON, schema = @Schema(implementation = ProblemDetail.class),
            examples = @ExampleObject(name = "naoEncontrada", value = ExemplosDeErro.NOTIFICACAO_NAO_ENCONTRADA)))
    @PatchMapping("/notificacoes/{id}/lida")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void marcarComoLida(@Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt,
                        @Parameter(description = "Id da notificação.", example = "5b6c7d8e-9f0a-4b1c-8d2e-3f4a5b6c7d8e")
                        @PathVariable UUID id);
}
