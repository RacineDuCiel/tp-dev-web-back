package com.library.libraryapi.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * DTO de requête pour créer ou modifier un livre.
 * authorIds contient les IDs des auteurs à associer au livre.
 * Le service se chargera de résoudre ces IDs en entités Author.
 */
@Data
public class BookRequest {

    @NotBlank(message = "Le titre est obligatoire")
    private String title;

    private String isbn;
    private LocalDate publicationDate;
    private String summary;
    private Integer pageCount;
    private String genre;

    // IDs des auteurs à associer — le service résout ces IDs en entités Author
    private Set<Long> authorIds = new HashSet<>();
}
