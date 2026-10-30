package com.caiovitor.vac.domain.calculo;

/**
 * Faixas de risco de frequência, medidas sobre o percentual do limite de faltas já consumido.
 */
public enum StatusFaltas {
    /** Menos de 70% do limite consumido. */
    SEGURO,
    /** De 70% até menos de 90%. */
    ATENCAO,
    /** De 90% até 100% inclusive: atingir exatamente o limite ainda não reprova. */
    CRITICO,
    /** Horas faltadas acima do limite. */
    REPROVADO_POR_FALTA;
}
