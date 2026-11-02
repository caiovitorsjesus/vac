package com.caiovitor.vac.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

class DisciplinaIntegracaoTest extends IntegracaoTest {

    private static final String CALCULO = """
            {"nome": "Cálculo I", "cargaHorariaTotal": 60, "percentualMaximoFaltas": 25,
             "mediaMinima": 7, "notaMaxima": 10}
            """;

    @Test
    void deveCriarDisciplinaComLocation() throws Exception {
        criar(CALCULO)
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/disciplinas/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Cálculo I"))
                .andExpect(jsonPath("$.cargaHorariaTotal").value(60))
                .andExpect(jsonPath("$.percentualMaximoFaltas").value(25))
                .andExpect(jsonPath("$.mediaMinima").value(7))
                .andExpect(jsonPath("$.notaMaxima").value(10));
    }

    @Test
    void deveRemoverEspacosDoNomePeloDominio() throws Exception {
        criar(CALCULO.replace("\"Cálculo I\"", "\"  Cálculo I  \""))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Cálculo I"));
    }

    @Test
    void deveListarDisciplinas() throws Exception {
        criar(CALCULO);
        criar(CALCULO.replace("Cálculo I", "Física"));

        mockMvc.perform(get("/api/disciplinas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nome").value("Cálculo I"))
                .andExpect(jsonPath("$[1].nome").value("Física"));
    }

    @Test
    void deveBuscarDisciplinaPorId() throws Exception {
        criar(CALCULO);

        mockMvc.perform(get("/api/disciplinas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Cálculo I"));
    }

    @Test
    void deveResponder404ParaDisciplinaInexistente() throws Exception {
        mockMvc.perform(get("/api/disciplinas/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Disciplina 999 não encontrada."));
    }

    @Test
    void deveResponder400ComMensagemDoDominio() throws Exception {
        criar(CALCULO.replace("\"cargaHorariaTotal\": 60", "\"cargaHorariaTotal\": 0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("A carga horária total deve ser maior que zero."));
    }

    // as mensagens do Bean Validation dependem do idioma do sistema; o teste verifica só o campo
    @Test
    void deveResponder400ComCampoAusente() throws Exception {
        criar("""
                {"cargaHorariaTotal": 60, "percentualMaximoFaltas": 25, "mediaMinima": 7, "notaMaxima": 10}
                """)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos.nome").exists());
    }

    @Test
    void deveResponder400ComMaisDeDuasCasasDecimais() throws Exception {
        criar(CALCULO.replace("\"mediaMinima\": 7", "\"mediaMinima\": 7.125"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos.mediaMinima").exists());
    }

    @Test
    void deveResponder400ParaJsonMalformado() throws Exception {
        criar("{\"nome\":")
                .andExpect(status().isBadRequest());
    }

    private ResultActions criar(String json) throws Exception {
        return mockMvc.perform(post("/api/disciplinas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));
    }
}
