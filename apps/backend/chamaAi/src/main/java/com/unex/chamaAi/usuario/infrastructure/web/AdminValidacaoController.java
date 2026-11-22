package com.unex.chamaAi.usuario.infrastructure.web;

import com.unex.chamaAi.config.OpenApiConfig;
import com.unex.chamaAi.shared.infrastructure.web.openapi.ExemplosDeErro;
import com.unex.chamaAi.usuario.domain.valueobjects.TipoArquivoValidacao;
import com.unex.chamaAi.usuario.infrastructure.web.dto.request.ReprovarValidacaoRequest;
import com.unex.chamaAi.usuario.infrastructure.web.dto.response.EnvioValidacaoResponse;
import com.unex.chamaAi.usuario.infrastructure.web.dto.response.PaginaEnviosValidacaoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@Tag(name = OpenApiConfig.TAG_VALIDACAO)
@RequestMapping("/admin/validacoes")
public interface AdminValidacaoController {

    String ENVIO_EXEMPLO = """
            {"id": "8d7c6b5a-4f3e-4d2c-9b1a-0f9e8d7c6b5a",
             "usuario": {"id": "3f2b6c1e-8a4d-4e0b-9c7a-1d2e3f4a5b6c", "nome": "João da Silva",
                         "email": "joao@unex.edu.br", "cpf": "52998224725", "dataNascimento": "2000-01-31",
                         "telefone": "71999999999"},
             "status": "PENDENTE", "enviadoEm": "2026-11-22T14:00:00.123456Z", "analisadoEm": null,
             "motivoReprovacao": null}""";

    String DESCRICAO_ADMIN = " Só para ADMIN (403 para os demais).";

    @Operation(summary = "Listar envios de validação",
            description = "Fila de análise. Sem `status`, lista os `PENDENTE` do mais antigo ao mais novo." + DESCRICAO_ADMIN)
    @ApiResponse(responseCode = "200", description = "Página de envios.", content = @Content(
            schema = @Schema(implementation = PaginaEnviosValidacaoResponse.class),
            examples = @ExampleObject(name = "fila", value = "{\"itens\": [" + ENVIO_EXEMPLO
                    + "], \"pagina\": 0, \"tamanho\": 20, \"totalItens\": 1, \"totalPaginas\": 1}")))
    @ApiResponse(responseCode = "400", description = "Status ou paginação inválidos.", content = @Content(
            mediaType = ExemplosDeErro.PROBLEM_JSON, schema = @Schema(implementation = ProblemDetail.class),
            examples = @ExampleObject(name = "tamanhoInvalido", value = ExemplosDeErro.TAMANHO_PAGINA_INVALIDO)))
    @ApiResponse(responseCode = "401", ref = "#/components/responses/NaoAutenticado")
    @ApiResponse(responseCode = "403", description = "Usuário sem papel ADMIN.")
    @GetMapping
    PaginaEnviosValidacaoResponse listar(
            @Parameter(description = "`PENDENTE` (padrão), `APROVADO` ou `REPROVADO`.", example = "PENDENTE")
            @RequestParam(required = false) String status,
            @Parameter(description = "Página, começando em 0. Padrão: 0.", example = "0")
            @RequestParam(required = false) Integer pagina,
            @Parameter(description = "Itens por página, de 1 a 50. Padrão: 20.", example = "20")
            @RequestParam(required = false) Integer tamanho);

    @Operation(summary = "Ver envio de validação", description = "Dados do envio e do cadastro." + DESCRICAO_ADMIN)
    @ApiResponse(responseCode = "200", description = "Envio.", content = @Content(
            schema = @Schema(implementation = EnvioValidacaoResponse.class),
            examples = @ExampleObject(name = "envio", value = ENVIO_EXEMPLO)))
    @ApiResponse(responseCode = "401", ref = "#/components/responses/NaoAutenticado")
    @ApiResponse(responseCode = "403", description = "Usuário sem papel ADMIN.")
    @ApiResponse(responseCode = "404", description = "Envio não encontrado.", content = @Content(
            mediaType = ExemplosDeErro.PROBLEM_JSON, schema = @Schema(implementation = ProblemDetail.class),
            examples = @ExampleObject(name = "naoEncontrado", value = ExemplosDeErro.ENVIO_VALIDACAO_NAO_ENCONTRADO)))
    @GetMapping("/{id}")
    EnvioValidacaoResponse detalhar(
            @Parameter(description = "Id do envio.", example = "8d7c6b5a-4f3e-4d2c-9b1a-0f9e8d7c6b5a") @PathVariable UUID id);

