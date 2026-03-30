package com.library.libraryapi.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

/**
 * DTO de requête pour créer ou modifier un auteur.
 * On utilise des DTOs pour ne jamais exposer les entités JPA directement dans l'API.
 * Les annotations de validation Jakarta garantissent l'intégrité des données entrantes.
 */
@Data
public class AuthorRequest {

    @NotBlank(message = "Le prénom est obligatoire")
    private String firstName;

    @NotBlank(message = "Le nom de famille est obligatoire")
    private String lastName;

    private String bio;
    private LocalDate birthDate;
    private String nationality;
}
