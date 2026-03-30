package com.library.libraryapi.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO de réponse pour un livre.
 * Contient des AuthorSummary (résumé léger) plutôt que des AuthorResponse complets,
 * pour éviter la récursion infinie (Book → Author → books → Book → ...).
 * Les DTOs règlent élégamment ce problème sans @JsonIgnore sur les entités.
 */
@Data
@Builder
public class BookResponse {
    private Long id;
    private String title;
    private String isbn;
    private LocalDate publicationDate;
    private String summary;
    private Integer pageCount;
    private String genre;
    private List<AuthorSummary> authors;
    private List<IllustrationResponse> illustrations;

    /**
     * Résumé léger d'un auteur, utilisé dans le contexte d'un livre.
     * On n'expose que les infos nécessaires pour identifier l'auteur,
     * sans risquer de récursion ou de surcharge de données.
     */
    @Data
    @Builder
    public static class AuthorSummary {
        private Long id;
        private String firstName;
        private String lastName;
    }
}
