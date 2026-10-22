package com.caiovitor.vac.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.caiovitor.vac.domain.exception.DominioInvalidoException;

class DisciplinaTest {

    private static final String NOME = "Cálculo I";
    private static final BigDecimal CARGA = new BigDecimal("60");
    private static final BigDecimal PERCENTUAL = new BigDecimal("25");
    private static final BigDecimal MEDIA = new BigDecimal("7.0");
    private static final BigDecimal NOTA_MAXIMA = new BigDecimal("10.0");

    @Test
    void deveCriarDisciplinaValida() {
        Disciplina disciplina = new Disciplina(NOME, CARGA, PERCENTUAL, MEDIA, NOTA_MAXIMA);

        assertEquals(NOME, disciplina.getNome());
        assertEquals(0, CARGA.compareTo(disciplina.getCargaHorariaTotal()));
        assertEquals(0, PERCENTUAL.compareTo(disciplina.getPercentualMaxFaltas()));
        assertEquals(0, MEDIA.compareTo(disciplina.getMediaMin()));
        assertEquals(0, NOTA_MAXIMA.compareTo(disciplina.getNotaMax()));
    }

    @Test
    void deveRemoverEspacosDasPontasDoNome() {
        Disciplina disciplina = new Disciplina("  Cálculo I  ", CARGA, PERCENTUAL, MEDIA, NOTA_MAXIMA);

        assertEquals("Cálculo I", disciplina.getNome());
    }

    @Test
    void deveRejeitarNomeNulo() {
        assertThrows(DominioInvalidoException.class,
                () -> new Disciplina(null, CARGA, PERCENTUAL, MEDIA, NOTA_MAXIMA));
    }

    @Test
    void deveRejeitarNomeEmBranco() {
        assertThrows(DominioInvalidoException.class,
                () -> new Disciplina("   ", CARGA, PERCENTUAL, MEDIA, NOTA_MAXIMA));
    }

    @Test
    void deveRejeitarCargaHorariaZero() {
        assertThrows(DominioInvalidoException.class,
                () -> new Disciplina(NOME, BigDecimal.ZERO, PERCENTUAL, MEDIA, NOTA_MAXIMA));
    }

    @Test
    void deveRejeitarCargaHorariaNegativa() {
        assertThrows(DominioInvalidoException.class,
                () -> new Disciplina(NOME, new BigDecimal("-1"), PERCENTUAL, MEDIA, NOTA_MAXIMA));
    }

    @Test
    void deveRejeitarPercentualZero() {
        assertThrows(DominioInvalidoException.class,
                () -> new Disciplina(NOME, CARGA, BigDecimal.ZERO, MEDIA, NOTA_MAXIMA));
    }

    @Test
    void deveRejeitarPercentualAcimaDeCem() {
        assertThrows(DominioInvalidoException.class,
                () -> new Disciplina(NOME, CARGA, new BigDecimal("100.01"), MEDIA, NOTA_MAXIMA));
    }

    @Test
    void deveAceitarPercentualExatamenteCem() {
        Disciplina disciplina = new Disciplina(NOME, CARGA, new BigDecimal("100"), MEDIA, NOTA_MAXIMA);

        assertEquals(0, new BigDecimal("100").compareTo(disciplina.getPercentualMaxFaltas()));
    }

    @Test
    void deveRejeitarNotaMaximaZero() {
        assertThrows(DominioInvalidoException.class,
                () -> new Disciplina(NOME, CARGA, PERCENTUAL, MEDIA, BigDecimal.ZERO));
    }

    @Test
    void deveRejeitarNotaMaximaNula() {
        assertThrows(DominioInvalidoException.class,
                () -> new Disciplina(NOME, CARGA, PERCENTUAL, MEDIA, null));
    }

    @Test
    void deveRejeitarMediaMinimaMaiorQueNotaMaxima() {
        assertThrows(DominioInvalidoException.class,
                () -> new Disciplina(NOME, CARGA, PERCENTUAL, new BigDecimal("10.5"), NOTA_MAXIMA));
    }

    @Test
    void deveRejeitarMediaMinimaZero() {
        assertThrows(DominioInvalidoException.class,
                () -> new Disciplina(NOME, CARGA, PERCENTUAL, BigDecimal.ZERO, NOTA_MAXIMA));
    }

    @Test
    void deveAceitarMediaMinimaIgualANotaMaxima() {
        Disciplina disciplina = new Disciplina(NOME, CARGA, PERCENTUAL, new BigDecimal("10.0"), NOTA_MAXIMA);

        assertEquals(0, NOTA_MAXIMA.compareTo(disciplina.getMediaMin()));
    }
}
