package com.unex.chamaAi.shared.infrastructure.web;

import com.unex.chamaAi.shared.domain.exceptions.AcessoNegadoException;
import com.unex.chamaAi.shared.domain.exceptions.ConflitoException;
import com.unex.chamaAi.shared.domain.exceptions.DadoInvalidoException;
import com.unex.chamaAi.shared.domain.exceptions.DominioException;
import com.unex.chamaAi.shared.domain.exceptions.NaoAutenticadoException;
import com.unex.chamaAi.shared.domain.exceptions.RecursoNaoEncontradoException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(DadoInvalidoException.class)
    public ProblemDetail dadoInvalido(DadoInvalidoException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ProblemDetail naoEncontrado(RecursoNaoEncontradoException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(NaoAutenticadoException.class)
    public ResponseEntity<ProblemDetail> naoAutenticado(NaoAutenticadoException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .header(HttpHeaders.WWW_AUTHENTICATE, "Bearer")
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, e.getMessage()));
    }

    @ExceptionHandler(AcessoNegadoException.class)
    public ProblemDetail acessoNegado(AcessoNegadoException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, e.getMessage());
    }

    @ExceptionHandler(ConflitoException.class)
    public ProblemDetail conflito(ConflitoException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, e.getMessage());
    }

    @ExceptionHandler(DominioException.class)
    public ProblemDetail regraDeNegocio(DominioException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_CONTENT, e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail erroInesperado(Exception e) {
        logger.error("Erro inesperado ao processar a requisição.", e);
        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno do servidor.");
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers,
                                                                  HttpStatusCode status, WebRequest request) {
        ProblemDetail corpo = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Corpo da requisição inválido.");
        return handleExceptionInternal(ex, corpo, headers, status, request);
    }

    /** Upload sem um dos arquivos obrigatórios (ex.: documento ou selfie). */
    @Override
    protected ResponseEntity<Object> handleMissingServletRequestPart(
            org.springframework.web.multipart.support.MissingServletRequestPartException ex, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {
        String parte = "selfie".equals(ex.getRequestPartName()) ? "a selfie com o documento" : "a foto do documento";
        ProblemDetail corpo = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Envie " + parte + ".");
        return handleExceptionInternal(ex, corpo, headers, HttpStatus.BAD_REQUEST, request);
    }

    /** Upload acima do limite configurado em spring.servlet.multipart. */
    @Override
    protected ResponseEntity<Object> handleMaxUploadSizeExceededException(
            org.springframework.web.multipart.MaxUploadSizeExceededException ex, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {
        ProblemDetail corpo = ProblemDetail.forStatusAndDetail(HttpStatus.PAYLOAD_TOO_LARGE,
                "Arquivo grande demais: cada imagem pode ter no máximo 5 MB.");
        return handleExceptionInternal(ex, corpo, headers, HttpStatus.PAYLOAD_TOO_LARGE, request);
    }

    /** Parâmetro de rota ou query com formato errado (ex.: id que não é UUID). */
    @Override
    protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers,
                                                        HttpStatusCode status, WebRequest request) {
        String parametro = ex.getPropertyName() == null ? "informado" : "'" + ex.getPropertyName() + "'";
        ProblemDetail corpo = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                "Parâmetro " + parametro + " com formato inválido.");
        return handleExceptionInternal(ex, corpo, headers, status, request);
    }
}
