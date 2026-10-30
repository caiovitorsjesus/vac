package com.caiovitor.vac.domain.model;

import java.math.BigDecimal;

import com.caiovitor.vac.domain.exception.DominioInvalidoException;

/**
 * Uma avaliação da disciplina (prova, trabalho...) com seu peso na média final.
 *
 * <p>Uma {@code nota} nula significa que a avaliação ainda está pendente, o que é diferente de nota zero:
 * zero entra na média, pendente não. A regra de que os pesos de uma disciplina somam 100% envolve o
 * conjunto de avaliações, por isso fica em {@link com.caiovitor.vac.domain.calculo.CalculadoraNotas}.
 */
public class Avaliacao {
    private final String nome;
    private final BigDecimal pesoPercentual;
    private final BigDecimal nota;

    /**
     * Cria uma avaliação validada. O nome é guardado sem espaços nas pontas.
     *
     * @param nome           nome da avaliação, obrigatório
     * @param pesoPercentual peso na média final: maior que 0 e no máximo 100
     * @param nota           nota obtida, entre 0 e a nota máxima; {@code null} se ainda pendente
     * @param notaMaxima     nota máxima da disciplina, usada só para validar a nota (não é guardada)
     * @throws DominioInvalidoException se algum valor violar essas regras
     */
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

    /**
     * Indica se a avaliação ainda não tem nota. Concentra aqui a regra "nota nula = pendente".
     *
     * @return {@code true} se não houver nota
     */
    public boolean isPendente() {
        return nota == null;
    }
}