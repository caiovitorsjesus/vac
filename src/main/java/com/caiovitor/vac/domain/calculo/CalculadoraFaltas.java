package com.caiovitor.vac.domain.calculo;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.List;

import com.caiovitor.vac.domain.model.Disciplina;
import com.caiovitor.vac.domain.model.RegistroFalta;

/**
 * Calcula a situação de frequência de uma disciplina a partir das faltas registradas.
 *
 * <pre>
 * limiteHoras         = cargaHorariaTotal × percentualMaxFaltas / 100
 * horasRestantes      = max(limiteHoras − horasFaltadas, 0)
 * horasExcedentes     = max(horasFaltadas − limiteHoras, 0)
 * percentualConsumido = horasFaltadas × 100 / limiteHoras
 * </pre>
 *
 * <p>O status segue as faixas de {@link StatusFaltas}. Atingir exatamente o limite ainda é
 * {@link StatusFaltas#CRITICO}; a reprovação só ocorre quando ele é ultrapassado.
 *
 * <p>Sem estado: a mesma entrada sempre gera o mesmo resultado.
 */
public class CalculadoraFaltas {
    private static final BigDecimal CEM = new BigDecimal(100);
    private static final BigDecimal LIMIAR_ATENCAO = new BigDecimal(70);
    private static final BigDecimal LIMIAR_CRITICO = new BigDecimal(90);

    /**
     * Calcula o limite, a margem, o percentual consumido e o status de frequência.
     *
     * <p>O status é decidido com o percentual exato; só o valor devolvido é arredondado
     * (2 casas, {@code HALF_UP}). Assim, 69,996% é {@code SEGURO}, embora exibido como 70,00%.
     *
     * @param disciplina      a disciplina, que já garante limite maior que zero
     * @param registrosFaltas as faltas registradas; lista vazia significa nenhuma falta
     * @return o resultado do cálculo
     * @throws NullPointerException se algum parâmetro for nulo (erro de quem chamou, não do usuário)
     */
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

        // do mais grave para o mais leve: reprovação compara horas (exato), não percentual
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
