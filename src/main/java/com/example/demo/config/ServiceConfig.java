package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ServiceConfig {

    /**
     * Configure the REST client here. Customize the underlying HTTP client here as needed, ie. socket timeout, etc.
     *
     * @return
     */
    @Bean
    public WebClient webClient() {
        return WebClient.create();
    }
}
