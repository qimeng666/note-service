package com.example.note_service.config;

import feign.Logger;
import feign.Request;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Bean
    public Request.Options requestOptions() {
        return new Request.Options(
                5000, // connectTimeoutMillis
                10000, // readTimeoutMillis
                true // followRedirects
        );
    }

    @Bean
    public RequestInterceptor authInterceptor() {
        return new FeignAuthInterceptor();
    }
} 