package com.caiovitor.vac.api;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * PostgreSQL temporário para os testes de integração.
 *
 * <p>O container é criado no início do {@code mvn test} e removido no fim. {@code @ServiceConnection}
 * faz o Spring se conectar a ele (porta e senha geradas na hora), nunca ao banco de desenvolvimento.
 */
@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfiguration {

    @Bean
    @ServiceConnection
    PostgreSQLContainer postgres() {
        // mesma versão do compose.yaml: os testes rodam no mesmo banco da aplicação
        return new PostgreSQLContainer(DockerImageName.parse("postgres:18"));
    }
}
