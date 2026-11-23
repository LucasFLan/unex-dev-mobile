package com.unex.chamaAi.usuario.infrastructure.web;

import com.unex.chamaAi.config.OpenApiConfig;
import com.unex.chamaAi.shared.infrastructure.web.openapi.ExemplosDeErro;
import com.unex.chamaAi.usuario.infrastructure.web.dto.request.AlterarSenhaRequest;
import com.unex.chamaAi.usuario.infrastructure.web.dto.request.CadastrarUsuarioRequest;
import com.unex.chamaAi.usuario.infrastructure.web.dto.request.EditarPerfilRequest;
import com.unex.chamaAi.usuario.infrastructure.web.dto.response.PerfilUsuarioResponse;
import com.unex.chamaAi.usuario.infrastructure.web.dto.response.UsuarioCadastradoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Tag(name = OpenApiConfig.TAG_USUARIOS)
@RequestMapping("/usuarios")
public interface UsuarioController {

    @Operation(summary = "Cadastrar usuário",
            description = """
                    Cria uma conta com papel `USUARIO`, `verificado = false` e status `PENDENTE`. Rota pública.

                    Regras:
- **nome**: obrigatório, até 150 caracteres.

- **dataNascimento**: obrigatória, `yyyy-MM-dd`, não pode estar no futuro.

- **cpf**: obrigatório e válido (dígitos verificadores), com ou sem máscara; único.

- **email**: obrigatório, formato válido, até 254 caracteres; único.

- **senha**: 8+ caracteres, com maiúscula, número e caractere especial; até 72 bytes.

- **telefone**: opcional, até 20 caracteres.""",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, content = @Content(
                    examples = @ExampleObject(name = "cadastro", value = """
                            {"nome": "João da Silva", "dataNascimento": "2000-01-31", "cpf": "529.982.247-25",
                             "email": "joao@unex.edu.br", "senha": "Senha123!", "telefone": "71999999999"}"""))))
    @SecurityRequirements
    @ApiResponse(responseCode = "201", description = "Usuário criado.", content = @Content(
            schema = @Schema(implementation = UsuarioCadastradoResponse.class),
            examples = @ExampleObject(name = "criado", value = """
                    {"id": "3f2b6c1e-8a4d-4e0b-9c7a-1d2e3f4a5b6c", "nome": "João da Silva",
                     "email": "joao@unex.edu.br"}""")))
    @ApiResponse(responseCode = "400", description = "Algum campo viola as regras acima, ou o JSON é malformado. "
            + "O `detail` diz qual regra falhou.", content = @Content(
            mediaType = ExemplosDeErro.PROBLEM_JSON, schema = @Schema(implementation = ProblemDetail.class),
            examples = {
                    @ExampleObject(name = "cpfInvalido", summary = "CPF inválido", value = ExemplosDeErro.CPF_INVALIDO),
                    @ExampleObject(name = "senhaFraca", summary = "Senha fora da política", value = ExemplosDeErro.SENHA_FRACA),
                    @ExampleObject(name = "corpoInvalido", summary = "JSON malformado ou data em formato errado",
                            value = ExemplosDeErro.CORPO_INVALIDO)}))
    @ApiResponse(responseCode = "409", description = "CPF ou email já pertencem a outra conta.", content = @Content(
            mediaType = ExemplosDeErro.PROBLEM_JSON, schema = @Schema(implementation = ProblemDetail.class),
            examples = {
                    @ExampleObject(name = "cpfDuplicado", summary = "CPF já cadastrado", value = ExemplosDeErro.CPF_JA_CADASTRADO),
                    @ExampleObject(name = "emailDuplicado", summary = "Email já cadastrado", value = ExemplosDeErro.EMAIL_JA_CADASTRADO)}))
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    UsuarioCadastradoResponse cadastrar(@RequestBody CadastrarUsuarioRequest request);

    @Operation(summary = "Ver meu perfil",
            description = "Retorna os dados do usuário dono do token. A senha nunca é retornada. "
                    + "O CPF vem só com dígitos.")
    @ApiResponse(responseCode = "200", description = "Perfil do usuário logado.", content = @Content(
            schema = @Schema(implementation = PerfilUsuarioResponse.class),
            examples = @ExampleObject(name = "perfil", value = """
                    {"id": "3f2b6c1e-8a4d-4e0b-9c7a-1d2e3f4a5b6c", "nome": "João da Silva",
                     "dataNascimento": "2000-01-31", "cpf": "52998224725", "email": "joao@unex.edu.br",
                     "telefone": "71999999999", "verificado": false, "papel": "USUARIO"}""")))
    @ApiResponse(responseCode = "401", ref = "#/components/responses/NaoAutenticado")
    @ApiResponse(responseCode = "404", ref = "#/components/responses/UsuarioNaoEncontrado")
    @GetMapping("/me")
    PerfilUsuarioResponse visualizarPerfil(@Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt);

    @Operation(summary = "Editar meu perfil",
            description = """
                    Substitui nome, data de nascimento e telefone (todos os campos são enviados; telefone ausente
                    ou `null` remove o telefone). CPF, email, senha e papel não mudam por esta rota.""",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, content = @Content(
                    examples = @ExampleObject(name = "edicao", value = """
                            {"nome": "João da Silva Santos", "dataNascimento": "2000-01-31", "telefone": "71988887777"}"""))))
    @ApiResponse(responseCode = "200", description = "Perfil atualizado.", content = @Content(
            schema = @Schema(implementation = PerfilUsuarioResponse.class),
            examples = @ExampleObject(name = "perfilAtualizado", value = """
                    {"id": "3f2b6c1e-8a4d-4e0b-9c7a-1d2e3f4a5b6c", "nome": "João da Silva Santos",
                     "dataNascimento": "2000-01-31", "cpf": "52998224725", "email": "joao@unex.edu.br",
                     "telefone": "71988887777", "verificado": false, "papel": "USUARIO"}""")))
    @ApiResponse(responseCode = "400", description = "Algum campo é inválido, ou o JSON é malformado.", content = @Content(
            mediaType = ExemplosDeErro.PROBLEM_JSON, schema = @Schema(implementation = ProblemDetail.class),
            examples = {
                    @ExampleObject(name = "nomeObrigatorio", summary = "Nome ausente", value = ExemplosDeErro.NOME_OBRIGATORIO),
                    @ExampleObject(name = "dataFutura", summary = "Data de nascimento no futuro",
                            value = ExemplosDeErro.DATA_NASCIMENTO_FUTURA)}))
    @ApiResponse(responseCode = "401", ref = "#/components/responses/NaoAutenticado")
    @ApiResponse(responseCode = "404", ref = "#/components/responses/UsuarioNaoEncontrado")
    @PutMapping("/me")
    PerfilUsuarioResponse editarPerfil(@Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt,
                                       @RequestBody EditarPerfilRequest request);

    @Operation(summary = "Alterar minha senha",
            description = "Exige a senha atual. A nova senha segue a mesma política do cadastro. "
                    + "Tokens já emitidos continuam válidos até expirarem.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, content = @Content(
                    examples = @ExampleObject(name = "troca", value = """
                            {"senhaAtual": "Senha123!", "novaSenha": "NovaSenha456@"}"""))))
    @ApiResponse(responseCode = "204", description = "Senha alterada. Sem corpo na resposta.")
    @ApiResponse(responseCode = "400", description = "Nova senha fora da política, ou JSON malformado.", content = @Content(
            mediaType = ExemplosDeErro.PROBLEM_JSON, schema = @Schema(implementation = ProblemDetail.class),
            examples = @ExampleObject(name = "novaSenhaFraca", value = ExemplosDeErro.NOVA_SENHA_FRACA)))
    @ApiResponse(responseCode = "401", ref = "#/components/responses/NaoAutenticado")
    @ApiResponse(responseCode = "404", ref = "#/components/responses/UsuarioNaoEncontrado")
    @ApiResponse(responseCode = "422", description = "A senha atual informada não confere.", content = @Content(
            mediaType = ExemplosDeErro.PROBLEM_JSON, schema = @Schema(implementation = ProblemDetail.class),
            examples = @ExampleObject(name = "senhaAtualIncorreta", value = ExemplosDeErro.SENHA_ATUAL_INCORRETA)))
    @PatchMapping("/me/senha")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void alterarSenha(@Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt,
                      @RequestBody AlterarSenhaRequest request);

    @Operation(summary = "Enviar ou trocar minha foto de perfil",
            description = """
                    Recebe a foto em `multipart/form-data` no campo `foto` (JPG, PNG ou WEBP, até 5 MB; o formato é
                    conferido pelos bytes). Substitui a foto anterior e devolve o perfil com `fotoAtualizadaEm`
                    atualizado. Não exige conta validada.""")
    @ApiResponse(responseCode = "200", description = "Foto gravada; perfil atualizado.", content = @Content(
            schema = @Schema(implementation = PerfilUsuarioResponse.class)))
    @ApiResponse(responseCode = "400", description = "Foto ausente, vazia, maior que 5 MB ou fora dos formatos aceitos.",
            content = @Content(mediaType = ExemplosDeErro.PROBLEM_JSON, schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "401", ref = "#/components/responses/NaoAutenticado")
    @ApiResponse(responseCode = "404", ref = "#/components/responses/UsuarioNaoEncontrado")
    @PutMapping(value = "/me/foto", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    PerfilUsuarioResponse alterarFoto(@Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt,
                                      @Parameter(description = "Foto de perfil (JPG, PNG ou WEBP, até 5 MB).")
                                      @RequestPart("foto") MultipartFile foto);

    @Operation(summary = "Remover minha foto de perfil",
            description = "Apaga a foto e devolve o perfil com `fotoAtualizadaEm = null`. Sem foto, não faz nada.")
    @ApiResponse(responseCode = "200", description = "Foto removida; perfil atualizado.", content = @Content(
            schema = @Schema(implementation = PerfilUsuarioResponse.class)))
    @ApiResponse(responseCode = "401", ref = "#/components/responses/NaoAutenticado")
    @ApiResponse(responseCode = "404", ref = "#/components/responses/UsuarioNaoEncontrado")
    @DeleteMapping("/me/foto")
    PerfilUsuarioResponse removerFoto(@Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt);

    @Operation(summary = "Ver a foto de perfil de um usuário",
            description = """
                    Devolve a imagem (`image/jpeg`, `image/png` ou `image/webp`) de qualquer usuário, para mostrar nos
                    anúncios e nas propostas. Exige login. Fica em cache por 5 minutos no navegador; para ver uma
                    troca na hora, acrescente `?v=<fotoAtualizadaEm>` à URL.""")
    @ApiResponse(responseCode = "200", description = "A imagem.", content = @Content(mediaType = "image/*"))
    @ApiResponse(responseCode = "401", ref = "#/components/responses/NaoAutenticado")
    @ApiResponse(responseCode = "404", description = "Usuário não existe ou não tem foto.", content = @Content(
            mediaType = ExemplosDeErro.PROBLEM_JSON, schema = @Schema(implementation = ProblemDetail.class)))
    @GetMapping("/{id}/foto")
    ResponseEntity<byte[]> foto(@PathVariable UUID id);

    @Operation(summary = "Aprovar usuário",
            description = "Altera o status de validação do usuário para `APROVADO`. Restrito a `ADMIN`.")
    @ApiResponse(responseCode = "204", description = "Usuário aprovado.")
    @ApiResponse(responseCode = "401", ref = "#/components/responses/NaoAutenticado")
    @ApiResponse(responseCode = "404", ref = "#/components/responses/UsuarioNaoEncontrado")
    @ApiResponse(responseCode = "409", description = "O usuário já está aprovado.", content = @Content(
            mediaType = ExemplosDeErro.PROBLEM_JSON, schema = @Schema(implementation = ProblemDetail.class)))
    @PatchMapping("/{id}/aprovar")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void aprovar(@PathVariable UUID id);

    @Operation(summary = "Reprovar usuário",
            description = "Altera o status de validação do usuário para `REPROVADO`. Restrito a `ADMIN`.")
    @ApiResponse(responseCode = "204", description = "Usuário reprovado.")
    @ApiResponse(responseCode = "401", ref = "#/components/responses/NaoAutenticado")
    @ApiResponse(responseCode = "404", ref = "#/components/responses/UsuarioNaoEncontrado")
    @ApiResponse(responseCode = "409", description = "O usuário já está reprovado ou aprovado.", content = @Content(
            mediaType = ExemplosDeErro.PROBLEM_JSON, schema = @Schema(implementation = ProblemDetail.class)))
    @PatchMapping("/{id}/reprovar")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void reprovar(@PathVariable UUID id);
}