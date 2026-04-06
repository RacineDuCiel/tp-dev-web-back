package com.library.libraryapi.service;

import com.library.libraryapi.dto.request.IllustrationRequest;
import com.library.libraryapi.dto.response.IllustrationResponse;
import com.library.libraryapi.entity.Book;
import com.library.libraryapi.entity.Illustration;
import com.library.libraryapi.exception.ResourceNotFoundException;
import com.library.libraryapi.repository.IllustrationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service métier pour la gestion des illustrations.
 * Dépend de BookService pour résoudre le bookId en entité Book.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class IllustrationService {

    private final IllustrationRepository illustrationRepository;
    private final BookService bookService;

    @Transactional(readOnly = true)
    public List<IllustrationResponse> getAll() {
        return illustrationRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public IllustrationResponse getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    /** Retourne toutes les illustrations d'un livre donné. */
    @Transactional(readOnly = true)
    public List<IllustrationResponse> getByBookId(Long bookId) {
        // Vérifie que le livre existe
        bookService.findOrThrow(bookId);
        return illustrationRepository.findByBookId(bookId).stream()
                .map(this::toResponse)
                .toList();
    }

    /** Crée une illustration et la rattache à un livre via addIllustration() (synchronise les deux côtés). */
    public IllustrationResponse create(IllustrationRequest request) {
        Book book = bookService.findOrThrow(request.getBookId());

        Illustration illustration = Illustration.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .imageUrl(request.getImageUrl())
                .build();

        // Utilise la méthode utilitaire de Book pour synchroniser la relation bidirectionnelle
        book.addIllustration(illustration);

        IllustrationResponse response = toResponse(illustrationRepository.save(illustration));
        log.info("Illustration créée : id={}, titre='{}', livre='{}'", response.getId(), response.getTitle(), response.getBookTitle());
        return response;
    }

    /**
     * Met à jour une illustration.
     * Permet de changer le livre associé : on retire l'illustration de l'ancien livre
     * et on l'ajoute au nouveau.
     */
    public IllustrationResponse update(Long id, IllustrationRequest request) {
        Illustration illustration = findOrThrow(id);

        illustration.setTitle(request.getTitle());
        illustration.setDescription(request.getDescription());
        illustration.setImageUrl(request.getImageUrl());

        // Si le bookId a changé, on met à jour la relation
        if (!illustration.getBook().getId().equals(request.getBookId())) {
            Book oldBook = illustration.getBook();
            Book newBook = bookService.findOrThrow(request.getBookId());
            oldBook.removeIllustration(illustration);
            newBook.addIllustration(illustration);
        }

        IllustrationResponse response = toResponse(illustrationRepository.save(illustration));
        log.info("Illustration mise à jour : id={}, titre='{}'", response.getId(), response.getTitle());
        return response;
    }

    /** Supprime une illustration. */
    public void delete(Long id) {
        Illustration illustration = findOrThrow(id);
        illustrationRepository.delete(illustration);
        log.info("Illustration supprimée : id={}, titre='{}'", id, illustration.getTitle());
    }

    public Illustration findOrThrow(Long id) {
        return illustrationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Illustration", id));
    }

    /** Convertit une entité Illustration en DTO IllustrationResponse. */
    private IllustrationResponse toResponse(Illustration illustration) {
        return IllustrationResponse.builder()
                .id(illustration.getId())
                .title(illustration.getTitle())
                .description(illustration.getDescription())
                .imageUrl(illustration.getImageUrl())
                .bookId(illustration.getBook().getId())
                .bookTitle(illustration.getBook().getTitle())
                .build();
    }
}
