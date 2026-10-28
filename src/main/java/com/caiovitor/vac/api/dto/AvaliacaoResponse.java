package com.caiovitor.vac.api.dto;

import java.math.BigDecimal;

import com.caiovitor.vac.infrastructure.persistence.AvaliacaoEntity;

/**
 * Avaliação devolvida pela API. Uma {@code nota} nula indica avaliação pendente.
 */
public record AvaliacaoResponse(
    Long id,
    String nome,
    BigDecimal pesoPercentual,
    BigDecimal nota
) {
    public static AvaliacaoResponse de(AvaliacaoEntity entity) {
        return new AvaliacaoResponse(entity.getId(), entity.getNome(), entity.getPesoPercentual(), entity.getNota());
    }
}
