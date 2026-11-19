package com.unex.chamaAi.anuncio.infrastructure.web;

import com.unex.chamaAi.anuncio.infrastructure.web.dto.request.AnuncioRequest;
import com.unex.chamaAi.anuncio.infrastructure.web.dto.response.AnuncioResponse;
import com.unex.chamaAi.anuncio.infrastructure.web.dto.response.PaginaAnunciosResponse;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@Tag(name = OpenApiConfig.TAG_ANUNCIOS)
public interface AnuncioController {

    String ANUNCIO_EXEMPLO = """
            {"id": "7a1c9e2b-4d3f-4b8a-9e6d-2c5f8a1b3d4e", "tipo": "OFERTA", "titulo": "Eletricista residencial",
             "descricao": "Instalação de chuveiros, tomadas e quadros de luz.", "preco": 150.00,
             "categoria": {"id": "0b9d3c4e-6f1a-4c2b-8d7e-5a4b3c2d1e0f", "slug": "eletricista", "nome": "Eletricista"},
             "cidade": "Salvador", "uf": "BA", "ativo": true, "autorId": "3f2b6c1e-8a4d-4e0b-9c7a-1d2e3f4a5b6c",
             "criadoEm": "2026-11-16T21:30:00.123456Z", "atualizadoEm": "2026-11-16T21:30:00.123456Z"}""";

    String PAGINA_EXEMPLO = """
            {"itens": [{"id": "7a1c9e2b-4d3f-4b8a-9e6d-2c5f8a1b3d4e", "tipo": "OFERTA",
              "titulo": "Eletricista residencial", "descricao": "Instalação de chuveiros, tomadas e quadros de luz.",
              "preco": 150.00,
              "categoria": {"id": "0b9d3c4e-6f1a-4c2b-8d7e-5a4b3c2d1e0f", "slug": "eletricista", "nome": "Eletricista"},
              "cidade": "Salvador", "uf": "BA", "ativo": true, "autorId": "3f2b6c1e-8a4d-4e0b-9c7a-1d2e3f4a5b6c",
              "criadoEm": "2026-11-16T21:30:00.123456Z", "atualizadoEm": "2026-11-16T21:30:00.123456Z"}],
             "pagina": 0, "tamanho": 20, "totalItens": 1, "totalPaginas": 1}""";

    String CORPO_EXEMPLO = """
            {"tipo": "OFERTA", "categoriaId": "0b9d3c4e-6f1a-4c2b-8d7e-5a4b3c2d1e0f",
             "titulo": "Eletricista residencial", "descricao": "Instalação de chuveiros, tomadas e quadros de luz.",
             "preco": 150.00, "cidade": "Salvador", "uf": "BA"}""";

    String CORPO_PEDIDO_EXEMPLO = """
            {"tipo": "PEDIDO", "categoriaId": "0b9d3c4e-6f1a-4c2b-8d7e-5a4b3c2d1e0f",
             "titulo": "Preciso trocar um chuveiro", "descricao": "Chuveiro queimou, preciso de alguém hoje.",
             "preco": null, "cidade": "Lauro de Freitas", "uf": "BA"}""";

    String REGRAS = """
            Regras:
            - **tipo**: `OFERTA` ou `PEDIDO` (aceita minúsculas).
            - **categoriaId**: id de uma categoria existente (`GET /categorias`).
            - **titulo**: 3 a 120 caracteres.
            - **descricao**: obrigatória, até 5000 caracteres.
            - **preco**: opcional; maior que zero, até 2 casas decimais. Ausente ou `null` = "a combinar".
            - **cidade**: obrigatória, até 100 caracteres.
            - **uf**: sigla válida de um dos 27 estados.""";

