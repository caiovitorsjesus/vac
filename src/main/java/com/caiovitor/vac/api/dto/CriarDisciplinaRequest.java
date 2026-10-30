package com.caiovitor.vac.api.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Corpo do {@code POST /api/disciplinas}.
 *
 * <p>Valida apenas presença e formato, incluindo os limites das colunas do banco (100 caracteres,
 * 2 casas decimais), para evitar erro ou arredondamento silencioso. As regras de negócio
 * (carga maior que zero, percentual até 100) são validadas pelo domínio.
 */
public record CriarDisciplinaRequest(
    @NotBlank @Size(max = 100) String nome,
    @NotNull @Digits(integer = 4, fraction = 2) BigDecimal cargaHorariaTotal,
    @NotNull @Digits(integer = 4, fraction = 2) BigDecimal percentualMaximoFaltas,
    @NotNull @Digits(integer = 4, fraction = 2) BigDecimal mediaMinima,
    @NotNull @Digits(integer = 4, fraction = 2) BigDecimal notaMaxima
) {}
