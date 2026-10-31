package com.caiovitor.vac.api;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
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
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void limparBanco() {
        jdbcTemplate.execute("TRUNCATE TABLE registro_falta, avaliacao, disciplina RESTART IDENTITY CASCADE");
    }
}
