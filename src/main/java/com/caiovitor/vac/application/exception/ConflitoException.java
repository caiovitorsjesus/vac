package com.caiovitor.vac.application.exception;

/**
 * Indica que uma operação é válida isoladamente, mas conflita com o estado já salvo.
 *
 * <p>Exemplo: uma avaliação com peso 50% é válida, mas não cabe numa disciplina cujas
 * avaliações já somam 60%. A API traduz esta exceção para {@code 409 Conflict}.
 */
public class ConflitoException extends RuntimeException {

    public ConflitoException(String message) {
        super(message);
    }
}
