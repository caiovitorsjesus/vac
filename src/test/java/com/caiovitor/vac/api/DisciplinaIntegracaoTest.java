package com.caiovitor.vac.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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

    @Test
    void deveAtualizarRegrasEGravarNoBanco() throws Exception {
        criar(CALCULO);

        atualizar(1, """
                {"nome": "Cálculo II", "cargaHorariaTotal": 80, "percentualMaximoFaltas": 20,
                 "mediaMinima": 6, "notaMaxima": 10}
                """)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Cálculo II"));

        mockMvc.perform(get("/api/disciplinas/1"))
                .andExpect(jsonPath("$.nome").value("Cálculo II"))
                .andExpect(jsonPath("$.cargaHorariaTotal").value(80.0))
                .andExpect(jsonPath("$.percentualMaximoFaltas").value(20.0))
                .andExpect(jsonPath("$.mediaMinima").value(6.0));
    }

    @Test
    void deveResponder404AoAtualizarDisciplinaInexistente() throws Exception {
        atualizar(999, CALCULO)
                .andExpect(status().isNotFound());
    }

    @Test
    void deveResponder400AoAtualizarComRegraInvalida() throws Exception {
        criar(CALCULO);

        atualizar(1, CALCULO.replace("\"mediaMinima\": 7", "\"mediaMinima\": 11"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value(
                        "A média mínima deve ser maior que zero e no maximo igual à nota máxima."));
    }

    @Test
    void deveResponder400AoAtualizarSemCampoObrigatorio() throws Exception {
        criar(CALCULO);

        atualizar(1, "{\"nome\": \"Cálculo I\"}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos.cargaHorariaTotal").exists());
    }

    @Test
    void deveResponder409QuandoNovaNotaMaximaFicaAbaixoDeNotaLancada() throws Exception {
        criar(CALCULO);
        criarAvaliacao("{\"nome\": \"P1\", \"pesoPercentual\": 40, \"nota\": 8}");

        atualizar(1, CALCULO.replace("\"mediaMinima\": 7", "\"mediaMinima\": 3")
                        .replace("\"notaMaxima\": 10", "\"notaMaxima\": 5"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value("A avaliação P1 tem nota 8, acima da nova nota máxima 5."));

        // a recusa não deixa alteração parcial: a regra antiga continua gravada
        mockMvc.perform(get("/api/disciplinas/1"))
                .andExpect(jsonPath("$.notaMaxima").value(10.0));
    }

    @Test
    void deveAceitarNovaNotaMaximaIgualANotaLancada() throws Exception {
        criar(CALCULO);
        criarAvaliacao("{\"nome\": \"P1\", \"pesoPercentual\": 40, \"nota\": 5}");

        atualizar(1, CALCULO.replace("\"mediaMinima\": 7", "\"mediaMinima\": 3")
                        .replace("\"notaMaxima\": 10", "\"notaMaxima\": 5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.notaMaxima").value(5));
    }

    private ResultActions atualizar(long id, String json) throws Exception {
        return mockMvc.perform(put("/api/disciplinas/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));
    }

    private void criarAvaliacao(String json) throws Exception {
        mockMvc.perform(post("/api/disciplinas/1/avaliacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());
    }

    private ResultActions criar(String json) throws Exception {
        return mockMvc.perform(post("/api/disciplinas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));
    }
}
