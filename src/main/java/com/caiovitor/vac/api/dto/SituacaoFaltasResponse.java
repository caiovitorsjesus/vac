package com.caiovitor.vac.api.dto;

import java.math.BigDecimal;

import com.caiovitor.vac.domain.calculo.ResultadoFaltas;
import com.caiovitor.vac.domain.calculo.StatusFaltas;

/**
 * Parte de faltas do status de uma disciplina. Espelha {@link ResultadoFaltas}, mas é o contrato
 * da API: mudanças no domínio não alteram este JSON sem uma decisão explícita.
 */
public record SituacaoFaltasResponse(
    BigDecimal horasFaltadas,
    BigDecimal limiteHoras,
    BigDecimal horasRestantes,
    BigDecimal horasExcedentes,
    BigDecimal percentualConsumido,
    StatusFaltas status
) {
    public static SituacaoFaltasResponse de(ResultadoFaltas resultado) {
        return new SituacaoFaltasResponse(
            resultado.horasFaltadas(),
            resultado.limiteHoras(),
            resultado.horasRestantes(),
            resultado.horasExcedentes(),
            resultado.percentualConsumido(),
            resultado.status()
        );
    }
}
