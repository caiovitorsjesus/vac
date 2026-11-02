package com.caiovitor.vac.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;

class AplicacaoIntegracaoTest extends IntegracaoTest {

    @Test
    void deveSubirAplicacaoComEsquemaDoFlywayNoPostgres() throws Exception {
        // se a migration ou a validação do Hibernate falhassem, a aplicação nem subiria
        mockMvc.perform(get("/api/disciplinas"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }
}
