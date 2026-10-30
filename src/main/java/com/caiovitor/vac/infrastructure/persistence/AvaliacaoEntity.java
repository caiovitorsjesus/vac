package com.caiovitor.vac.infrastructure.persistence;

import java.math.BigDecimal;

import com.caiovitor.vac.domain.model.Avaliacao;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Representação da tabela {@code avaliacao}.
 *
 * <p>Cada avaliação pertence a uma disciplina. Uma {@code nota} nula significa que a
 * avaliação ainda está pendente. Depois de criada, apenas a nota pode mudar, por meio de
 * {@link #registrarNota(BigDecimal)}; nome e peso são definitivos.
 *
 * <p>As regras de validação ficam em {@link Avaliacao}: esta classe só recebe dados que
 * já passaram pelo domínio.
 */
@Entity
@Table(name = "avaliacao")
public class AvaliacaoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "disciplina_id", nullable = false)
    private DisciplinaEntity disciplina;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, precision = 6, scale = 2)
    private BigDecimal pesoPercentual;

    @Column(precision = 6, scale = 2)
    private BigDecimal nota;

    protected AvaliacaoEntity() {}

    private AvaliacaoEntity(DisciplinaEntity disciplina, String nome, BigDecimal pesoPercentual, BigDecimal nota) {
        this.disciplina = disciplina;
        this.nome = nome;
        this.pesoPercentual = pesoPercentual;
        this.nota = nota;
    }

    /**
     * Cria a entidade a partir de uma avaliação de domínio já validada.
     *
     * @param avaliacao  avaliação validada pelo domínio
     * @param disciplina disciplina à qual a avaliação pertence
     * @return entidade pronta para ser salva, ainda sem id
     */
    public static AvaliacaoEntity de(Avaliacao avaliacao, DisciplinaEntity disciplina) {
        return new AvaliacaoEntity(disciplina, avaliacao.getNome(), avaliacao.getPesoPercentual(), avaliacao.getNota());
    }

    /**
     * Converte de volta para o domínio. Os dados passam de novo pela validação de {@link Avaliacao}.
     *
     * @param notaMaxima nota máxima da disciplina, necessária para validar a nota
     * @return a avaliação pronta para ser usada pela calculadora de notas
     */
    public Avaliacao paraDominio(BigDecimal notaMaxima) {
        return new Avaliacao(nome, pesoPercentual, nota, notaMaxima);
    }

    /**
     * Registra ou corrige a nota. Não valida: quem chama deve validar a nota pelo domínio antes.
     *
     * @param nota nova nota da avaliação
     */
    public void registrarNota(BigDecimal nota) {
        this.nota = nota;
    }

    public Long getId() {
        return id;
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
}
