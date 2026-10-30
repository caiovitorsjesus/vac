package com.caiovitor.vac.api.dto;

import java.math.BigDecimal;

import com.caiovitor.vac.domain.calculo.ResultadoNotas;

/**
 * Parte de notas do status de uma disciplina. Espelha {@link ResultadoNotas}, mas é o contrato da API.
 *
 * <p>{@code notaNecessaria} é nula quando todas as avaliações já têm nota; nesse caso,
 * {@code viavel} indica se a média mínima foi atingida.
 */
public record ProjecaoNotasResponse(
    BigDecimal pontosGarantidos,
    BigDecimal pesoConcluido,
    BigDecimal pesoRestante,
    BigDecimal notaNecessaria,
    boolean viavel
) {
    public static ProjecaoNotasResponse de(ResultadoNotas resultado) {
        return new ProjecaoNotasResponse(
            resultado.pontosGarantidos(),
            resultado.pesoConcluido(),
            resultado.pesoRestante(),
            resultado.notaNecessaria(),
            resultado.viavel()
        );
    }
}
