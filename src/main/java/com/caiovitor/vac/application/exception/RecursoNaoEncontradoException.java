package com.caiovitor.vac.application.exception;

/**
 * O recurso buscado (disciplina, avaliação) não existe. A API traduz esta exceção para {@code 404 Not Found}.
 *
 * <p>Fica em {@code application}, e não em {@code domain}, porque é um resultado de busca: o domínio não conhece ids.
 */
public class RecursoNaoEncontradoException extends RuntimeException {
    public RecursoNaoEncontradoException(String message){
        super(message);
    }
}
