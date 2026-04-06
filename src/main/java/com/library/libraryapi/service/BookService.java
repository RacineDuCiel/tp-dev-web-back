package com.library.libraryapi.service;

import com.library.libraryapi.dto.request.BookRequest;
import com.library.libraryapi.dto.response.BookResponse;
import com.library.libraryapi.dto.response.IllustrationResponse;
import com.library.libraryapi.entity.Author;
import com.library.libraryapi.entity.Book;
import com.library.libraryapi.exception.ResourceNotFoundException;
import com.library.libraryapi.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service métier pour la gestion des livres.
 * Dépend de AuthorService pour résoudre les authorIds en entités Author.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorService authorService;

    @Transactional(readOnly = true)
    public List<BookResponse> getAll() {
        return bookRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public BookResponse getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    @Transactional(readOnly = true)
    public List<BookResponse> search(String query) {
        return bookRepository.findByTitleContainingIgnoreCase(query).stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Crée un livre et lui associe les auteurs fournis via leurs IDs.
     * On utilise book.addAuthor() pour synchroniser les deux côtés de la relation.
     */
    public BookResponse create(BookRequest request) {
        Book book = Book.builder()
                .title(request.getTitle())
                .isbn(request.getIsbn())
                .publicationDate(request.getPublicationDate())
                .summary(request.getSummary())
                .pageCount(request.getPageCount())
                .genre(request.getGenre())
                .build();

        // Résoudre les IDs en entités Author et synchroniser la relation bidirectionnelle
        if (request.getAuthorIds() != null) {
            request.getAuthorIds().forEach(authorId -> {
                Author author = authorService.findOrThrow(authorId);
                book.addAuthor(author);
            });
        }

        BookResponse response = toResponse(bookRepository.save(book));
        log.info("Livre créé : id={}, titre='{}'", response.getId(), response.getTitle());
        return response;
    }

    /**
     * Met à jour un livre.
     * On retire d'abord tous les anciens auteurs, puis on ajoute les nouveaux.
     * Cette approche garantit la synchronisation complète de la relation.
     */
    public BookResponse update(Long id, BookRequest request) {
        Book book = findOrThrow(id);

        book.setTitle(request.getTitle());
        book.setIsbn(request.getIsbn());
        book.setPublicationDate(request.getPublicationDate());
        book.setSummary(request.getSummary());
        book.setPageCount(request.getPageCount());
        book.setGenre(request.getGenre());

        // Retirer les anciens auteurs (synchronise les deux côtés)
        Set<Author> currentAuthors = Set.copyOf(book.getAuthors());
        currentAuthors.forEach(book::removeAuthor);

        // Ajouter les nouveaux auteurs
        if (request.getAuthorIds() != null) {
            request.getAuthorIds().forEach(authorId -> {
                Author author = authorService.findOrThrow(authorId);
                book.addAuthor(author);
            });
        }

        BookResponse response = toResponse(bookRepository.save(book));
        log.info("Livre mis à jour : id={}, titre='{}'", response.getId(), response.getTitle());
        return response;
    }

    /**
     * Supprime un livre.
     * On retire le livre de tous ses auteurs avant suppression pour respecter
     * l'intégrité de la relation ManyToMany côté Author.
     */
    public void delete(Long id) {
        Book book = findOrThrow(id);
        Set<Author> authors = Set.copyOf(book.getAuthors());
        authors.forEach(book::removeAuthor);
        bookRepository.delete(book);
        log.info("Livre supprimé : id={}, titre='{}'", id, book.getTitle());
    }

    /**
     * Méthode utilitaire publique — réutilisée par IllustrationService.
     */
    public Book findOrThrow(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Livre", id));
    }

    /** Convertit une entité Book en DTO BookResponse, incluant auteurs et illustrations. */
    private BookResponse toResponse(Book book) {
        List<BookResponse.AuthorSummary> authorSummaries = book.getAuthors().stream()
                .map(a -> BookResponse.AuthorSummary.builder()
                        .id(a.getId())
                        .firstName(a.getFirstName())
                        .lastName(a.getLastName())
                        .build())
                .toList();

        List<IllustrationResponse> illustrationResponses = book.getIllustrations().stream()
                .map(ill -> IllustrationResponse.builder()
                        .id(ill.getId())
                        .title(ill.getTitle())
                        .description(ill.getDescription())
                        .imageUrl(ill.getImageUrl())
                        .bookId(book.getId())
                        .bookTitle(book.getTitle())
                        .build())
                .toList();

        return BookResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .isbn(book.getIsbn())
                .publicationDate(book.getPublicationDate())
                .summary(book.getSummary())
                .pageCount(book.getPageCount())
                .genre(book.getGenre())
                .authors(authorSummaries)
                .illustrations(illustrationResponses)
                .build();
    }
}
