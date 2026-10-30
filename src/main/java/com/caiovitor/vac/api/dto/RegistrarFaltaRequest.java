package com.caiovitor.vac.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Corpo do {@code POST /api/disciplinas/{id}/faltas}. A data vem no formato ISO ({@code "2026-10-20"}).
 *
 * <p>Valida apenas presença e formato; a regra de não aceitar data futura fica no domínio.
 * A {@code observacao} é opcional.
 */
public record RegistrarFaltaRequest(
    @NotNull LocalDate data,
    @NotNull @Digits(integer = 4, fraction = 2) BigDecimal horas,
    @Size(max = 255) String observacao
) {}
