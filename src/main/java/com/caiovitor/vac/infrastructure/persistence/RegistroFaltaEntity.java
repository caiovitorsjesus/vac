package com.caiovitor.vac.infrastructure.persistence;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.caiovitor.vac.domain.model.RegistroFalta;

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
 * Representação da tabela {@code registro_falta}.
 *
 * <p>Cada falta pertence a uma disciplina pela coluna {@code disciplina_id}. A relação é carregada
 * de forma preguiçosa ({@code LAZY}) para que listar faltas não busque a disciplina de cada uma.
 */
@Entity
@Table(name = "registro_falta")
public class RegistroFaltaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "disciplina_id", nullable = false)
    private DisciplinaEntity disciplina;

    @Column(nullable = false)
    private LocalDate data;

    @Column(nullable = false, precision = 6, scale = 2)
    private BigDecimal horas;

    @Column(length = 255)
    private String observacao;

    /** Usado apenas pelo JPA ao ler do banco. */
    protected RegistroFaltaEntity() {}

    private RegistroFaltaEntity(
        DisciplinaEntity disciplina,
        LocalDate data,
        BigDecimal horas,
        String observacao
    ){
        this.disciplina = disciplina;
        this.data = data;
        this.horas = horas;
        this.observacao = observacao;
    }

    /**
     * Cria a entidade ligando uma falta já validada à sua disciplina.
     *
     * @param falta      falta validada pelo domínio
     * @param disciplina disciplina à qual a falta pertence
     * @return entidade pronta para ser salva, ainda sem id
     */
    public static RegistroFaltaEntity de(RegistroFalta falta, DisciplinaEntity disciplina) {
        return new RegistroFaltaEntity(
            disciplina,
            falta.getData(),
            falta.getHoras(),
            falta.getObservacao()
        );
    }

    /**
     * Converte de volta para o domínio. Os dados passam de novo pela validação de {@link RegistroFalta}.
     *
     * @return a falta pronta para ser usada pela calculadora de faltas
     */
    public RegistroFalta paraDominio() {
        return new RegistroFalta(data, horas, observacao);
    }

    public Long getId() {
        return id;
    }

    public LocalDate getData() {
        return data;
    }

    public BigDecimal getHoras() {
        return horas;
    }

    public String getObservacao() {
        return observacao;
    }
}
