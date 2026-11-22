package com.unex.chamaAi.proposta.infrastructure.web;

import com.unex.chamaAi.config.OpenApiConfig;
import com.unex.chamaAi.proposta.infrastructure.web.dto.request.PropostaRequest;
import com.unex.chamaAi.proposta.infrastructure.web.dto.response.PaginaPropostasResponse;
import com.unex.chamaAi.proposta.infrastructure.web.dto.response.PropostaResponse;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@Tag(name = OpenApiConfig.TAG_PROPOSTAS)
public interface PropostaController {

    String PROPOSTA_EXEMPLO = """
            {"id": "c4d5e6f7-1a2b-4c3d-8e9f-0a1b2c3d4e5f",
             "anuncio": {"id": "7a1c9e2b-4d3f-4b8a-9e6d-2c5f8a1b3d4e", "titulo": "Preciso trocar um chuveiro",
                         "tipo": "PEDIDO"},
             "remetente": {"id": "3f2b6c1e-8a4d-4e0b-9c7a-1d2e3f4a5b6c", "nome": "Maria Souza", "telefone": null},
             "destinatario": {"id": "9e8d7c6b-5a4f-4e3d-2c1b-0a9f8e7d6c5b", "nome": "João Silva", "telefone": null},
             "valor": 180.00, "mensagem": "Posso fazer amanhã pela manhã, levo o material.", "status": "PENDENTE",
             "criadoEm": "2026-11-20T21:30:00.123456Z", "respondidoEm": null}""";

    String PROPOSTA_ACEITA_EXEMPLO = """
            {"id": "c4d5e6f7-1a2b-4c3d-8e9f-0a1b2c3d4e5f",
             "anuncio": {"id": "7a1c9e2b-4d3f-4b8a-9e6d-2c5f8a1b3d4e", "titulo": "Preciso trocar um chuveiro",
                         "tipo": "PEDIDO"},
             "remetente": {"id": "3f2b6c1e-8a4d-4e0b-9c7a-1d2e3f4a5b6c", "nome": "Maria Souza",
                           "telefone": "71988887777"},
             "destinatario": {"id": "9e8d7c6b-5a4f-4e3d-2c1b-0a9f8e7d6c5b", "nome": "João Silva",
                              "telefone": "71999999999"},
             "valor": 180.00, "mensagem": "Posso fazer amanhã pela manhã, levo o material.", "status": "ACEITA",
             "criadoEm": "2026-11-20T21:30:00.123456Z", "respondidoEm": "2026-11-21T09:15:00.654321Z"}""";

    String PAGINA_EXEMPLO = "{\"itens\": [" + PROPOSTA_EXEMPLO
            + "], \"pagina\": 0, \"tamanho\": 20, \"totalItens\": 1, \"totalPaginas\": 1}";

