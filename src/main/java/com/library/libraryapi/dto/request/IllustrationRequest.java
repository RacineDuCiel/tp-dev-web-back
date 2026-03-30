package com.library.libraryapi.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO de requête pour créer ou modifier une illustration.
 * bookId est l'ID du livre auquel l'illustration appartient.
 */
@Data
public class IllustrationRequest {

    @NotBlank(message = "Le titre est obligatoire")
    private String title;

    private String description;

    @NotBlank(message = "L'URL de l'image est obligatoire")
    private String imageUrl;

    @NotNull(message = "L'ID du livre est obligatoire")
    private Long bookId;
}
