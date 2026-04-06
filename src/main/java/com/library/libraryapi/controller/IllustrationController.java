package com.library.libraryapi.controller;

import com.library.libraryapi.dto.request.IllustrationRequest;
import com.library.libraryapi.dto.response.IllustrationResponse;
import com.library.libraryapi.service.IllustrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST exposant les opérations CRUD sur les illustrations.
 *
 * <p>Route de base : {@code /api/illustrations} (protégée par Keycloak — JWT Bearer requis).
 *
 * <p>Une illustration est toujours rattachée à un livre ({@code bookId} obligatoire dans
 * la requête). La relation est gérée côté entité via {@code Book.addIllustration()},
 * ce qui synchronise les deux côtés de la relation OneToMany/ManyToOne.
 *
 * <p>Endpoint spécifique : {@code GET /api/illustrations/book/{bookId}} permet de récupérer
 * toutes les illustrations d'un livre donné — utile pour la page de détail d'un livre
 * sans charger l'intégralité du livre.
 */
@RestController
@RequestMapping("/api/illustrations")
@RequiredArgsConstructor
public class IllustrationController {

    private final IllustrationService illustrationService;

    /**
     * Récupère toutes les illustrations (tous livres confondus).
     *
     * @return HTTP 200 avec la liste complète des illustrations
     */
    @GetMapping
    public ResponseEntity<List<IllustrationResponse>> getAll() {
        return ResponseEntity.ok(illustrationService.getAll());
    }

    /**
     * Récupère une illustration par son identifiant.
     *
     * @param id identifiant de l'illustration
     * @return HTTP 200 avec le DTO de l'illustration
     * @throws com.library.libraryapi.exception.ResourceNotFoundException si l'illustration n'existe pas
     *         → intercepté par {@code GlobalExceptionHandler} → HTTP 404
     */
    @GetMapping("/{id}")
    public ResponseEntity<IllustrationResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(illustrationService.getById(id));
    }

    /**
     * Récupère toutes les illustrations d'un livre donné.
     *
     * <p>Le service vérifie d'abord que le livre existe ({@code bookService.findOrThrow(bookId)})
     * avant d'interroger le repository, ce qui garantit un HTTP 404 propre si le livre
     * est introuvable plutôt qu'une liste vide trompeuse.
     *
     * @param bookId identifiant du livre dont on veut les illustrations
     * @return HTTP 200 avec la liste des illustrations du livre (peut être vide)
     */
    @GetMapping("/book/{bookId}")
    public ResponseEntity<List<IllustrationResponse>> getByBookId(@PathVariable Long bookId) {
        return ResponseEntity.ok(illustrationService.getByBookId(bookId));
    }

    /**
     * Crée une nouvelle illustration et la rattache au livre indiqué par {@code bookId}.
     *
     * <p>Le service récupère l'entité {@code Book} puis appelle {@code book.addIllustration()}
     * pour synchroniser la relation bidirectionnelle OneToMany/ManyToOne.
     *
     * @param request données de l'illustration (titre, URL image et bookId obligatoires)
     * @return HTTP 201 Created avec le DTO de l'illustration créée
     */
    @PostMapping
    public ResponseEntity<IllustrationResponse> create(@Valid @RequestBody IllustrationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(illustrationService.create(request));
    }

    /**
     * Met à jour une illustration existante (remplacement complet — sémantique PUT).
     *
     * <p>Si le {@code bookId} change, le service déplace l'illustration d'un livre à l'autre
     * en appelant {@code oldBook.removeIllustration()} puis {@code newBook.addIllustration()}.
     *
     * @param id      identifiant de l'illustration à modifier
     * @param request nouvelles données de l'illustration
     * @return HTTP 200 avec le DTO de l'illustration mise à jour
     */
    @PutMapping("/{id}")
    public ResponseEntity<IllustrationResponse> update(@PathVariable Long id,
                                                        @Valid @RequestBody IllustrationRequest request) {
        return ResponseEntity.ok(illustrationService.update(id, request));
    }

    /**
     * Supprime une illustration par son identifiant.
     *
     * <p>Grâce à {@code orphanRemoval = true} sur la relation {@code Book.illustrations},
     * retirer l'illustration de la collection du livre dans le service aurait suffi,
     * mais on fait une suppression explicite via le repository par clarté.
     *
     * @param id identifiant de l'illustration à supprimer
     * @return HTTP 204 No Content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        illustrationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
