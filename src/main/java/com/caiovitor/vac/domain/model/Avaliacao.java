package com.caiovitor.vac.domain.model;

import java.math.BigDecimal;

import com.caiovitor.vac.domain.exception.DominioInvalidoException;

public class Avaliacao {
    private final String nome;
    private final BigDecimal pesoPercentual;
    private final BigDecimal nota;

    public Avaliacao(String nome, BigDecimal pesoPercentual, BigDecimal nota, BigDecimal notaMaxima) {
        validar(nome, pesoPercentual, nota, notaMaxima);
        this.nome = nome.strip();
        this.pesoPercentual = pesoPercentual;
        this.nota = nota;
    }

    public String getNome() {
        return nome;
    }

    public BigDecimal getPesoPercentual() {
        return pesoPercentual;
    }

    public BigDecimal getNota() {
        return nota;
    }
    
    private void validar(String nome, BigDecimal pesoPercentual, BigDecimal nota, BigDecimal notaMaxima) {
        if (nome == null || nome.isBlank()) {
            throw new DominioInvalidoException("O nome da avaliação não pode ser nulo ou vazio.");
        }
        if (pesoPercentual == null || pesoPercentual.compareTo(BigDecimal.ZERO) <= 0 || pesoPercentual.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new DominioInvalidoException("O peso percentual da avaliação deve ser maior que 0 e no máximo 100.");
        }
        if (notaMaxima == null || notaMaxima.compareTo(BigDecimal.ZERO) <= 0) {
            throw new DominioInvalidoException("A nota máxima da avaliação deve ser maior que zero.");
        }
        if (nota != null && (nota.compareTo(BigDecimal.ZERO) < 0 || nota.compareTo(notaMaxima) > 0)) {
            throw new DominioInvalidoException("A nota da avaliação deve ser maior ou igual a zero e no máximo igual à nota máxima.");
        }
    }

    public boolean isPendente() {
        return nota == null;
    }
}