package com.library.libraryapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Configuration de Spring Security pour l'API REST avec Keycloak comme serveur d'autorisation.
 *
 * L'API est une ressource OAuth2 (Resource Server) : elle ne gère pas les sessions,
 * elle valide uniquement les JWT émis par Keycloak.
 *
 * Pourquoi désactiver CSRF ?
 * L'API REST est stateless (pas de cookies de session), donc CSRF ne s'applique pas.
 * Les requêtes sont authentifiées par le header Authorization: Bearer <token>.
 *
 * Pourquoi CORS ici (et non uniquement dans WebConfig) ?
 * Spring Security intercepte les requêtes AVANT Spring MVC. Il faut donc configurer
 * CORS ici pour que les requêtes préflight OPTIONS passent avant le filtre de sécurité.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Désactiver CSRF : API stateless, pas de session
            .csrf(csrf -> csrf.disable())

            // CORS : autoriser le front React (Vite)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            // Pas de session HTTP : chaque requête est validée via son JWT
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // Toutes les routes /api/** nécessitent un JWT valide
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/**").authenticated()
                .anyRequest().permitAll()
            )

            // Configurer l'API comme Resource Server JWT (Keycloak émet les tokens)
            // Spring Boot récupère automatiquement la clé publique via issuer-uri
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));

        return http.build();
    }

    /**
     * Configuration CORS centralisée ici car Spring Security doit l'appliquer
     * avant que les requêtes atteignent Spring MVC.
     * Le front React (Vite) tourne sur le port 8080.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(
            "http://localhost:8080"
        ));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        return source;
    }
}
