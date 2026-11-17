package com.unex.chamaAi.usuario.infrastructure.web;

import com.unex.chamaAi.config.OpenApiConfig;
import com.unex.chamaAi.shared.infrastructure.web.openapi.ExemplosDeErro;
import com.unex.chamaAi.usuario.infrastructure.web.dto.request.LoginRequest;
import com.unex.chamaAi.usuario.infrastructure.web.dto.response.TokenAcessoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

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
}
