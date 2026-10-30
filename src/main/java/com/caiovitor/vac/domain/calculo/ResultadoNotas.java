package com.caiovitor.vac.domain.calculo;

import java.math.BigDecimal;

/**
 * Resultado de {@link CalculadoraNotas}. Só transporta dados; nenhum cálculo acontece aqui.
 *
 * @param pontosGarantidos pontos da média final já assegurados pelas avaliações corrigidas
 * @param pesoConcluido    soma dos pesos das avaliações corrigidas
 * @param pesoRestante     soma dos pesos das avaliações pendentes
 * @param notaNecessaria   nota mínima nas pendentes para atingir a média; {@code null} se não houver pendentes
 * @param viavel           se a média ainda é alcançável (ou, sem pendentes, se foi atingida)
 */
public record ResultadoNotas(
    BigDecimal pontosGarantidos,
    BigDecimal pesoConcluido,
    BigDecimal pesoRestante,
    BigDecimal notaNecessaria,
    boolean viavel
) {}
