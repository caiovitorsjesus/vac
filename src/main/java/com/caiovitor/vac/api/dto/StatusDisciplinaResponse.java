package com.caiovitor.vac.api.dto;

/**
 * Panorama de uma disciplina: situação de faltas e projeção de notas.
 *
 * <p>Enquanto os pesos das avaliações não somam 100%, a projeção de notas não é possível:
 * {@code notas} vem nulo e {@code avisoNotas} explica o motivo. A parte de faltas é sempre calculada.
 */
public record StatusDisciplinaResponse(
    Long disciplinaId,
    String nome,
    SituacaoFaltasResponse faltas,
    ProjecaoNotasResponse notas,
    String avisoNotas
) {}
