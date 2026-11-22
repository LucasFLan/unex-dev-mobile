package com.unex.chamaAi.usuario.infrastructure.web;

import com.unex.chamaAi.config.OpenApiConfig;
import com.unex.chamaAi.shared.infrastructure.web.openapi.ExemplosDeErro;
import com.unex.chamaAi.usuario.infrastructure.web.dto.request.LoginRequest;
import com.unex.chamaAi.usuario.infrastructure.web.dto.request.RedefinirSenhaRequest;
import com.unex.chamaAi.usuario.infrastructure.web.dto.request.SolicitarRecuperacaoSenhaRequest;
import com.unex.chamaAi.usuario.infrastructure.web.dto.request.ValidarCodigoRecuperacaoRequest;
import com.unex.chamaAi.usuario.infrastructure.web.dto.response.TokenAcessoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Tag(name = OpenApiConfig.TAG_AUTENTICACAO)
@RequestMapping("/auth")
public interface AuthController {

    @Operation(summary = "Fazer login",
            description = """
                    Troca email e senha por um token de acesso (JWT). Use o `accessToken` no cabeçalho
                    `Authorization: Bearer <accessToken>` das demais rotas.

                    Por segurança, email inexistente, senha errada ou email/senha em formato inválido recebem
                    **a mesma resposta 401**, para não revelar quais emails estão cadastrados.""",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, content = @Content(
                    examples = @ExampleObject(name = "login", value = """
                            {"email": "joao@unex.edu.br", "senha": "Senha123!"}"""))))
    @SecurityRequirements
    @ApiResponse(responseCode = "200", description = "Login realizado.", content = @Content(
            schema = @Schema(implementation = TokenAcessoResponse.class),
            examples = @ExampleObject(name = "token", value = """
                    {"accessToken": "eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJjaGFtYWFpIiwic3ViIjoiM2Y...", \
                    "tokenType": "Bearer", "expiresIn": 3600}""")))
    @ApiResponse(responseCode = "400", description = "Corpo ausente ou JSON malformado.", content = @Content(
            mediaType = ExemplosDeErro.PROBLEM_JSON, schema = @Schema(implementation = ProblemDetail.class),
            examples = @ExampleObject(name = "corpoInvalido", value = """
                    {"title": "Bad Request", "status": 400,
                     "detail": "Corpo da requisição inválido.", "instance": "/auth/login"}""")))
    @ApiResponse(responseCode = "401", description = "Email ou senha inválidos.", content = @Content(
            mediaType = ExemplosDeErro.PROBLEM_JSON, schema = @Schema(implementation = ProblemDetail.class),
            examples = @ExampleObject(name = "credenciaisInvalidas", value = ExemplosDeErro.CREDENCIAIS_INVALIDAS)))
    @PostMapping("/login")
    TokenAcessoResponse login(@RequestBody LoginRequest request);

    @Operation(summary = "Pedir código de recuperação de senha",
            description = """
                    Envia para o email um código de 6 dígitos que vale por 15 minutos. Um código novo anula o
                    anterior; pedidos repetidos em menos de 60 segundos são ignorados (o código anterior continua
                    valendo).

                    Por segurança, a resposta é **sempre 202**, exista ou não uma conta com esse email.""",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, content = @Content(
                    examples = @ExampleObject(name = "pedido", value = """
                            {"email": "joao@unex.edu.br"}"""))))
    @SecurityRequirements
    @ApiResponse(responseCode = "202", description = "Pedido recebido. Se o email estiver cadastrado, o código foi enviado.")
    @PostMapping("/recuperar-senha")
    @ResponseStatus(HttpStatus.ACCEPTED)
    void solicitarRecuperacaoSenha(@RequestBody SolicitarRecuperacaoSenhaRequest request);

    @Operation(summary = "Conferir código de recuperação",
            description = """
                    Confere o código antes de pedir a nova senha. **Não** consome o código: ele continua valendo
                    para `POST /auth/recuperar-senha/redefinir`. Cada erro conta uma tentativa.""",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, content = @Content(
                    examples = @ExampleObject(name = "codigo", value = """
                            {"email": "joao@unex.edu.br", "codigo": "482913"}"""))))
    @SecurityRequirements
    @ApiResponse(responseCode = "204", description = "Código correto.")
    @ApiResponse(responseCode = "400", description = "Código errado, expirado, já usado ou de outro email. "
            + "Depois de 5 erros o código é bloqueado.", content = @Content(
            mediaType = ExemplosDeErro.PROBLEM_JSON, schema = @Schema(implementation = ProblemDetail.class),
            examples = @ExampleObject(name = "codigoInvalido", value = ExemplosDeErro.CODIGO_RECUPERACAO_INVALIDO)))
    @PostMapping("/recuperar-senha/validar")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void validarCodigoRecuperacao(@RequestBody ValidarCodigoRecuperacaoRequest request);

    @Operation(summary = "Redefinir senha com o código",
            description = """
                    Troca a senha usando o código recebido por email e consome o código. A nova senha segue as mesmas
                    regras do cadastro. Depois disso, faça login com a nova senha.""",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, content = @Content(
                    examples = @ExampleObject(name = "redefinir", value = """
                            {"email": "joao@unex.edu.br", "codigo": "482913", "novaSenha": "NovaSenha456@"}"""))))
    @SecurityRequirements
    @ApiResponse(responseCode = "204", description = "Senha redefinida.")
    @ApiResponse(responseCode = "400", description = "Código inválido ou nova senha fora das regras. O `detail` diz o motivo.",
            content = @Content(mediaType = ExemplosDeErro.PROBLEM_JSON,
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = {
                            @ExampleObject(name = "codigoInvalido", value = ExemplosDeErro.CODIGO_RECUPERACAO_INVALIDO),
                            @ExampleObject(name = "senhaFraca", value = ExemplosDeErro.NOVA_SENHA_FRACA)}))
    @PostMapping("/recuperar-senha/redefinir")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void redefinirSenha(@RequestBody RedefinirSenhaRequest request);
}
