package com.caiovitor.vac.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import com.caiovitor.vac.domain.exception.DominioInvalidoException;

class RegistroFaltaTest {

    private static final BigDecimal HORAS = new BigDecimal("2.5");
    private static final String OBSERVACAO = "Faltou por motivo de saúde";

    @Test
    void deveCriarRegistroFaltaValido() {
        LocalDate ontem = LocalDate.now().minusDays(1);

        RegistroFalta registroFalta = new RegistroFalta(ontem, HORAS, OBSERVACAO);

        assertEquals(ontem, registroFalta.getData());
        assertEquals(0, HORAS.compareTo(registroFalta.getHoras()));
        assertEquals(OBSERVACAO, registroFalta.getObservacao());
    }

    @Test
    void deveAceitarDataDeHoje() {
        LocalDate hoje = LocalDate.now();

        RegistroFalta registroFalta = new RegistroFalta(hoje, HORAS, OBSERVACAO);

        assertEquals(hoje, registroFalta.getData());
    }

    @Test
    void deveRejeitarDataFutura() {
        LocalDate amanha = LocalDate.now().plusDays(1);

        assertThrows(DominioInvalidoException.class,
                () -> new RegistroFalta(amanha, HORAS, OBSERVACAO));
    }

    @Test
    void deveRejeitarDataNula() {
        assertThrows(DominioInvalidoException.class,
                () -> new RegistroFalta(null, HORAS, OBSERVACAO));
    }

    @Test
    void deveRejeitarHorasNulas() {
        LocalDate ontem = LocalDate.now().minusDays(1);

        assertThrows(DominioInvalidoException.class,
                () -> new RegistroFalta(ontem, null, OBSERVACAO));
    }

    @Test
    void deveRejeitarHorasZero() {
        LocalDate ontem = LocalDate.now().minusDays(1);

        assertThrows(DominioInvalidoException.class,
                () -> new RegistroFalta(ontem, BigDecimal.ZERO, OBSERVACAO));
    }

    @Test
    void deveRejeitarHorasNegativas() {
        LocalDate ontem = LocalDate.now().minusDays(1);

        assertThrows(DominioInvalidoException.class,
                () -> new RegistroFalta(ontem, new BigDecimal("-1"), OBSERVACAO));
    }

    @Test
    void deveAceitarObservacaoNula() {
        LocalDate ontem = LocalDate.now().minusDays(1);

        RegistroFalta registroFalta = new RegistroFalta(ontem, HORAS, null);

        assertNull(registroFalta.getObservacao());
    }

    @Test
    void deveNormalizarObservacaoEmBrancoParaNulo() {
        LocalDate ontem = LocalDate.now().minusDays(1);

        RegistroFalta registroFalta = new RegistroFalta(ontem, HORAS, "   ");

        assertNull(registroFalta.getObservacao());
    }

    @Test
    void deveRemoverEspacosDasPontasDaObservacao() {
        LocalDate ontem = LocalDate.now().minusDays(1);

        RegistroFalta registroFalta = new RegistroFalta(ontem, HORAS, "  Consulta médica  ");

        assertEquals("Consulta médica", registroFalta.getObservacao());
    }
}
