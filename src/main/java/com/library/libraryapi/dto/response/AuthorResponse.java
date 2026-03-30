package com.library.libraryapi.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

/**
 * DTO de réponse pour un auteur.
 * bookCount est calculé à partir de la taille de la collection books de l'entité.
 * On n'expose pas la liste complète des livres ici pour éviter la récursion infinie
 * (Book contient des Author, Author contiendrait des Book qui contiennent des Author...).
 */
@Data
@Builder
public class AuthorResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String bio;
    private LocalDate birthDate;
    private String nationality;
    private int bookCount;
}
