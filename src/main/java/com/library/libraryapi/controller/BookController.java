package com.library.libraryapi.controller;

import com.library.libraryapi.dto.request.BookRequest;
import com.library.libraryapi.dto.response.BookResponse;
import com.library.libraryapi.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST exposant les opérations CRUD sur les livres.
 *
 * <p>Route de base : {@code /api/books} (protégée par Keycloak — JWT Bearer requis).
 *
 * <p>Points notables de la réponse {@link BookResponse} :
 * <ul>
 *   <li>Elle inclut la liste des auteurs sous forme de {@code AuthorSummary} (id, prénom, nom)
 *       — évite la récursion infinie qu'on aurait en sérialisant les entités JPA directement.</li>
 *   <li>Elle inclut toutes les illustrations du livre, permettant de vérifier les deux
 *       relations Hibernate (ManyToMany avec Author, OneToMany avec Illustration) en un seul GET.</li>
 * </ul>
 *
 * <p>Architecture :
 * <ul>
 *   <li>{@code @Valid} + Jakarta Validation → HTTP 400 automatique si le titre est absent.</li>
 *   <li>{@code @RequiredArgsConstructor} (Lombok) → injection par constructeur sans {@code @Autowired}.</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    /**
     * Récupère tous les livres avec leurs auteurs et leurs illustrations.
     *
     * @return HTTP 200 avec la liste complète des livres (peut être vide)
     */
    @GetMapping
    public ResponseEntity<List<BookResponse>> getAll() {
        return ResponseEntity.ok(bookService.getAll());
    }

    /**
     * Récupère un livre par son identifiant, avec auteurs et illustrations.
     *
     * @param id identifiant du livre (extrait du chemin URL)
     * @return HTTP 200 avec le DTO complet du livre
     * @throws com.library.libraryapi.exception.ResourceNotFoundException si le livre n'existe pas
     *         → intercepté par {@code GlobalExceptionHandler} → HTTP 404
     */
    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.getById(id));
    }

    /**
     * Recherche des livres dont le titre contient le terme donné (insensible à la casse).
     *
     * <p>La requête est générée automatiquement par Spring Data depuis le nom de la méthode
     * du repository ({@code findByTitleContainingIgnoreCase}).
     *
     * @param q terme de recherche (paramètre de requête URL, ex. : {@code ?q=miserable})
     * @return HTTP 200 avec la liste des livres correspondants (peut être vide)
     */
    @GetMapping("/search")
    public ResponseEntity<List<BookResponse>> search(@RequestParam String q) {
        return ResponseEntity.ok(bookService.search(q));
    }

    /**
     * Crée un nouveau livre et lui associe les auteurs fournis par leurs IDs.
     *
     * <p>Le service résout chaque {@code authorId} en entité {@code Author} puis
     * appelle {@code book.addAuthor()} pour synchroniser les deux côtés de la
     * relation ManyToMany.
     *
     * @param request données du livre à créer (titre obligatoire, authorIds peut être vide)
     * @return HTTP 201 Created avec le DTO du livre créé (incluant son {@code id} généré)
     */
    @PostMapping
    public ResponseEntity<BookResponse> create(@Valid @RequestBody BookRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookService.create(request));
    }

    /**
     * Met à jour un livre existant (remplacement complet — sémantique PUT).
     *
     * <p>La liste des auteurs est entièrement recalculée : on retire les anciens
     * puis on ajoute les nouveaux, ce qui garantit la cohérence de la table de jointure.
     *
     * @param id      identifiant du livre à modifier
     * @param request nouvelles données du livre
     * @return HTTP 200 avec le DTO du livre mis à jour
     * @throws com.library.libraryapi.exception.ResourceNotFoundException si le livre ou un auteur n'existe pas
     */
    @PutMapping("/{id}")
    public ResponseEntity<BookResponse> update(@PathVariable Long id,
                                                @Valid @RequestBody BookRequest request) {
        return ResponseEntity.ok(bookService.update(id, request));
    }

    /**
     * Supprime un livre et, par cascade JPA (CascadeType.ALL + orphanRemoval),
     * toutes ses illustrations associées.
     *
     * <p>Le service retire d'abord le livre de tous ses auteurs (relation ManyToMany)
     * avant la suppression pour maintenir la cohérence de la table de jointure.
     *
     * @param id identifiant du livre à supprimer
     * @return HTTP 204 No Content (convention REST — pas de corps dans la réponse)
     * @throws com.library.libraryapi.exception.ResourceNotFoundException si le livre n'existe pas
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        bookService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
