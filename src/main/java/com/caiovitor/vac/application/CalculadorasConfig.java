package com.caiovitor.vac.application;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.caiovitor.vac.domain.calculo.CalculadoraFaltas;
import com.caiovitor.vac.domain.calculo.CalculadoraNotas;

/**
 * Registra as calculadoras do domínio como beans do Spring, para que possam ser injetadas nos services.
 *
 * <p>As calculadoras são Java puro, sem anotações do Spring, e continuam assim: é esta classe
 * que diz ao Spring como criá-las, mantendo o domínio independente de framework.
 */
@Configuration
public class CalculadorasConfig {

    @Bean
    public CalculadoraFaltas calculadoraFaltas() {
        return new CalculadoraFaltas();
    }

    @Bean
    public CalculadoraNotas calculadoraNotas() {
        return new CalculadoraNotas();
    }
}
