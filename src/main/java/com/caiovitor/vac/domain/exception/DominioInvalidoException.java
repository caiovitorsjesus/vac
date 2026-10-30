package com.caiovitor.vac.domain.exception;

/**
 * Um dado viola uma regra de negócio (por exemplo, carga horária zero ou nota acima da máxima).
 *
 * <p>A mensagem é escrita para o usuário final. A API traduz esta exceção para {@code 400 Bad Request}.
 */
public class DominioInvalidoException extends RuntimeException {
    public DominioInvalidoException(String message) {
        super(message);
    }

}
