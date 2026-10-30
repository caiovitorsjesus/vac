package com.caiovitor.vac.infrastructure.persistence;

import java.math.BigDecimal;

import com.caiovitor.vac.domain.model.Disciplina;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Representação da tabela {@code disciplina}.
 *
 * <p>Separada de {@link Disciplina} porque o JPA exige construtor vazio e campos mutáveis, o que
 * desfaria a imutabilidade do domínio. Esta classe só recebe dados que já passaram pelo domínio,
 * e a conversão de volta ({@link #paraDominio()}) os valida de novo.
 */
@Entity
@Table(name = "disciplina")
public class DisciplinaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, precision = 6, scale = 2)
    private BigDecimal cargaHorariaTotal;

    @Column(nullable = false, precision = 6, scale = 2)
    private BigDecimal percentualMaximoFaltas;

    @Column(nullable = false, precision = 6, scale = 2)
    private BigDecimal mediaMinima;

    @Column(nullable = false, precision = 6, scale = 2)
    private BigDecimal notaMaxima;

    /** Usado apenas pelo JPA ao ler do banco. */
    protected DisciplinaEntity(){}

    private DisciplinaEntity(
        String nome,
        BigDecimal cargaHorariaTotal,
        BigDecimal percentualMaximoFaltas,
        BigDecimal mediaMinima,
        BigDecimal notaMaxima
    ) {
        this.nome = nome;
        this.cargaHorariaTotal = cargaHorariaTotal;
        this.percentualMaximoFaltas = percentualMaximoFaltas;
        this.mediaMinima = mediaMinima;
        this.notaMaxima = notaMaxima;
    }
    
    /**
     * Cria a entidade a partir de uma disciplina de domínio já validada.
     *
     * @param disciplina disciplina validada
     * @return entidade pronta para ser salva, ainda sem id
     */
    public static DisciplinaEntity de(Disciplina disciplina){
        return new DisciplinaEntity(
            disciplina.getNome(),
            disciplina.getCargaHorariaTotal(),
            disciplina.getPercentualMaxFaltas(),
            disciplina.getMediaMin(),
            disciplina.getNotaMax()
        );
    }

    /**
     * Converte de volta para o domínio. Os dados passam de novo pela validação de {@link Disciplina}.
     *
     * @return a disciplina pronta para ser usada pelas calculadoras
     */
    public Disciplina paraDominio() {
        return new Disciplina(nome, cargaHorariaTotal, percentualMaximoFaltas, mediaMinima, notaMaxima);
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public BigDecimal getCargaHorariaTotal() {
        return cargaHorariaTotal;
    }

    public BigDecimal getPercentualMaximoFaltas() {
        return percentualMaximoFaltas;
    }

    public BigDecimal getMediaMinima() {
        return mediaMinima;
    }

    public BigDecimal getNotaMaxima() {
        return notaMaxima;
    }
}