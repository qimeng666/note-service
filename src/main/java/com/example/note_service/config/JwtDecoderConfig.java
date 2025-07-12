package com.example.note_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;

import java.nio.charset.StandardCharsets;

@Configuration
public class JwtDecoderConfig {
    @Bean
    public JwtDecoder jwtDecoder(@Value("${jwt.secret}") String secretKey) {
        // 用 HmacSHA512 对应 HS512
        SecretKey key = new SecretKeySpec(
                secretKey.getBytes(StandardCharsets.UTF_8),
                MacAlgorithm.HS512.getName()
        );
        return NimbusJwtDecoder
                .withSecretKey(key)
                .macAlgorithm(MacAlgorithm.HS512)
                .build();
    }
}
