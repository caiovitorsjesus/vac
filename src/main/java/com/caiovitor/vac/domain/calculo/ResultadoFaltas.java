package com.caiovitor.vac.domain.calculo;

import java.math.BigDecimal;

/**
 * Resultado de {@link CalculadoraFaltas}. Só transporta dados; nenhum cálculo acontece aqui.
 *
 * @param horasFaltadas       soma das horas de todas as faltas
 * @param limiteHoras         quanto pode ser faltado no total
 * @param horasRestantes      quanto ainda pode ser faltado; nunca negativo
 * @param horasExcedentes     quanto o limite foi ultrapassado; zero se não foi
 * @param percentualConsumido quanto do limite já foi usado, com 2 casas
 * @param status              a faixa de risco
 */
public record ResultadoFaltas(BigDecimal horasFaltadas, BigDecimal limiteHoras, BigDecimal horasRestantes, BigDecimal horasExcedentes, BigDecimal percentualConsumido, StatusFaltas status) {}
