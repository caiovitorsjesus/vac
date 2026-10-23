package com.caiovitor.vac.domain.calculo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.caiovitor.vac.domain.exception.ConfiguracaoIncompletaException;
import com.caiovitor.vac.domain.model.Avaliacao;
import com.caiovitor.vac.domain.model.Disciplina;

class CalculadoraNotasTest {

    // média mínima 7, nota máxima 10
    private static final Disciplina DISCIPLINA = new Disciplina("Cálculo I",
            new BigDecimal("60"), new BigDecimal("25"), new BigDecimal("7"), new BigDecimal("10"));

    private final CalculadoraNotas calculadora = new CalculadoraNotas();

    @Test
    void deveProjetarMediaMinimaQuandoNadaFoiCorrigido() {
        ResultadoNotas resultado = calculadora.calcular(DISCIPLINA,
                List.of(pendente("40"), pendente("60")));

        assertValor("0", resultado.pontosGarantidos());
        assertValor("0", resultado.pesoConcluido());
        assertValor("100", resultado.pesoRestante());
        assertEquals(new BigDecimal("7.00"), resultado.notaNecessaria());
        assertTrue(resultado.viavel());
    }

    @Test
    void deveCalcularExemploDoGuia() {
        // P1 40% nota 5; P2 60% pendente: precisa de 8,333... na P2
        ResultadoNotas resultado = calculadora.calcular(DISCIPLINA,
                List.of(corrigida("40", "5"), pendente("60")));

        assertValor("2", resultado.pontosGarantidos());
        assertValor("40", resultado.pesoConcluido());
        assertValor("60", resultado.pesoRestante());
        assertEquals(new BigDecimal("8.34"), resultado.notaNecessaria());
        assertTrue(resultado.viavel());
    }

    @Test
    void deveInformarReprovacaoQuandoTudoFoiCorrigidoAbaixoDaMedia() {
        // 5 × 40% + 8 × 60% = 6,8
        ResultadoNotas resultado = calculadora.calcular(DISCIPLINA,
                List.of(corrigida("40", "5"), corrigida("60", "8")));

        assertValor("6.8", resultado.pontosGarantidos());
        assertValor("0", resultado.pesoRestante());
        assertNull(resultado.notaNecessaria());
        assertFalse(resultado.viavel());
    }

    @Test
    void deveAprovarQuandoTudoFoiCorrigidoComExatamenteAMedia() {
        // 10 × 40% + 5 × 60% = 7
        ResultadoNotas resultado = calculadora.calcular(DISCIPLINA,
                List.of(corrigida("40", "10"), corrigida("60", "5")));

        assertValor("7", resultado.pontosGarantidos());
        assertNull(resultado.notaNecessaria());
        assertTrue(resultado.viavel());
    }

    @Test
    void deveMarcarComoInviavelQuandoNotaNecessariaPassaDaMaxima() {
        // precisa de 7 / 0,6 = 11,666...
        ResultadoNotas resultado = calculadora.calcular(DISCIPLINA,
                List.of(corrigida("40", "0"), pendente("60")));

        assertEquals(new BigDecimal("11.67"), resultado.notaNecessaria());
        assertFalse(resultado.viavel());
    }

    @Test
    void deveSerViavelQuandoNotaNecessariaEExatamenteAMaxima() {
        // 2,5 × 40% = 1; precisa de (7 - 1) / 0,6 = 10
        ResultadoNotas resultado = calculadora.calcular(DISCIPLINA,
                List.of(corrigida("40", "2.5"), pendente("60")));

        assertEquals(new BigDecimal("10.00"), resultado.notaNecessaria());
        assertTrue(resultado.viavel());
    }

    @Test
    void deveExibirZeroQuandoAMetaJaEstaGarantida() {
        // 10 × 90% = 9 pontos, já acima da média 7
        ResultadoNotas resultado = calculadora.calcular(DISCIPLINA,
                List.of(corrigida("90", "10"), pendente("10")));

        assertEquals(new BigDecimal("0.00"), resultado.notaNecessaria());
        assertTrue(resultado.viavel());
    }

    @Test
    void deveManterPontosGarantidosExatosSemArredondar() {
        // 9,99 × 50% = 4,995: arredondar para 5,00 exibiria um valor que o aluno não tem
        ResultadoNotas resultado = calculadora.calcular(DISCIPLINA,
                List.of(corrigida("50", "9.99"), pendente("50")));

        assertValor("4.995", resultado.pontosGarantidos());
    }

    @Test
    void deveAtenderSistemaPorUnidadesConvertidoEmPesos() {
        // Duas unidades, cada uma com duas provas de 5 pontos; média final = (U1 + U2) / 2.
        // Cada prova vale 25% da final e a nota vai para a escala 0-10 (3 de 5 = 6).
        // U1: 3/5 e 4/5 -> notas 6 e 8 -> U1 = 7; U2 ainda pendente.
        ResultadoNotas resultado = calculadora.calcular(DISCIPLINA, List.of(
                corrigida("25", "6"), corrigida("25", "8"),
                pendente("25"), pendente("25")));

        assertValor("3.5", resultado.pontosGarantidos());
        assertEquals(new BigDecimal("7.00"), resultado.notaNecessaria());
        assertTrue(resultado.viavel());
    }

    @Test
    void deveRejeitarPesosSomandoMenosDeCem() {
        ConfiguracaoIncompletaException excecao = assertThrows(ConfiguracaoIncompletaException.class,
                () -> calculadora.calcular(DISCIPLINA, List.of(corrigida("40", "5"), pendente("50"))));

        assertEquals("Os pesos somam 90%. Devem somar 100%.", excecao.getMessage());
    }

    @Test
    void deveRejeitarPesosSomandoMaisDeCem() {
        assertThrows(ConfiguracaoIncompletaException.class,
                () -> calculadora.calcular(DISCIPLINA, List.of(corrigida("50", "5"), pendente("60"))));
    }

    @Test
    void deveRejeitarListaVazia() {
        assertThrows(ConfiguracaoIncompletaException.class,
                () -> calculadora.calcular(DISCIPLINA, List.of()));
    }

    @Test
    void deveRejeitarDisciplinaNula() {
        assertThrows(NullPointerException.class,
                () -> calculadora.calcular(null, List.of(pendente("100"))));
    }

    @Test
    void deveRejeitarListaDeAvaliacoesNula() {
        assertThrows(NullPointerException.class, () -> calculadora.calcular(DISCIPLINA, null));
    }

    private static Avaliacao corrigida(String peso, String nota) {
        return new Avaliacao("Prova", new BigDecimal(peso), new BigDecimal(nota), DISCIPLINA.getNotaMax());
    }

    private static Avaliacao pendente(String peso) {
        return new Avaliacao("Prova", new BigDecimal(peso), null, DISCIPLINA.getNotaMax());
    }

    private static void assertValor(String esperado, BigDecimal atual) {
        assertEquals(0, new BigDecimal(esperado).compareTo(atual),
                () -> "esperado " + esperado + " mas foi " + atual);
    }
}
