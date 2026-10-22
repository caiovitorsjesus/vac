package com.caiovitor.vac.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.caiovitor.vac.domain.exception.DominioInvalidoException;

public class AvaliacaoTest {
    private static final String NOME = "Prova Final";
    private static final BigDecimal PESO = new BigDecimal("30");
    private static final BigDecimal NOTA = new BigDecimal("8");
    private static final BigDecimal NOTA_MAXIMA = new BigDecimal("10");

    @Test
    void deveCriarAvaliacaoValida() {
        Avaliacao avaliacao = new Avaliacao(NOME, PESO, NOTA, NOTA_MAXIMA);

        assertEquals(NOME, avaliacao.getNome());
        assertEquals(0, PESO.compareTo(avaliacao.getPesoPercentual()));
        assertEquals(0, NOTA.compareTo(avaliacao.getNota()));
        assertFalse(avaliacao.isPendente());
    }

    @Test
    void deveCriarAvaliacaoPendente() {
        Avaliacao avaliacao = new Avaliacao(NOME, PESO, null, NOTA_MAXIMA);

        assertTrue(avaliacao.isPendente());
    }

    @Test
    void deveRejeitarNomeNulo() {
        assertThrows(DominioInvalidoException.class,
                () -> new Avaliacao(null, PESO, NOTA, NOTA_MAXIMA));
    }

    @Test
    void deveRejeitarNomeEmBranco() {
        assertThrows(DominioInvalidoException.class,
                () -> new Avaliacao("   ", PESO, NOTA, NOTA_MAXIMA));
    }

    @Test
    void deveRejeitarPesoZero() {
        assertThrows(DominioInvalidoException.class,
                () -> new Avaliacao(NOME, BigDecimal.ZERO, NOTA, NOTA_MAXIMA));
    }

    @Test
    void deveRejeitarPesoNegativo() {
        assertThrows(DominioInvalidoException.class,
                () -> new Avaliacao(NOME, new BigDecimal("-10"), NOTA, NOTA_MAXIMA));
    }

    @Test
    void deveRejeitarPesoAcimaDeCem() {
        assertThrows(DominioInvalidoException.class,
                () -> new Avaliacao(NOME, new BigDecimal("100.01"), NOTA, NOTA_MAXIMA));
    }

    @Test
    void deveAceitarPesoExatamenteCem() {
        Avaliacao avaliacao = new Avaliacao(NOME, new BigDecimal("100"), NOTA, NOTA_MAXIMA);

        assertEquals(0, new BigDecimal("100").compareTo(avaliacao.getPesoPercentual()));
    }

    @Test
    void deveRejeitarNotaNegativa() {
        assertThrows(DominioInvalidoException.class,
                () -> new Avaliacao(NOME, PESO, new BigDecimal("-0.01"), NOTA_MAXIMA));
    }

    @Test
    void deveAceitarNotaZeroSemFicarPendente() {
        Avaliacao avaliacao = new Avaliacao(NOME, PESO, BigDecimal.ZERO, NOTA_MAXIMA);

        assertEquals(0, BigDecimal.ZERO.compareTo(avaliacao.getNota()));
        assertFalse(avaliacao.isPendente());
    }

    @Test
    void deveRejeitarNotaAcimaDaMaxima() {
        assertThrows(DominioInvalidoException.class,
                () -> new Avaliacao(NOME, PESO, new BigDecimal("10.01"), NOTA_MAXIMA));
    }

    @Test
    void deveAceitarNotaIgualAMaxima() {
        Avaliacao avaliacao = new Avaliacao(NOME, PESO, NOTA_MAXIMA, NOTA_MAXIMA);

        assertEquals(0, NOTA_MAXIMA.compareTo(avaliacao.getNota()));
    }

    @Test
    void deveRejeitarNotaMaximaNula() {
        assertThrows(DominioInvalidoException.class,
                () -> new Avaliacao(NOME, PESO, NOTA, null));
    }

    @Test
    void deveRejeitarNotaMaximaZero() {
        assertThrows(DominioInvalidoException.class,
                () -> new Avaliacao(NOME, PESO, NOTA, BigDecimal.ZERO));
    }
}
