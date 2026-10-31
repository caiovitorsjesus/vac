package com.caiovitor.vac.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

class FaltaIntegracaoTest extends IntegracaoTest {

    // datas relativas a hoje: uma data fixa no futuro deixaria de ser futura com o tempo
    private static final LocalDate HOJE = LocalDate.now();

    @BeforeEach
    void prepararDisciplina() throws Exception {
        criarDisciplina();
    }

    @Test
    void deveRegistrarFalta() throws Exception {
        registrar(1, HOJE.minusDays(1), "2", null)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.data").value(HOJE.minusDays(1).toString()))
                .andExpect(jsonPath("$.horas").value(2))
                .andExpect(jsonPath("$.observacao").doesNotExist());
    }

    @Test
    void deveNormalizarObservacaoPeloDominio() throws Exception {
        registrar(1, HOJE.minusDays(1), "2", "  consulta médica  ")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.observacao").value("consulta médica"));
    }

    @Test
    void deveListarFaltasOrdenadasPorData() throws Exception {
        registrar(1, HOJE.minusDays(1), "2", null);
        registrar(1, HOJE.minusDays(5), "1.5", null);

        mockMvc.perform(get("/api/disciplinas/1/faltas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].data").value(HOJE.minusDays(5).toString()))
                .andExpect(jsonPath("$[1].data").value(HOJE.minusDays(1).toString()));
    }

    @Test
    void deveAceitarFaltaComDataDeHoje() throws Exception {
        registrar(1, HOJE, "2", null)
                .andExpect(status().isCreated());
    }

    @Test
    void deveResponder400ParaDataFutura() throws Exception {
        registrar(1, HOJE.plusDays(1), "2", null)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("A data da falta não pode ser futura."));
    }

    @Test
    void deveResponder400ParaHorasZero() throws Exception {
        registrar(1, HOJE.minusDays(1), "0", null)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("As horas da falta devem ser maiores que zero."));
    }

    @Test
    void deveResponder400SemData() throws Exception {
        mockMvc.perform(post("/api/disciplinas/1/faltas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"horas\": 2}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos.data").exists());
    }

    @Test
    void deveResponder404AoRegistrarEmDisciplinaInexistente() throws Exception {
        registrar(999, HOJE.minusDays(1), "2", null)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Disciplina 999 não encontrada."));
    }

    @Test
    void deveResponder404AoListarDisciplinaInexistenteEmVezDeListaVazia() throws Exception {
        mockMvc.perform(get("/api/disciplinas/999/faltas"))
                .andExpect(status().isNotFound());
    }

    private ResultActions registrar(long disciplinaId, LocalDate data, String horas, String observacao) throws Exception {
        String json = observacao == null
                ? "{\"data\": \"%s\", \"horas\": %s}".formatted(data, horas)
                : "{\"data\": \"%s\", \"horas\": %s, \"observacao\": \"%s\"}".formatted(data, horas, observacao);
        return mockMvc.perform(post("/api/disciplinas/" + disciplinaId + "/faltas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));
    }
}
