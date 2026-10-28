package com.caiovitor.vac.api.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

/**
 * Corpo do {@code PATCH /api/disciplinas/{id}/avaliacoes/{avaliacaoId}/nota}.
 *
 * <p>A nota é obrigatória: o endpoint registra ou corrige notas, mas não devolve a avaliação ao estado pendente.
 */
public record AtualizarNotaRequest(
    @NotNull @Digits(integer = 4, fraction = 2) BigDecimal nota
) {}
