package com.caiovitor.vac.api.dto;

import java.math.BigDecimal;

import com.caiovitor.vac.infrastructure.persistence.DisciplinaEntity;

/**
 * Disciplina devolvida pela API. É o contrato JSON, separado da entidade do banco.
 */
public record DisciplinaResponse(
    Long id,
    String nome,
    BigDecimal cargaHorariaTotal,
    BigDecimal percentualMaximoFaltas,
    BigDecimal mediaMinima,
    BigDecimal notaMaxima
) {
    public static DisciplinaResponse de(DisciplinaEntity entity) {
        return new DisciplinaResponse(
            entity.getId(),
            entity.getNome(),
            entity.getCargaHorariaTotal(),
            entity.getPercentualMaximoFaltas(),
            entity.getMediaMinima(),
            entity.getNotaMaxima()
        );
    }
}
