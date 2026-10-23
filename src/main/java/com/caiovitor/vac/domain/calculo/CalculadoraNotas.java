package com.caiovitor.vac.domain.calculo;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

import com.caiovitor.vac.domain.exception.ConfiguracaoIncompletaException;
import com.caiovitor.vac.domain.model.Avaliacao;
import com.caiovitor.vac.domain.model.Disciplina;

public class CalculadoraNotas {
    private static final BigDecimal CEM = new BigDecimal(100);

    public ResultadoNotas calcular(Disciplina disciplina, List<Avaliacao> avaliacoes) {
        Objects.requireNonNull(disciplina, "Disciplina não pode ser nula");
        Objects.requireNonNull(avaliacoes, "Avaliações não podem ser nulas");

        BigDecimal pesoTotal = avaliacoes.stream()
                .map(Avaliacao::getPesoPercentual)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (pesoTotal.compareTo(CEM) != 0) {
            throw new ConfiguracaoIncompletaException("Os pesos somam " + pesoTotal + "%. Devem somar 100%.");
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
