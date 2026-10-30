package com.caiovitor.vac.api;

import java.util.LinkedHashMap;
import java.util.Map;

import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.caiovitor.vac.application.exception.ConflitoException;
import com.caiovitor.vac.application.exception.RecursoNaoEncontradoException;
import com.caiovitor.vac.domain.exception.ConfiguracaoIncompletaException;
import com.caiovitor.vac.domain.exception.DominioInvalidoException;

/**
 * Traduz exceções em respostas HTTP no formato {@code ProblemDetail} (RFC 9457), para todos os controllers.
 *
 * <table>
 *   <caption>Mapeamento de exceções</caption>
 *   <tr><td>{@link DominioInvalidoException}</td><td>400: dado viola uma regra de negócio</td></tr>
 *   <tr><td>erros de {@code @Valid}</td><td>400: campo ausente ou fora do formato, com a lista em {@code campos}</td></tr>
 *   <tr><td>{@link RecursoNaoEncontradoException}</td><td>404</td></tr>
 *   <tr><td>{@link ConfiguracaoIncompletaException}, {@link ConflitoException}</td><td>409: conflita com o estado salvo</td></tr>
 * </table>
 *
 * <p>Erros do próprio Spring (JSON malformado, método HTTP errado) são tratados pela classe herdada.
 * Erros inesperados viram 500 sem expor stack trace.
 */
@RestControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {
    
    @ExceptionHandler(DominioInvalidoException.class)
    public ProblemDetail tratarDominioInvalido(DominioInvalidoException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ProblemDetail tratarRecursoNaoEncontrado(RecursoNaoEncontradoException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler({ConfiguracaoIncompletaException.class, ConflitoException.class})
    public ProblemDetail tratarConflito(RuntimeException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
    }

    /**
     * Inclui no corpo do erro um mapa {@code campos} com cada campo inválido e sua mensagem;
     * por padrão, o Spring só responderia "Invalid request content".
     */
    @Override
    protected @Nullable ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        Map<String, String> campos = new LinkedHashMap<>();
        for (FieldError erro : ex.getBindingResult().getFieldErrors()) {
            campos.put(erro.getField(), erro.getDefaultMessage());
        }
        
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Dados inválidos.");
        problema.setProperty("campos", campos);
        return ResponseEntity.badRequest().body(problema);
    }
}
