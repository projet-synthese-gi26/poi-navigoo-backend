package com.poi.yow_point.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class PasswordConfig {

    /**
     * Bean pour encoder les mots de passe avec BCrypt
     * BCrypt est recommandé pour sa sécurité et sa résistance aux attaques par
     * force brute
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12); // Facteur de coût de 12 pour un bon équilibre sécurité/performance
    }
}