package com.caiovitor.vac.domain.calculo;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

import com.caiovitor.vac.domain.exception.ConfiguracaoIncompletaException;
import com.caiovitor.vac.domain.model.Avaliacao;
import com.caiovitor.vac.domain.model.Disciplina;

/**
 * Projeta a nota necessária nas avaliações pendentes para atingir a média mínima (média ponderada).
 *
 * <pre>
 * pontosGarantidos = soma(nota × peso / 100)  das avaliações com nota
 * pesoRestante     = 100 − soma(peso)          das avaliações com nota
 * notaNecessaria   = (mediaMinima − pontosGarantidos) × 100 / pesoRestante
 * </pre>
 *
 * <p>Sistemas de outras instituições (por exemplo, notas por unidade) são atendidos convertendo-os
 * para pesos percentuais. Sem estado: a mesma entrada sempre gera o mesmo resultado.
 */
public class CalculadoraNotas {
    private static final BigDecimal CEM = new BigDecimal(100);

    /**
     * Calcula os pontos garantidos e a nota necessária no que ainda falta.
     *
     * <ul>
     *   <li>Sem peso restante (tudo corrigido): {@code notaNecessaria} é {@code null} e
     *       {@code viavel} indica se a média mínima foi atingida.</li>
     *   <li>A nota necessária é arredondada <b>para cima</b> ({@code CEILING}): ela é um mínimo, e
     *       arredondar para baixo prometeria algo falso (com 8,33 em vez de 8,333..., a média seria 6,998).</li>
     *   <li>Nota necessária negativa (meta já garantida) é exibida como zero.</li>
     *   <li>{@code viavel} é decidido com o valor exato, antes do arredondamento.</li>
     *   <li>Pontos e pesos não são arredondados: são exatos.</li>
     * </ul>
     *
     * @param disciplina a disciplina, com média mínima e nota máxima
     * @param avaliacoes todas as avaliações da disciplina
     * @return a projeção de notas
     * @throws ConfiguracaoIncompletaException se os pesos não somarem exatamente 100% (inclusive lista vazia)
     * @throws NullPointerException            se algum parâmetro for nulo (erro de quem chamou, não do usuário)
     */
    public ResultadoNotas calcular(Disciplina disciplina, List<Avaliacao> avaliacoes) {
        Objects.requireNonNull(disciplina, "Disciplina não pode ser nula");
        Objects.requireNonNull(avaliacoes, "Avaliações não podem ser nulas");

        BigDecimal pesoTotal = avaliacoes.stream()
                .map(Avaliacao::getPesoPercentual)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (pesoTotal.compareTo(CEM) != 0) {
            // pesos vindos do banco têm 2 casas (40.00); na mensagem, "40" é mais legível
            throw new ConfiguracaoIncompletaException(
                    "Os pesos somam " + pesoTotal.stripTrailingZeros().toPlainString() + "%. Devem somar 100%.");
        }
        BigDecimal pontosGarantidos = avaliacoes.stream()
                .filter(avaliacao -> !avaliacao.isPendente())
                .map(avaliacao -> avaliacao.getNota().multiply(avaliacao.getPesoPercentual()).divide(CEM))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal pesoConcluido = avaliacoes.stream()
                .filter(avaliacao -> !avaliacao.isPendente())
                .map(Avaliacao::getPesoPercentual)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal pesoRestante = CEM.subtract(pesoConcluido);
        if (pesoRestante.compareTo(BigDecimal.ZERO) == 0) {
            boolean aprovado = pontosGarantidos.compareTo(disciplina.getMediaMin()) >= 0;
            return new ResultadoNotas(pontosGarantidos, pesoConcluido, pesoRestante, null, aprovado);
        }

        BigDecimal notaExata = disciplina.getMediaMin().subtract(pontosGarantidos)
                .multiply(CEM)
                .divide(pesoRestante, MathContext.DECIMAL64);
        boolean viavel = notaExata.compareTo(disciplina.getNotaMax()) <= 0;
        BigDecimal notaNecessaria = notaExata.max(BigDecimal.ZERO).setScale(2, RoundingMode.CEILING);
        return new ResultadoNotas(pontosGarantidos, pesoConcluido, pesoRestante, notaNecessaria, viavel);
    }
}
