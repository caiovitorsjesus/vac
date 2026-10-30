package com.caiovitor.vac;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Ponto de entrada da aplicação.
 *
 * <p>Fica no pacote raiz para que o Spring encontre controllers, services e repositórios
 * em todos os subpacotes. O pacote {@code domain} não usa nenhuma anotação do Spring.
 */
@SpringBootApplication
public class VacApplication {

    public static void main(String[] args) {
        SpringApplication.run(VacApplication.class, args);
    }
}
