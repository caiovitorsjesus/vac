package com.caiovitor.vac.domain.exception;

/**
 * Os dados estão corretos, mas ainda não formam um todo, por exemplo, pesos das avaliações
 * somando menos de 100%.
 *
 * <p>Diferente de {@link DominioInvalidoException}: nada está errado, falta completar o cadastro.
 * A API traduz esta exceção para {@code 409 Conflict}.
 */
public class ConfiguracaoIncompletaException extends RuntimeException {
    public ConfiguracaoIncompletaException(String message) {
        super(message);
    }

}
