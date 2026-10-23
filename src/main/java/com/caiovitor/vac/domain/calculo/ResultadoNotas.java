package com.caiovitor.vac.domain.calculo;

import java.math.BigDecimal;

public record ResultadoNotas(
    BigDecimal pontosGarantidos,
    BigDecimal pesoConcluido,
    BigDecimal pesoRestante,
    BigDecimal notaNecessaria,
    boolean viavel
) {}
