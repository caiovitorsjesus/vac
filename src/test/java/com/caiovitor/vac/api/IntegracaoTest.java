package com.caiovitor.vac.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Base dos testes de integração da API: sobe a aplicação inteira contra o PostgreSQL do
 * {@link TestcontainersConfiguration} e expõe o {@link MockMvc} para as requisições.
 *
 * <p>Antes de cada teste, as tabelas são esvaziadas e os ids reiniciam em 1, então cada teste
 * começa do zero e pode contar com ids previsíveis.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
abstract class IntegracaoTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @BeforeEach
    void limparBanco() {
        jdbcTemplate.execute("TRUNCATE TABLE registro_falta, avaliacao, disciplina RESTART IDENTITY CASCADE");
    }

    /**
     * Cadastra a disciplina do exemplo do guia (60 h, 25% de faltas, média 7, nota máxima 10).
     * Com o banco limpo antes de cada teste, ela recebe o id 1.
     */
    protected void criarDisciplina() throws Exception {
        mockMvc.perform(post("/api/disciplinas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome": "Cálculo I", "cargaHorariaTotal": 60, "percentualMaximoFaltas": 25,
                                 "mediaMinima": 7, "notaMaxima": 10}
                                """))
                .andExpect(status().isCreated());
    }
}
