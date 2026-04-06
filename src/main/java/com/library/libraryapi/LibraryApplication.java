package com.library.libraryapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Point d'entrée de l'application Spring Boot « Library API ».
 *
 * <p>{@code @SpringBootApplication} regroupe trois annotations essentielles :
 * <ul>
 *   <li>{@code @Configuration} — désigne cette classe comme source de beans Spring.</li>
 *   <li>{@code @EnableAutoConfiguration} — active la configuration automatique de Spring Boot
 *       (datasource, JPA, sécurité OAuth2, etc.) selon les dépendances présentes dans le
 *       classpath ({@code pom.xml}).</li>
 *   <li>{@code @ComponentScan} — scanne récursivement ce package et ses sous-packages
 *       pour détecter les {@code @RestController}, {@code @Service}, {@code @Repository},
 *       {@code @Configuration}, etc.</li>
 * </ul>
 *
 * <p>La configuration applicative est externalisée dans
 * {@code src/main/resources/application.properties} (datasource MySQL, issuer Keycloak).
 * La configuration de la sécurité est dans {@code SecurityConfig}, le CORS dans
 * {@code WebConfig}, et le logging structuré (ELK) dans {@code logback-spring.xml}.
 */
@SpringBootApplication
public class LibraryApplication {

    public static void main(String[] args) {
        SpringApplication.run(LibraryApplication.class, args);
    }

}