    @Operation(summary = "Ver imagem do envio",
            description = "Devolve a imagem (`DOCUMENTO` ou `SELFIE`) com o content type original. Não fica em cache."
                    + DESCRICAO_ADMIN)
    @ApiResponse(responseCode = "200", description = "Imagem.", content = {
            @Content(mediaType = "image/jpeg"), @Content(mediaType = "image/png"), @Content(mediaType = "image/webp")})
    @ApiResponse(responseCode = "401", ref = "#/components/responses/NaoAutenticado")
    @ApiResponse(responseCode = "403", description = "Usuário sem papel ADMIN.")
    @ApiResponse(responseCode = "404", description = "Envio ou imagem não encontrados.", content = @Content(
            mediaType = ExemplosDeErro.PROBLEM_JSON, schema = @Schema(implementation = ProblemDetail.class),
            examples = @ExampleObject(name = "naoEncontrado", value = ExemplosDeErro.ENVIO_VALIDACAO_NAO_ENCONTRADO)))
    @GetMapping("/{id}/arquivos/{tipo}")
    ResponseEntity<byte[]> arquivo(
            @Parameter(description = "Id do envio.", example = "8d7c6b5a-4f3e-4d2c-9b1a-0f9e8d7c6b5a") @PathVariable UUID id,
            @Parameter(description = "`DOCUMENTO` ou `SELFIE`.", example = "DOCUMENTO") @PathVariable TipoArquivoValidacao tipo);

    @Operation(summary = "Aprovar validação",
            description = "Aprova o envio: a conta passa a `APROVADO` e o usuário é notificado." + DESCRICAO_ADMIN)
    @ApiResponse(responseCode = "200", description = "Envio aprovado.", content = @Content(
            schema = @Schema(implementation = EnvioValidacaoResponse.class),
            examples = @ExampleObject(name = "aprovado", value = ENVIO_EXEMPLO)))
    @ApiResponse(responseCode = "401", ref = "#/components/responses/NaoAutenticado")
    @ApiResponse(responseCode = "403", description = "Usuário sem papel ADMIN.")
    @ApiResponse(responseCode = "404", description = "Envio não encontrado.", content = @Content(
            mediaType = ExemplosDeErro.PROBLEM_JSON, schema = @Schema(implementation = ProblemDetail.class),
            examples = @ExampleObject(name = "naoEncontrado", value = ExemplosDeErro.ENVIO_VALIDACAO_NAO_ENCONTRADO)))
    @ApiResponse(responseCode = "422", description = "O envio já foi analisado.", content = @Content(
            mediaType = ExemplosDeErro.PROBLEM_JSON, schema = @Schema(implementation = ProblemDetail.class),
            examples = @ExampleObject(name = "jaAnalisado", value = ExemplosDeErro.ENVIO_VALIDACAO_JA_ANALISADO)))
    @PatchMapping("/{id}/aprovar")
    EnvioValidacaoResponse aprovar(@Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt,
                                   @Parameter(description = "Id do envio.", example = "8d7c6b5a-4f3e-4d2c-9b1a-0f9e8d7c6b5a")
                                   @PathVariable UUID id);

    @Operation(summary = "Reprovar validação",
            description = "Reprova o envio com um motivo que o usuário vai ver; ele pode reenviar os documentos."
                    + DESCRICAO_ADMIN,
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, content = @Content(
                    examples = @ExampleObject(name = "motivo", value = """
                            {"motivo": "A foto do documento está desfocada. Envie de novo com boa iluminação."}"""))))
    @ApiResponse(responseCode = "200", description = "Envio reprovado.", content = @Content(
            schema = @Schema(implementation = EnvioValidacaoResponse.class),
            examples = @ExampleObject(name = "reprovado", value = ENVIO_EXEMPLO)))
    @ApiResponse(responseCode = "400", description = "Motivo ausente ou curto demais.", content = @Content(
            mediaType = ExemplosDeErro.PROBLEM_JSON, schema = @Schema(implementation = ProblemDetail.class),
            examples = @ExampleObject(name = "motivoObrigatorio", value = ExemplosDeErro.MOTIVO_REPROVACAO_OBRIGATORIO)))
    @ApiResponse(responseCode = "401", ref = "#/components/responses/NaoAutenticado")
    @ApiResponse(responseCode = "403", description = "Usuário sem papel ADMIN.")
    @ApiResponse(responseCode = "404", description = "Envio não encontrado.", content = @Content(
            mediaType = ExemplosDeErro.PROBLEM_JSON, schema = @Schema(implementation = ProblemDetail.class),
            examples = @ExampleObject(name = "naoEncontrado", value = ExemplosDeErro.ENVIO_VALIDACAO_NAO_ENCONTRADO)))
    @ApiResponse(responseCode = "422", description = "O envio já foi analisado.", content = @Content(
            mediaType = ExemplosDeErro.PROBLEM_JSON, schema = @Schema(implementation = ProblemDetail.class),
            examples = @ExampleObject(name = "jaAnalisado", value = ExemplosDeErro.ENVIO_VALIDACAO_JA_ANALISADO)))
    @PatchMapping("/{id}/reprovar")
    EnvioValidacaoResponse reprovar(@Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt,
                                    @Parameter(description = "Id do envio.", example = "8d7c6b5a-4f3e-4d2c-9b1a-0f9e8d7c6b5a")
                                    @PathVariable UUID id,
                                    @RequestBody ReprovarValidacaoRequest request);
}
