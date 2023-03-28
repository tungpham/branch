package com.example.demo.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ServiceConfig {

    /**
     * Configure the REST client here. Customize the underlying HTTP client here as needed, ie. socket timeout, etc.
     *
     * Customize the ObjectMapper here to handle timestamp field on JSON response
     *
     * @return
     */
    @Bean
    public WebClient webClient() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        return WebClient.builder()
                .codecs(configurer -> configurer
                        .defaultCodecs()
                        .jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper)))
                .build();
    }
}
