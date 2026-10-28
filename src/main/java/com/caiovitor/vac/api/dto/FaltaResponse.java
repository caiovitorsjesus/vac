package com.caiovitor.vac.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.caiovitor.vac.infrastructure.persistence.RegistroFaltaEntity;

public record FaltaResponse(
    Long id,
    LocalDate data,
    BigDecimal horas,
    String observacao
) {
    public static FaltaResponse de(RegistroFaltaEntity entity) {
        return new FaltaResponse(
            entity.getId(),
            entity.getData(),
            entity.getHoras(),
            entity.getObservacao()
        );
    }
}
