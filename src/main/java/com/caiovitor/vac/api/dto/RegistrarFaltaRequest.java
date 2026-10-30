package com.caiovitor.vac.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegistrarFaltaRequest(
    @NotNull LocalDate data,
    @NotNull @Digits(integer = 4, fraction = 2) BigDecimal horas,
    @Size(max = 255) String observacao
) {}
