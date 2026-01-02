package com.retailsports.stock_service.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configurazione per il RestTemplate con Load Balancing
 */
@Configuration
public class RestClientConfig {

    /**
     * Bean RestTemplate con Load Balancing abilitato
     * Permette di chiamare altri microservizi tramite Eureka usando il nome del servizio
     */
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
