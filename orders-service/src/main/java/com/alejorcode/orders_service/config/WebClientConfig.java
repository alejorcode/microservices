package com.alejorcode.orders_service.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.server.resource.web.reactive.function.client.ServletBearerExchangeFilterFunction;
import org.springframework.web.reactive.function.client.DefaultClientRequestObservationConvention;
import org.springframework.web.reactive.function.client.WebClient;

import io.micrometer.observation.ObservationRegistry;

@Configuration
public class WebClientConfig {
    @Bean
    @LoadBalanced
    WebClient.Builder webClientBuilder(ObservationRegistry observationRegistry){
		return WebClient.builder()
				.filter(new ServletBearerExchangeFilterFunction())
				.observationRegistry(observationRegistry)
				.observationConvention(new DefaultClientRequestObservationConvention());
	}
 
    @Bean
    WebClient webClient(WebClient.Builder builder) {
    	return builder.build();
    }

}