    @Operation(summary = "Criar anúncio",
            description = "Cria um anúncio ativo cujo autor é o usuário do token.\n\n" + REGRAS,
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, content = @Content(
                    examples = {
                            @ExampleObject(name = "oferta", summary = "Oferta com preço", value = CORPO_EXEMPLO),
                            @ExampleObject(name = "pedido", summary = "Pedido a combinar", value = CORPO_PEDIDO_EXEMPLO)})))
    @ApiResponse(responseCode = "201", description = "Anúncio criado.", content = @Content(
            schema = @Schema(implementation = AnuncioResponse.class),
            examples = @ExampleObject(name = "criado", value = ANUNCIO_EXEMPLO)))
    @ApiResponse(responseCode = "400", description = "Algum campo viola as regras, ou o JSON é malformado. "
            + "O `detail` diz qual regra falhou.", content = @Content(
            mediaType = ExemplosDeErro.PROBLEM_JSON, schema = @Schema(implementation = ProblemDetail.class),
            examples = {
                    @ExampleObject(name = "tipoInvalido", summary = "Tipo diferente de OFERTA/PEDIDO",
                            value = ExemplosDeErro.TIPO_ANUNCIO_INVALIDO),
                    @ExampleObject(name = "categoriaInvalida", summary = "Categoria inexistente",
                            value = ExemplosDeErro.CATEGORIA_INVALIDA),
                    @ExampleObject(name = "precoInvalido", summary = "Preço zero ou negativo",
                            value = ExemplosDeErro.PRECO_INVALIDO),
                    @ExampleObject(name = "ufInvalida", summary = "UF inexistente", value = ExemplosDeErro.UF_INVALIDA)}))
    @ApiResponse(responseCode = "401", ref = "#/components/responses/NaoAutenticado")
    @PostMapping("/anuncios")
    @ResponseStatus(HttpStatus.CREATED)
    AnuncioResponse criar(@Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt,
                          @RequestBody AnuncioRequest request);

    @Operation(summary = "Listar anúncios",
            description = """
                    Rota pública. Lista só anúncios **ativos**, do mais recente para o mais antigo. Todos os
                    filtros são opcionais e se combinam (E lógico).""")
    @SecurityRequirements
    @ApiResponse(responseCode = "200", description = "Página de anúncios.", content = @Content(
            schema = @Schema(implementation = PaginaAnunciosResponse.class),
            examples = @ExampleObject(name = "pagina", value = PAGINA_EXEMPLO)))
    @ApiResponse(responseCode = "400", description = "Filtro ou paginação inválidos.", content = @Content(
            mediaType = ExemplosDeErro.PROBLEM_JSON, schema = @Schema(implementation = ProblemDetail.class),
            examples = {
                    @ExampleObject(name = "tamanhoInvalido", summary = "tamanho fora de 1..50",
                            value = ExemplosDeErro.TAMANHO_PAGINA_INVALIDO),
                    @ExampleObject(name = "ufInvalida", summary = "UF inexistente", value = ExemplosDeErro.UF_INVALIDA),
                    @ExampleObject(name = "categoriaIdMalformado", summary = "categoriaId não é UUID",
                            value = ExemplosDeErro.PARAMETRO_INVALIDO)}))
    @GetMapping("/anuncios")
    PaginaAnunciosResponse listar(
            @Parameter(description = "`OFERTA` ou `PEDIDO`.", example = "OFERTA",
                    schema = @Schema(allowableValues = {"OFERTA", "PEDIDO"}))
            @RequestParam(required = false) String tipo,
            @Parameter(description = "Id da categoria.", example = "0b9d3c4e-6f1a-4c2b-8d7e-5a4b3c2d1e0f")
            @RequestParam(required = false) UUID categoriaId,
            @Parameter(description = "Cidade exata, sem diferenciar maiúsculas de minúsculas.", example = "Salvador")
            @RequestParam(required = false) String cidade,
            @Parameter(description = "Sigla da UF.", example = "BA")
            @RequestParam(required = false) String uf,
            @Parameter(description = "Página, começando em 0. Padrão: 0.", example = "0")
            @RequestParam(required = false) Integer pagina,
            @Parameter(description = "Itens por página, de 1 a 50. Padrão: 20.", example = "20")
            @RequestParam(required = false) Integer tamanho);

    @Operation(summary = "Ver anúncio",
            description = """
                    Pode ser chamada sem token. Anúncio **inativo** só é retornado para o autor; para qualquer
                    outra pessoa (ou sem token) responde 404, como se não existisse. Se um token for enviado,
                    ele precisa ser válido.""")
    @ApiResponse(responseCode = "200", description = "Anúncio encontrado.", content = @Content(
            schema = @Schema(implementation = AnuncioResponse.class),
            examples = @ExampleObject(name = "anuncio", value = ANUNCIO_EXEMPLO)))
    @ApiResponse(responseCode = "400", description = "O id não é um UUID.", content = @Content(
            mediaType = ExemplosDeErro.PROBLEM_JSON, schema = @Schema(implementation = ProblemDetail.class),
            examples = @ExampleObject(name = "idInvalido", value = ExemplosDeErro.ID_INVALIDO)))
    @ApiResponse(responseCode = "401", ref = "#/components/responses/NaoAutenticado")
    @ApiResponse(responseCode = "404", ref = "#/components/responses/AnuncioNaoEncontrado")
    @GetMapping("/anuncios/{id}")
    AnuncioResponse buscar(@Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt,
                           @Parameter(description = "Id do anúncio.", example = "7a1c9e2b-4d3f-4b8a-9e6d-2c5f8a1b3d4e")
                           @PathVariable UUID id);

    @Operation(summary = "Editar anúncio",
            description = "Só o autor pode editar. Substitui todos os campos (envie o anúncio completo).\n\n" + REGRAS,
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, content = @Content(
                    examples = @ExampleObject(name = "edicao", value = CORPO_EXEMPLO))))
    @ApiResponse(responseCode = "200", description = "Anúncio atualizado.", content = @Content(
            schema = @Schema(implementation = AnuncioResponse.class),
            examples = @ExampleObject(name = "atualizado", value = ANUNCIO_EXEMPLO)))
    @ApiResponse(responseCode = "400", description = "Algum campo viola as regras, ou o JSON é malformado.",
            content = @Content(mediaType = ExemplosDeErro.PROBLEM_JSON,
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(name = "categoriaInvalida", value = ExemplosDeErro.CATEGORIA_INVALIDA)))
    @ApiResponse(responseCode = "401", ref = "#/components/responses/NaoAutenticado")
    @ApiResponse(responseCode = "403", description = "O anúncio pertence a outro usuário.", content = @Content(
            mediaType = ExemplosDeErro.PROBLEM_JSON, schema = @Schema(implementation = ProblemDetail.class),
            examples = @ExampleObject(name = "acessoNegado", value = ExemplosDeErro.ACESSO_NEGADO_ANUNCIO)))
    @ApiResponse(responseCode = "404", ref = "#/components/responses/AnuncioNaoEncontrado")
    @PutMapping("/anuncios/{id}")
    AnuncioResponse editar(@Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt,
                           @Parameter(description = "Id do anúncio.", example = "7a1c9e2b-4d3f-4b8a-9e6d-2c5f8a1b3d4e")
                           @PathVariable UUID id,
                           @RequestBody AnuncioRequest request);

    @Operation(summary = "Excluir anúncio",
            description = "O autor exclui o próprio anúncio. Um usuário `ADMIN` pode excluir qualquer anúncio (moderação). "
                    + "A exclusão é definitiva.")
    @ApiResponse(responseCode = "204", description = "Anúncio excluído. Sem corpo na resposta.")
    @ApiResponse(responseCode = "401", ref = "#/components/responses/NaoAutenticado")
    @ApiResponse(responseCode = "403", description = "O anúncio pertence a outro usuário e o solicitante não é ADMIN.",
            content = @Content(mediaType = ExemplosDeErro.PROBLEM_JSON,
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(name = "acessoNegado", value = ExemplosDeErro.ACESSO_NEGADO_ANUNCIO)))
    @ApiResponse(responseCode = "404", ref = "#/components/responses/AnuncioNaoEncontrado")
    @DeleteMapping("/anuncios/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void excluir(@Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt,
                 @Parameter(description = "Id do anúncio.", example = "7a1c9e2b-4d3f-4b8a-9e6d-2c5f8a1b3d4e")
                 @PathVariable UUID id);

    String ANUNCIO_INATIVO_EXEMPLO = """
            {"id": "7a1c9e2b-4d3f-4b8a-9e6d-2c5f8a1b3d4e", "tipo": "OFERTA", "titulo": "Eletricista residencial",
             "descricao": "Instalação de chuveiros, tomadas e quadros de luz.", "preco": 150.00,
             "categoria": {"id": "0b9d3c4e-6f1a-4c2b-8d7e-5a4b3c2d1e0f", "slug": "eletricista", "nome": "Eletricista"},
             "cidade": "Salvador", "uf": "BA", "ativo": false, "autorId": "3f2b6c1e-8a4d-4e0b-9c7a-1d2e3f4a5b6c",
             "criadoEm": "2026-11-16T21:30:00.123456Z", "atualizadoEm": "2026-11-18T14:05:00.654321Z"}""";

    @Operation(summary = "Ativar anúncio",
            description = """
                    Só o autor pode ativar. O anúncio volta a aparecer na listagem pública e no detalhe para
                    qualquer pessoa. Ativar um anúncio que já está ativo não é erro (nada muda).""")
    @ApiResponse(responseCode = "200", description = "Anúncio ativo.", content = @Content(
            schema = @Schema(implementation = AnuncioResponse.class),
            examples = @ExampleObject(name = "ativo", value = ANUNCIO_EXEMPLO)))
    @ApiResponse(responseCode = "401", ref = "#/components/responses/NaoAutenticado")
    @ApiResponse(responseCode = "403", description = "O anúncio pertence a outro usuário.", content = @Content(
            mediaType = ExemplosDeErro.PROBLEM_JSON, schema = @Schema(implementation = ProblemDetail.class),
            examples = @ExampleObject(name = "acessoNegado", value = ExemplosDeErro.ACESSO_NEGADO_ANUNCIO)))
    @ApiResponse(responseCode = "404", ref = "#/components/responses/AnuncioNaoEncontrado")
    @PatchMapping("/anuncios/{id}/ativar")
    AnuncioResponse ativar(@Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt,
                           @Parameter(description = "Id do anúncio.", example = "7a1c9e2b-4d3f-4b8a-9e6d-2c5f8a1b3d4e")
                           @PathVariable UUID id);

    @Operation(summary = "Desativar anúncio",
            description = """
                    Só o autor pode desativar. O anúncio some da listagem pública e o detalhe passa a responder
                    404 para quem não é o autor; ele continua em `GET /usuarios/me/anuncios`. Desativar um
                    anúncio que já está inativo não é erro (nada muda).""")
    @ApiResponse(responseCode = "200", description = "Anúncio inativo.", content = @Content(
            schema = @Schema(implementation = AnuncioResponse.class),
            examples = @ExampleObject(name = "inativo", value = ANUNCIO_INATIVO_EXEMPLO)))
    @ApiResponse(responseCode = "401", ref = "#/components/responses/NaoAutenticado")
    @ApiResponse(responseCode = "403", description = "O anúncio pertence a outro usuário.", content = @Content(
            mediaType = ExemplosDeErro.PROBLEM_JSON, schema = @Schema(implementation = ProblemDetail.class),
            examples = @ExampleObject(name = "acessoNegado", value = ExemplosDeErro.ACESSO_NEGADO_ANUNCIO)))
    @ApiResponse(responseCode = "404", ref = "#/components/responses/AnuncioNaoEncontrado")
    @PatchMapping("/anuncios/{id}/desativar")
    AnuncioResponse desativar(@Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt,
                              @Parameter(description = "Id do anúncio.", example = "7a1c9e2b-4d3f-4b8a-9e6d-2c5f8a1b3d4e")
                              @PathVariable UUID id);

    @Operation(summary = "Listar meus anúncios",
            description = "Anúncios do usuário do token, **incluindo os inativos**, do mais recente para o mais antigo.")
    @ApiResponse(responseCode = "200", description = "Página com os anúncios do usuário.", content = @Content(
            schema = @Schema(implementation = PaginaAnunciosResponse.class),
            examples = @ExampleObject(name = "pagina", value = PAGINA_EXEMPLO)))
    @ApiResponse(responseCode = "400", description = "Paginação inválida.", content = @Content(
            mediaType = ExemplosDeErro.PROBLEM_JSON, schema = @Schema(implementation = ProblemDetail.class),
            examples = @ExampleObject(name = "tamanhoInvalido", value = ExemplosDeErro.TAMANHO_PAGINA_INVALIDO)))
    @ApiResponse(responseCode = "401", ref = "#/components/responses/NaoAutenticado")
    @GetMapping("/usuarios/me/anuncios")
    PaginaAnunciosResponse listarMeus(
            @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt,
            @Parameter(description = "Página, começando em 0. Padrão: 0.", example = "0")
            @RequestParam(required = false) Integer pagina,
            @Parameter(description = "Itens por página, de 1 a 50. Padrão: 20.", example = "20")
            @RequestParam(required = false) Integer tamanho);
}
