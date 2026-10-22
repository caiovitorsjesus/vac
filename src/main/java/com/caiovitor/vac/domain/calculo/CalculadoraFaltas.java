package com.caiovitor.vac.domain.calculo;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.List;

import com.caiovitor.vac.domain.model.Disciplina;
import com.caiovitor.vac.domain.model.RegistroFalta;

public class CalculadoraFaltas {
    private static final BigDecimal CEM = new BigDecimal(100);
    private static final BigDecimal LIMIAR_ATENCAO = new BigDecimal(70);
    private static final BigDecimal LIMIAR_CRITICO = new BigDecimal(90);

    public ResultadoFaltas calcular(Disciplina disciplina, List<RegistroFalta> registrosFaltas) {
        Objects.requireNonNull(disciplina, "Disciplina não pode ser nula");
        Objects.requireNonNull(registrosFaltas, "Registros de faltas não podem ser nulos");

        BigDecimal limiteHoras = disciplina.getCargaHorariaTotal().multiply(disciplina.getPercentualMaxFaltas().divide(CEM));
        BigDecimal horasFaltadas = registrosFaltas.stream()
                .map(RegistroFalta::getHoras)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal horasRestantes = limiteHoras.subtract(horasFaltadas).max(BigDecimal.ZERO);
        BigDecimal horasExcedentes = horasFaltadas.subtract(limiteHoras).max(BigDecimal.ZERO);
        BigDecimal percentualExato = horasFaltadas.multiply(CEM).divide(limiteHoras, MathContext.DECIMAL64);

        StatusFaltas status;
        if (horasFaltadas.compareTo(limiteHoras) > 0) {
            status = StatusFaltas.REPROVADO_POR_FALTA;
        } else if (percentualExato.compareTo(LIMIAR_CRITICO) >= 0) {
            status = StatusFaltas.CRITICO;
        } else if (percentualExato.compareTo(LIMIAR_ATENCAO) >= 0) {
            status = StatusFaltas.ATENCAO;
        } else {
            status = StatusFaltas.SEGURO;
        }

        BigDecimal percentualConsumido = percentualExato.setScale(2, RoundingMode.HALF_UP);

        return new ResultadoFaltas(horasFaltadas, limiteHoras, horasRestantes, horasExcedentes, percentualConsumido, status);
    }
}
