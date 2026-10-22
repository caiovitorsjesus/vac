package com.caiovitor.vac.domain.calculo;

import java.math.BigDecimal;

public record ResultadoFaltas(BigDecimal horasFaltadas, BigDecimal limiteHoras, BigDecimal horasRestantes, BigDecimal horasExcedentes, BigDecimal percentualConsumido, StatusFaltas status) {}