    @Operation(summary = "Enviar proposta",
            description = """
                    Envia uma proposta para o autor do anúncio: um profissional respondendo a um **pedido**, ou um
                    cliente pedindo orçamento para uma **oferta**. O autor recebe uma notificação
                    (`GET /usuarios/me/notificacoes`).

                    Regras:
                    - **valor**: opcional; maior que zero, até 2 casas decimais. Ausente ou `null` = "a combinar".
                    - **mensagem**: obrigatória, até 1000 caracteres.
                    - Não é possível propor no próprio anúncio (422) nem enviar duas propostas para o mesmo
                      anúncio (409).""",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, content = @Content(
                    schema = @Schema(implementation = PropostaRequest.class),
                    examples = @ExampleObject(name = "proposta", value = """
                            {"valor": 180.00, "mensagem": "Posso fazer amanhã pela manhã, levo o material."}"""))))
    @ApiResponse(responseCode = "201", description = "Proposta enviada.", content = @Content(
            schema = @Schema(implementation = PropostaResponse.class),
            examples = @ExampleObject(name = "enviada", value = PROPOSTA_EXEMPLO)))
    @ApiResponse(responseCode = "400", description = "Dado inválido. O `detail` diz qual regra falhou.", content = @Content(
            mediaType = ExemplosDeErro.PROBLEM_JSON, schema = @Schema(implementation = ProblemDetail.class),
            examples = {
                    @ExampleObject(name = "mensagemObrigatoria", value = ExemplosDeErro.MENSAGEM_PROPOSTA_OBRIGATORIA),
                    @ExampleObject(name = "valorInvalido", value = ExemplosDeErro.PRECO_INVALIDO)}))
    @ApiResponse(responseCode = "401", ref = "#/components/responses/NaoAutenticado")
    @ApiResponse(responseCode = "404", ref = "#/components/responses/AnuncioNaoEncontrado")
    @ApiResponse(responseCode = "409", description = "Você já enviou uma proposta para este anúncio.", content = @Content(
            mediaType = ExemplosDeErro.PROBLEM_JSON, schema = @Schema(implementation = ProblemDetail.class),
            examples = @ExampleObject(name = "duplicada", value = ExemplosDeErro.PROPOSTA_DUPLICADA)))
    @ApiResponse(responseCode = "422", description = "O anúncio é seu.", content = @Content(
            mediaType = ExemplosDeErro.PROBLEM_JSON, schema = @Schema(implementation = ProblemDetail.class),
            examples = @ExampleObject(name = "proprioAnuncio", value = ExemplosDeErro.PROPOSTA_PROPRIO_ANUNCIO)))
    @PostMapping("/anuncios/{id}/propostas")
    @ResponseStatus(HttpStatus.CREATED)
    PropostaResponse enviar(@Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt,
                            @Parameter(description = "Id do anúncio.", example = "7a1c9e2b-4d3f-4b8a-9e6d-2c5f8a1b3d4e")
                            @PathVariable UUID id,
                            @RequestBody PropostaRequest request);

    @Operation(summary = "Listar propostas recebidas",
            description = "Propostas enviadas para os anúncios do usuário do token, da mais recente para a mais antiga.")
    @ApiResponse(responseCode = "200", description = "Página com as propostas recebidas.", content = @Content(
            schema = @Schema(implementation = PaginaPropostasResponse.class),
            examples = @ExampleObject(name = "pagina", value = PAGINA_EXEMPLO)))
    @ApiResponse(responseCode = "400", description = "Paginação inválida.", content = @Content(
            mediaType = ExemplosDeErro.PROBLEM_JSON, schema = @Schema(implementation = ProblemDetail.class),
            examples = @ExampleObject(name = "tamanhoInvalido", value = ExemplosDeErro.TAMANHO_PAGINA_INVALIDO)))
    @ApiResponse(responseCode = "401", ref = "#/components/responses/NaoAutenticado")
    @GetMapping("/usuarios/me/propostas/recebidas")
    PaginaPropostasResponse listarRecebidas(
            @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt,
            @Parameter(description = "Página, começando em 0. Padrão: 0.", example = "0")
            @RequestParam(required = false) Integer pagina,
            @Parameter(description = "Itens por página, de 1 a 50. Padrão: 20.", example = "20")
            @RequestParam(required = false) Integer tamanho);

    @Operation(summary = "Listar propostas enviadas",
            description = "Propostas que o usuário do token enviou, da mais recente para a mais antiga.")
    @ApiResponse(responseCode = "200", description = "Página com as propostas enviadas.", content = @Content(
            schema = @Schema(implementation = PaginaPropostasResponse.class),
            examples = @ExampleObject(name = "pagina", value = PAGINA_EXEMPLO)))
    @ApiResponse(responseCode = "400", description = "Paginação inválida.", content = @Content(
            mediaType = ExemplosDeErro.PROBLEM_JSON, schema = @Schema(implementation = ProblemDetail.class),
            examples = @ExampleObject(name = "tamanhoInvalido", value = ExemplosDeErro.TAMANHO_PAGINA_INVALIDO)))
    @ApiResponse(responseCode = "401", ref = "#/components/responses/NaoAutenticado")
    @GetMapping("/usuarios/me/propostas/enviadas")
    PaginaPropostasResponse listarEnviadas(
            @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt,
            @Parameter(description = "Página, começando em 0. Padrão: 0.", example = "0")
            @RequestParam(required = false) Integer pagina,
            @Parameter(description = "Itens por página, de 1 a 50. Padrão: 20.", example = "20")
            @RequestParam(required = false) Integer tamanho);

    @Operation(summary = "Aceitar proposta",
            description = """
                    Só o autor do anúncio aceita. A proposta fica `ACEITA` e quem enviou é notificado; a partir daí
                    os dois veem o telefone um do outro na proposta.

                    Num **PEDIDO** o cliente já contratou alguém: o pedido é **pausado** (sai da listagem) e as outras
                    propostas pendentes viram `NAO_SELECIONADA`, com aviso para cada profissional. Numa **OFERTA**
                    o profissional pode atender vários clientes, então a oferta e as outras propostas não mudam.""")
    @ApiResponse(responseCode = "200", description = "Proposta aceita.", content = @Content(
            schema = @Schema(implementation = PropostaResponse.class),
            examples = @ExampleObject(name = "aceita", value = PROPOSTA_ACEITA_EXEMPLO)))
    @ApiResponse(responseCode = "401", ref = "#/components/responses/NaoAutenticado")
    @ApiResponse(responseCode = "403", description = "Quem enviou não pode responder à própria proposta.", content = @Content(
            mediaType = ExemplosDeErro.PROBLEM_JSON, schema = @Schema(implementation = ProblemDetail.class),
            examples = @ExampleObject(name = "acessoNegado", value = ExemplosDeErro.ACESSO_NEGADO_RESPONDER_PROPOSTA)))
    @ApiResponse(responseCode = "404", description = "Proposta inexistente ou de outras pessoas.", content = @Content(
            mediaType = ExemplosDeErro.PROBLEM_JSON, schema = @Schema(implementation = ProblemDetail.class),
            examples = @ExampleObject(name = "naoEncontrada", value = ExemplosDeErro.PROPOSTA_NAO_ENCONTRADA)))
    @ApiResponse(responseCode = "422", description = "A proposta não está mais pendente.", content = @Content(
            mediaType = ExemplosDeErro.PROBLEM_JSON, schema = @Schema(implementation = ProblemDetail.class),
            examples = @ExampleObject(name = "jaRespondida", value = ExemplosDeErro.PROPOSTA_JA_RESPONDIDA)))
    @PatchMapping("/propostas/{id}/aceitar")
    PropostaResponse aceitar(@Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt,
                             @Parameter(description = "Id da proposta.", example = "c4d5e6f7-1a2b-4c3d-8e9f-0a1b2c3d4e5f")
                             @PathVariable UUID id);

    @Operation(summary = "Recusar proposta",
            description = "Só o autor do anúncio recusa. A proposta fica `RECUSADA` e quem enviou é notificado.")
    @ApiResponse(responseCode = "200", description = "Proposta recusada.", content = @Content(
            schema = @Schema(implementation = PropostaResponse.class),
            examples = @ExampleObject(name = "recusada", value = PROPOSTA_EXEMPLO)))
    @ApiResponse(responseCode = "401", ref = "#/components/responses/NaoAutenticado")
    @ApiResponse(responseCode = "403", description = "Quem enviou não pode responder à própria proposta.", content = @Content(
            mediaType = ExemplosDeErro.PROBLEM_JSON, schema = @Schema(implementation = ProblemDetail.class),
            examples = @ExampleObject(name = "acessoNegado", value = ExemplosDeErro.ACESSO_NEGADO_RESPONDER_PROPOSTA)))
    @ApiResponse(responseCode = "404", description = "Proposta inexistente ou de outras pessoas.", content = @Content(
            mediaType = ExemplosDeErro.PROBLEM_JSON, schema = @Schema(implementation = ProblemDetail.class),
            examples = @ExampleObject(name = "naoEncontrada", value = ExemplosDeErro.PROPOSTA_NAO_ENCONTRADA)))
    @ApiResponse(responseCode = "422", description = "A proposta não está mais pendente.", content = @Content(
            mediaType = ExemplosDeErro.PROBLEM_JSON, schema = @Schema(implementation = ProblemDetail.class),
            examples = @ExampleObject(name = "jaRespondida", value = ExemplosDeErro.PROPOSTA_JA_RESPONDIDA)))
    @PatchMapping("/propostas/{id}/recusar")
    PropostaResponse recusar(@Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt,
                             @Parameter(description = "Id da proposta.", example = "c4d5e6f7-1a2b-4c3d-8e9f-0a1b2c3d4e5f")
                             @PathVariable UUID id);

    @Operation(summary = "Cancelar proposta",
            description = "Só quem enviou cancela, e só enquanto estiver `PENDENTE`. A proposta fica `CANCELADA`.")
    @ApiResponse(responseCode = "200", description = "Proposta cancelada.", content = @Content(
            schema = @Schema(implementation = PropostaResponse.class),
            examples = @ExampleObject(name = "cancelada", value = PROPOSTA_EXEMPLO)))
    @ApiResponse(responseCode = "401", ref = "#/components/responses/NaoAutenticado")
    @ApiResponse(responseCode = "403", description = "Só quem enviou pode cancelar.", content = @Content(
            mediaType = ExemplosDeErro.PROBLEM_JSON, schema = @Schema(implementation = ProblemDetail.class),
            examples = @ExampleObject(name = "acessoNegado", value = ExemplosDeErro.ACESSO_NEGADO_CANCELAR_PROPOSTA)))
    @ApiResponse(responseCode = "404", description = "Proposta inexistente ou de outras pessoas.", content = @Content(
            mediaType = ExemplosDeErro.PROBLEM_JSON, schema = @Schema(implementation = ProblemDetail.class),
            examples = @ExampleObject(name = "naoEncontrada", value = ExemplosDeErro.PROPOSTA_NAO_ENCONTRADA)))
    @ApiResponse(responseCode = "422", description = "A proposta não está mais pendente.", content = @Content(
            mediaType = ExemplosDeErro.PROBLEM_JSON, schema = @Schema(implementation = ProblemDetail.class),
            examples = @ExampleObject(name = "jaRespondida", value = ExemplosDeErro.PROPOSTA_JA_RESPONDIDA)))
    @PatchMapping("/propostas/{id}/cancelar")
    PropostaResponse cancelar(@Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt,
                             @Parameter(description = "Id da proposta.", example = "c4d5e6f7-1a2b-4c3d-8e9f-0a1b2c3d4e5f")
                             @PathVariable UUID id);
}
