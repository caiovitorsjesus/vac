package com.caiovitor.vac.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

class AvaliacaoIntegracaoTest extends IntegracaoTest {

    @BeforeEach
    void prepararDisciplina() throws Exception {
        criarDisciplina();
    }

    @Test
    void deveCriarAvaliacaoComNota() throws Exception {
        criar(1, "{\"nome\": \"P1\", \"pesoPercentual\": 40, \"nota\": 5}")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("P1"))
                .andExpect(jsonPath("$.pesoPercentual").value(40))
                .andExpect(jsonPath("$.nota").value(5));
    }

    @Test
    void deveCriarAvaliacaoPendente() throws Exception {
        criar(1, "{\"nome\": \"P2\", \"pesoPercentual\": 60}")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nota").doesNotExist());
    }

    @Test
    void deveResponder409QuandoPesosPassariamDeCem() throws Exception {
        criar(1, "{\"nome\": \"P1\", \"pesoPercentual\": 40, \"nota\": 5}");
        criar(1, "{\"nome\": \"P2\", \"pesoPercentual\": 33.5}");

        criar(1, "{\"nome\": \"P3\", \"pesoPercentual\": 30}")
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value("Os pesos já somam 73.5%. Restam 26.5% para novas avaliações."));
    }

    @Test
    void deveResponder400ParaPesoZero() throws Exception {
        criar(1, "{\"nome\": \"P1\", \"pesoPercentual\": 0}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("O peso percentual da avaliação deve ser maior que 0 e no máximo 100."));
    }

    @Test
    void deveResponder400ParaNotaAcimaDaMaximaNaCriacao() throws Exception {
        criar(1, "{\"nome\": \"P1\", \"pesoPercentual\": 40, \"nota\": 10.5}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value(
                        "A nota da avaliação deve ser maior ou igual a zero e no máximo igual à nota máxima."));
    }

    @Test
    void deveListarAvaliacoesNaOrdemDeCadastro() throws Exception {
        criar(1, "{\"nome\": \"P1\", \"pesoPercentual\": 40, \"nota\": 5}");
        criar(1, "{\"nome\": \"P2\", \"pesoPercentual\": 60}");

        mockMvc.perform(get("/api/disciplinas/1/avaliacoes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nome").value("P1"))
                .andExpect(jsonPath("$[1].nome").value("P2"));
    }

    // o GET depois do PATCH lê do banco: prova que o UPDATE automático do @Transactional foi confirmado
    @Test
    void deveRegistrarNotaEGravarNoBanco() throws Exception {
        criar(1, "{\"nome\": \"P2\", \"pesoPercentual\": 60}");

        registrarNota(1, 1, "{\"nota\": 8}")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nota").value(8));

        mockMvc.perform(get("/api/disciplinas/1/avaliacoes"))
                .andExpect(jsonPath("$[0].nota").value(8.0));
    }

    @Test
    void naoDeveAlterarNotaQuandoNovaNotaEInvalida() throws Exception {
        criar(1, "{\"nome\": \"P1\", \"pesoPercentual\": 40, \"nota\": 5}");

        registrarNota(1, 1, "{\"nota\": 11}")
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/api/disciplinas/1/avaliacoes"))
                .andExpect(jsonPath("$[0].nota").value(5.0));
    }

    @Test
    void deveResponder400AoRegistrarNotaVazia() throws Exception {
        criar(1, "{\"nome\": \"P1\", \"pesoPercentual\": 40}");

        registrarNota(1, 1, "{}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos.nota").exists());
    }

    @Test
    void deveResponder404AoRegistrarNotaDeAvaliacaoInexistente() throws Exception {
        registrarNota(1, 99, "{\"nota\": 8}")
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Avaliação 99 não encontrada na disciplina 1."));
    }

    @Test
    void naoDeveAlterarAvaliacaoAcessadaPorOutraDisciplina() throws Exception {
        criarDisciplina();
        criar(1, "{\"nome\": \"P1\", \"pesoPercentual\": 40, \"nota\": 5}");

        registrarNota(2, 1, "{\"nota\": 8}")
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Avaliação 1 não encontrada na disciplina 2."));

        mockMvc.perform(get("/api/disciplinas/1/avaliacoes"))
                .andExpect(jsonPath("$[0].nota").value(5.0));
    }

    @Test
    void deveResponder404AoListarDisciplinaInexistente() throws Exception {
        mockMvc.perform(get("/api/disciplinas/999/avaliacoes"))
                .andExpect(status().isNotFound());
    }

    private ResultActions criar(long disciplinaId, String json) throws Exception {
        return mockMvc.perform(post("/api/disciplinas/" + disciplinaId + "/avaliacoes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));
    }

    private ResultActions registrarNota(long disciplinaId, long avaliacaoId, String json) throws Exception {
        return mockMvc.perform(patch("/api/disciplinas/" + disciplinaId + "/avaliacoes/" + avaliacaoId + "/nota")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));
    }
}
