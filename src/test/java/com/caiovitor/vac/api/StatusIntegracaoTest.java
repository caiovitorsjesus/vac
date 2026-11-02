package com.caiovitor.vac.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

/**
 * Testes de ponta a ponta do status: os dados entram pela API, passam pelo banco e pelas
 * calculadoras, e os números conferidos são os do exemplo do guia.
 */
class StatusIntegracaoTest extends IntegracaoTest {

    @BeforeEach
    void prepararDisciplina() throws Exception {
        criarDisciplina(); // 60 h, 25% de faltas: limite de 15 h; média 7, nota máxima 10
    }

    @Test
    void deveCalcularExemploDoGuia() throws Exception {
        registrarFalta("9");
        criarAvaliacao("{\"nome\": \"P1\", \"pesoPercentual\": 40, \"nota\": 5}");
        criarAvaliacao("{\"nome\": \"P2\", \"pesoPercentual\": 60}");

        mockMvc.perform(get("/api/disciplinas/1/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.disciplinaId").value(1))
                .andExpect(jsonPath("$.nome").value("Cálculo I"))
                .andExpect(jsonPath("$.faltas.horasFaltadas").value(9.0))
                .andExpect(jsonPath("$.faltas.limiteHoras").value(15.0))
                .andExpect(jsonPath("$.faltas.horasRestantes").value(6.0))
                .andExpect(jsonPath("$.faltas.percentualConsumido").value(60.0))
                .andExpect(jsonPath("$.faltas.status").value("SEGURO"))
                .andExpect(jsonPath("$.notas.pontosGarantidos").value(2.0))
                .andExpect(jsonPath("$.notas.pesoRestante").value(60.0))
                .andExpect(jsonPath("$.notas.notaNecessaria").value(8.34))
                .andExpect(jsonPath("$.notas.viavel").value(true))
                .andExpect(jsonPath("$.avisoNotas").doesNotExist());
    }

    @Test
    void deveCalcularDisciplinaSemNadaCadastrado() throws Exception {
        mockMvc.perform(get("/api/disciplinas/1/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.faltas.horasFaltadas").value(0))
                .andExpect(jsonPath("$.faltas.horasRestantes").value(15.0))
                .andExpect(jsonPath("$.faltas.status").value("SEGURO"))
                .andExpect(jsonPath("$.notas").doesNotExist())
                .andExpect(jsonPath("$.avisoNotas").value("Os pesos somam 0%. Devem somar 100%."));
    }

    // pesos lidos do banco têm escala 2: o aviso deve mostrar "40%", não "40.00%"
    @Test
    void deveMostrarFaltasEAvisoQuandoPesosEstaoIncompletos() throws Exception {
        registrarFalta("9");
        criarAvaliacao("{\"nome\": \"P1\", \"pesoPercentual\": 40, \"nota\": 5}");

        mockMvc.perform(get("/api/disciplinas/1/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.faltas.status").value("SEGURO"))
                .andExpect(jsonPath("$.faltas.horasRestantes").value(6.0))
                .andExpect(jsonPath("$.notas").doesNotExist())
                .andExpect(jsonPath("$.avisoNotas").value("Os pesos somam 40%. Devem somar 100%."));
    }

    @Test
    void deveIndicarReprovacaoComTudoCorrigido() throws Exception {
        registrarFalta("16");
        criarAvaliacao("{\"nome\": \"P1\", \"pesoPercentual\": 40, \"nota\": 5}");
        criarAvaliacao("{\"nome\": \"P2\", \"pesoPercentual\": 60, \"nota\": 8}");

        mockMvc.perform(get("/api/disciplinas/1/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.faltas.horasRestantes").value(0))
                .andExpect(jsonPath("$.faltas.horasExcedentes").value(1.0))
                .andExpect(jsonPath("$.faltas.status").value("REPROVADO_POR_FALTA"))
                .andExpect(jsonPath("$.notas.pontosGarantidos").value(6.8))
                .andExpect(jsonPath("$.notas.pesoRestante").value(0.0))
                .andExpect(jsonPath("$.notas.notaNecessaria").doesNotExist())
                .andExpect(jsonPath("$.notas.viavel").value(false));
    }

    @Test
    void deveResponder404ParaDisciplinaInexistente() throws Exception {
        mockMvc.perform(get("/api/disciplinas/999/status"))
                .andExpect(status().isNotFound());
    }

    private void registrarFalta(String horas) throws Exception {
        mockMvc.perform(post("/api/disciplinas/1/faltas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"data\": \"%s\", \"horas\": %s}".formatted(LocalDate.now().minusDays(1), horas)))
                .andExpect(status().isCreated());
    }

    private void criarAvaliacao(String json) throws Exception {
        mockMvc.perform(post("/api/disciplinas/1/avaliacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());
    }
}
