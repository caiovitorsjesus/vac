package com.caiovitor.vac.api.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Corpo do {@code POST /api/disciplinas/{id}/avaliacoes}.
 *
 * <p>Valida apenas presença e formato. As regras (peso entre 0 e 100, nota até a nota máxima)
 * são validadas pelo domínio. A {@code nota} é opcional: sem ela, a avaliação nasce pendente.
 */
public record CriarAvaliacaoRequest(
    @NotBlank @Size(max = 100) String nome,
    @NotNull @Digits(integer = 4, fraction = 2) BigDecimal pesoPercentual,
    @Digits(integer = 4, fraction = 2) BigDecimal nota
) {}
