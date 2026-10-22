package com.caiovitor.vac.domain.calculo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.caiovitor.vac.domain.model.Disciplina;
import com.caiovitor.vac.domain.model.RegistroFalta;

class CalculadoraFaltasTest {

    // 60 h com 25% de faltas permitidas: limite de 15 h; 70% = 10,5 h; 90% = 13,5 h
    private static final Disciplina DISCIPLINA = new Disciplina("Cálculo I",
            new BigDecimal("60"), new BigDecimal("25"), new BigDecimal("7"), new BigDecimal("10"));

    private final CalculadoraFaltas calculadora = new CalculadoraFaltas();

    @Test
    void deveCalcularSemFaltas() {
        ResultadoFaltas resultado = calculadora.calcular(DISCIPLINA, faltas());

        assertValor("0", resultado.horasFaltadas());
        assertValor("15", resultado.limiteHoras());
        assertValor("15", resultado.horasRestantes());
        assertValor("0", resultado.horasExcedentes());
        assertValor("0", resultado.percentualConsumido());
        assertEquals(StatusFaltas.SEGURO, resultado.status());
    }

    @Test
    void deveCalcularExemploDoGuia() {
        ResultadoFaltas resultado = calculadora.calcular(DISCIPLINA, faltas("9"));

        assertValor("15", resultado.limiteHoras());
        assertValor("6", resultado.horasRestantes());
        assertValor("60", resultado.percentualConsumido());
        assertEquals(StatusFaltas.SEGURO, resultado.status());
    }

    @Test
    void deveSerSeguroLogoAbaixoDeSetentaPorCento() {
        assertEquals(StatusFaltas.SEGURO, calculadora.calcular(DISCIPLINA, faltas("10.4")).status());
    }

    @Test
    void deveSerAtencaoComExatamenteSetentaPorCento() {
        assertEquals(StatusFaltas.ATENCAO, calculadora.calcular(DISCIPLINA, faltas("10.5")).status());
    }

    @Test
    void deveSerAtencaoLogoAbaixoDeNoventaPorCento() {
        assertEquals(StatusFaltas.ATENCAO, calculadora.calcular(DISCIPLINA, faltas("13.4")).status());
    }

    @Test
    void deveSerCriticoComExatamenteNoventaPorCento() {
        assertEquals(StatusFaltas.CRITICO, calculadora.calcular(DISCIPLINA, faltas("13.5")).status());
    }

    @Test
    void deveSerCriticoAoAtingirExatamenteOLimite() {
        ResultadoFaltas resultado = calculadora.calcular(DISCIPLINA, faltas("15"));

        assertValor("0", resultado.horasRestantes());
        assertValor("0", resultado.horasExcedentes());
        assertValor("100", resultado.percentualConsumido());
        assertEquals(StatusFaltas.CRITICO, resultado.status());
    }

    @Test
    void deveReprovarAoUltrapassarOLimite() {
        ResultadoFaltas resultado = calculadora.calcular(DISCIPLINA, faltas("16"));

        assertValor("0", resultado.horasRestantes());
        assertValor("1", resultado.horasExcedentes());
        assertEquals(StatusFaltas.REPROVADO_POR_FALTA, resultado.status());
    }

    @Test
    void deveSomarVariasFaltasFracionadas() {
        ResultadoFaltas resultado = calculadora.calcular(DISCIPLINA, faltas("1.5", "2.5", "3"));

        assertValor("7", resultado.horasFaltadas());
        assertValor("8", resultado.horasRestantes());
    }

    @Test
    void deveArredondarPercentualComDizimaEmDuasCasas() {
        // 5 / 15 = 33,333...%
        ResultadoFaltas resultado = calculadora.calcular(DISCIPLINA, faltas("5"));

        assertEquals(new BigDecimal("33.33"), resultado.percentualConsumido());
    }

    @Test
    void deveDecidirStatusPeloPercentualExatoENaoPeloArredondado() {
        // 10,4994 / 15 = 69,996%: exibido como 70,00%, mas ainda abaixo de 70%
        ResultadoFaltas resultado = calculadora.calcular(DISCIPLINA, faltas("10.4994"));

        assertEquals(new BigDecimal("70.00"), resultado.percentualConsumido());
        assertEquals(StatusFaltas.SEGURO, resultado.status());
    }

    @Test
    void deveRejeitarDisciplinaNula() {
        assertThrows(NullPointerException.class, () -> calculadora.calcular(null, faltas()));
    }

    @Test
    void deveRejeitarListaDeFaltasNula() {
        assertThrows(NullPointerException.class, () -> calculadora.calcular(DISCIPLINA, null));
    }

    private static List<RegistroFalta> faltas(String... horas) {
        LocalDate ontem = LocalDate.now().minusDays(1);
        return Arrays.stream(horas)
                .map(h -> new RegistroFalta(ontem, new BigDecimal(h), null))
                .toList();
    }

    private static void assertValor(String esperado, BigDecimal atual) {
        assertEquals(0, new BigDecimal(esperado).compareTo(atual),
                () -> "esperado " + esperado + " mas foi " + atual);
    }
}
