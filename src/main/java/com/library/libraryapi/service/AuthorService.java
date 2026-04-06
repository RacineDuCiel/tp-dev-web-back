package com.library.libraryapi.service;

import com.library.libraryapi.dto.request.AuthorRequest;
import com.library.libraryapi.dto.response.AuthorResponse;
import com.library.libraryapi.entity.Author;
import com.library.libraryapi.exception.ResourceNotFoundException;
import com.library.libraryapi.repository.AuthorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service métier pour la gestion des auteurs.
 * @Transactional garantit que toutes les opérations BDD d'une méthode sont atomiques :
 * en cas d'erreur, tout est annulé (rollback automatique).
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthorService {

    private final AuthorRepository authorRepository;

    /** Retourne tous les auteurs. */
    @Transactional(readOnly = true)
    public List<AuthorResponse> getAll() {
        return authorRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    /** Retourne un auteur par son ID, lève ResourceNotFoundException si introuvable. */
    @Transactional(readOnly = true)
    public AuthorResponse getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    /**
     * Recherche les auteurs par prénom ou nom de famille.
     * Le même terme de recherche est passé aux deux paramètres de la méthode custom du repository.
     */
    @Transactional(readOnly = true)
    public List<AuthorResponse> search(String query) {
        return authorRepository
                .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(query, query)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /** Crée un nouvel auteur à partir du DTO de requête. */
    public AuthorResponse create(AuthorRequest request) {
        Author author = Author.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .bio(request.getBio())
                .birthDate(request.getBirthDate())
                .nationality(request.getNationality())
                .build();
        AuthorResponse response = toResponse(authorRepository.save(author));
        log.info("Auteur créé : id={}, nom='{} {}'", response.getId(), response.getFirstName(), response.getLastName());
        return response;
    }

    /** Met à jour un auteur existant. */
    public AuthorResponse update(Long id, AuthorRequest request) {
        Author author = findOrThrow(id);
        author.setFirstName(request.getFirstName());
        author.setLastName(request.getLastName());
        author.setBio(request.getBio());
        author.setBirthDate(request.getBirthDate());
        author.setNationality(request.getNationality());
        AuthorResponse response = toResponse(authorRepository.save(author));
        log.info("Auteur mis à jour : id={}, nom='{} {}'", response.getId(), response.getFirstName(), response.getLastName());
        return response;
    }

    /**
     * Supprime un auteur.
     * Avant de supprimer, on retire l'auteur de tous ses livres pour respecter
     * l'intégrité de la relation ManyToMany (sinon violation de contrainte FK).
     */
    public void delete(Long id) {
        Author author = findOrThrow(id);
        // Synchroniser le côté propriétaire (Book) avant suppression
        author.getBooks().forEach(book -> book.getAuthors().remove(author));
        authorRepository.delete(author);
        log.info("Auteur supprimé : id={}, nom='{} {}'", id, author.getFirstName(), author.getLastName());
    }

    /**
     * Méthode utilitaire publique — réutilisée par BookService pour résoudre les IDs.
     * Lève ResourceNotFoundException si l'auteur n'existe pas.
     */
    public Author findOrThrow(Long id) {
        return authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Auteur", id));
    }

    /** Convertit une entité Author en DTO AuthorResponse. */
    private AuthorResponse toResponse(Author author) {
        return AuthorResponse.builder()
                .id(author.getId())
                .firstName(author.getFirstName())
                .lastName(author.getLastName())
                .bio(author.getBio())
                .birthDate(author.getBirthDate())
                .nationality(author.getNationality())
                .bookCount(author.getBooks().size())
                .build();
    }
}
