package com.caiovitor.vac.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.caiovitor.vac.domain.exception.DominioInvalidoException;

/**
 * Uma ausência registrada, medida em horas.
 *
 * <p>A unidade é hora, e não dia, porque encontros podem ter durações diferentes. O registro
 * não sabe a qual disciplina pertence: essa ligação é feita por quem o usa (calculadora ou persistência).
 */
public class RegistroFalta {
    private final LocalDate data;
    private final BigDecimal horas;
    private final String observacao;

    /**
     * Cria um registro de falta validado.
     *
     * @param data       dia da ausência; hoje é permitido, datas futuras não
     * @param horas      horas de ausência, maiores que zero (aceita frações, como 1,5)
     * @param observacao texto opcional; nulo ou em branco é guardado como {@code null}, e o restante sem espaços nas pontas
     * @throws DominioInvalidoException se a data for nula ou futura, ou as horas não forem positivas
     */
    public RegistroFalta(LocalDate data, BigDecimal horas, String observacao) {
        validar(data, horas);
        this.data = data;
        this.horas = horas;
        this.observacao = (observacao == null || observacao.isBlank()) ? null : observacao.strip();
    }

    public LocalDate getData() {
        return data;
    }

    public BigDecimal getHoras() {
        return horas;
    }

    public String getObservacao() {
        return observacao;
    }

    private void validar(LocalDate data, BigDecimal horas) {
        if (data == null) {
            throw new DominioInvalidoException("A data da falta não pode ser nula.");
        }
        if (data.isAfter(LocalDate.now())) {
            throw new DominioInvalidoException("A data da falta não pode ser futura.");
        }
        if (horas == null || horas.compareTo(BigDecimal.ZERO) <= 0) {
            throw new DominioInvalidoException("As horas da falta devem ser maiores que zero.");
        }
    }
}

