package com.caiovitor.vac.domain.model;

import java.math.BigDecimal;

import com.caiovitor.vac.domain.exception.DominioInvalidoException;

/**
 * Uma matéria e as regras configuradas pelo próprio estudante: carga horária, limite de faltas,
 * média mínima e nota máxima.
 *
 * <p>Imutável e sempre válida: o construtor rejeita qualquer configuração inválida, então quem
 * recebe uma {@code Disciplina} não precisa validá-la de novo. O limite de faltas nunca é zero,
 * o que garante que as calculadoras não dividem por zero.
 */
public class Disciplina {
    private final String nome;
    private final BigDecimal cargaHorariaTotal;
    private final BigDecimal percentualMaxFaltas;
    private final BigDecimal mediaMin;
    private final BigDecimal notaMax;

    /**
     * Cria uma disciplina validada. O nome é guardado sem espaços nas pontas.
     *
     * @param nome                nome da matéria, obrigatório
     * @param cargaHorariaTotal   carga horária em horas, maior que zero
     * @param percentualMaxFaltas percentual da carga horária que pode ser faltado: maior que 0 e no máximo 100
     * @param mediaMin            média mínima para aprovação: maior que zero e no máximo a nota máxima
     * @param notaMax             nota máxima de uma avaliação, maior que zero
     * @throws DominioInvalidoException se algum valor for nulo ou violar essas regras
     */
    public Disciplina(String nome, BigDecimal cargaHorariaTotal, BigDecimal percentualMaxFaltas, BigDecimal mediaMin, BigDecimal notaMax) {
        validar(nome, cargaHorariaTotal, percentualMaxFaltas, mediaMin, notaMax);
        this.nome = nome.strip();
        this.cargaHorariaTotal = cargaHorariaTotal;
        this.percentualMaxFaltas = percentualMaxFaltas;
        this.mediaMin = mediaMin;
        this.notaMax = notaMax;
    }

    public String getNome() {
        return nome;
    }

    public BigDecimal getCargaHorariaTotal() {
        return cargaHorariaTotal;
    }

    public BigDecimal getPercentualMaxFaltas() {
        return percentualMaxFaltas;
    }

    public BigDecimal getMediaMin() {
        return mediaMin;
    }

    public BigDecimal getNotaMax() {
        return notaMax;
    }

    private void validar(String nome, BigDecimal cargaHorariaTotal, BigDecimal percentualMaxFaltas, BigDecimal mediaMin, BigDecimal notaMax) {
        if (nome == null || nome.isBlank()) {
            throw new DominioInvalidoException("O nome da disciplina não pode ser nulo ou vazio.");
        }
        if (cargaHorariaTotal == null || cargaHorariaTotal.compareTo(BigDecimal.ZERO) <= 0) {
            throw new DominioInvalidoException("A carga horária total deve ser maior que zero.");
        }
        if (percentualMaxFaltas == null || percentualMaxFaltas.compareTo(BigDecimal.ZERO) <= 0 || percentualMaxFaltas.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new DominioInvalidoException("O percentual máximo de faltas deve ser maior que 0 e no maximo 100.");
        }
        if (notaMax == null || notaMax.compareTo(BigDecimal.ZERO) <= 0) {
            throw new DominioInvalidoException("A nota máxima deve ser maior que zero.");
        }
        if (mediaMin == null || mediaMin.compareTo(BigDecimal.ZERO) <= 0 || mediaMin.compareTo(notaMax) > 0) {
            throw new DominioInvalidoException("A média mínima deve ser maior que zero e no maximo igual à nota máxima.");
        }
    }
}    