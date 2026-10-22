package com.caiovitor.vac.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.caiovitor.vac.domain.exception.DominioInvalidoException;

public class RegistroFalta {
    private final LocalDate data;
    private final BigDecimal horas;
    private final String observacao;

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

