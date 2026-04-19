package com.poi.yow_point.config;

import com.poi.yow_point.infrastructure.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration(proxyBeanMethods = false)
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, CorsConfigurationSource corsSource) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(cors -> cors.configurationSource(corsSource))
                .authorizeExchange(exchange -> exchange
                        // 1. AUTHENTIFICATION & TOKENS (Publiques)
                        .pathMatchers(HttpMethod.POST,
                                "/poi-navigoo/api/auth/login",
                                "/poi-navigoo/api/auth/register",
                                "/poi-navigoo/api/auth/refresh"
                        ).permitAll()
                        
                        // 2. GESTION DES MOTS DE PASSE (Publiques)
                        .pathMatchers(HttpMethod.POST,
                                "/poi-navigoo/api/password/check",
                                "/poi-navigoo/api/password/forgot",
                                "/poi-navigoo/api/password/reset",
                                "/poi-navigoo/api/password/verify"
                        ).permitAll()

                        // 3. VÉRIFICATIONS DE FORMULAIRES (Publiques : utiles pour le frontend lors de la frappe)
                        .pathMatchers(HttpMethod.GET,
                                "/poi-navigoo/api/users/check-email/**",
                                "/poi-navigoo/api/users/check-username/**",
                                "/poi-navigoo/api/pois/check-name"
                        ).permitAll()
                        .pathMatchers(HttpMethod.GET, 
                            "/poi-navigoo/api/pois/**", 
                            "/poi-navigoo/api/blogs/**", 
                            "/poi-navigoo/api/podcasts/**", 
                            "/poi-navigoo/api/organizations/**", 
                            "/poi-navigoo/api/reviews/**", 
                            "/poi-navigoo/api-review/**").permitAll()
                        .pathMatchers(HttpMethod.POST, 
                            "/poi-navigoo/api/organizations/**").permitAll()
                        .pathMatchers(HttpMethod.PUT, 
                            "/poi-navigoo/api/organizations/**").permitAll()
                        .pathMatchers(HttpMethod.DELETE, 
                            "/poi-navigoo/api/organizations/**").permitAll()
                        
                        // Documentation and Actuator
                        .pathMatchers("/poi-navigoo/v3/api-docs/**", 
                        "/v3/api-docs/**", 
                        "/poi-navigoo/swagger-ui/**", 
                        "/swagger-ui/**", 
                        "/poi-navigoo/swagger-ui.html", 
                        "/poi-navigoo/webjars/**").permitAll()
                        .pathMatchers("/poi-navigoo/actuator/**").permitAll()
                        
                        // WebSocket endpoints
                        .pathMatchers("/poi-navigoo/ws/**").permitAll()
                        
                        // General fallback
                        .anyExchange().authenticated()
                )
                .addFilterAt(jwtAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
