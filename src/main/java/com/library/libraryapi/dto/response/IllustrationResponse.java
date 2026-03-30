package com.library.libraryapi.dto.response;

import lombok.Builder;
import lombok.Data;

/**
 * DTO de réponse pour une illustration.
 * bookId et bookTitle permettent au front de savoir à quel livre l'illustration appartient
 * sans avoir à charger l'entité Book complète.
 */
@Data
@Builder
public class IllustrationResponse {
    private Long id;
    private String title;
    private String description;
    private String imageUrl;
    private Long bookId;
    private String bookTitle;
}
