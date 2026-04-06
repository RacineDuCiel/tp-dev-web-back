package com.library.libraryapi.controller;

import com.library.libraryapi.dto.request.AuthorRequest;
import com.library.libraryapi.dto.response.AuthorResponse;
import com.library.libraryapi.service.AuthorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST exposant les opérations CRUD sur les auteurs.
 *
 * <p>Route de base : {@code /api/authors} (protégée par Keycloak — JWT Bearer requis).
 *
 * <p>Principes appliqués :
 * <ul>
 *   <li>{@code @Valid} déclenche la validation Jakarta Bean Validation sur le corps de la requête
 *       (ex. : {@code @NotBlank}, {@code @NotNull} déclarés dans {@link AuthorRequest}).
 *       Si la validation échoue, Spring renvoie automatiquement un HTTP 400 Bad Request.</li>
 *   <li>On ne manipule jamais les entités JPA ici — uniquement les DTOs {@link AuthorRequest}
 *       (entrée) et {@link AuthorResponse} (sortie). Cela isole la couche HTTP des détails
 *       de la base de données.</li>
 *   <li>{@code @RequiredArgsConstructor} (Lombok) génère un constructeur avec injection
 *       du service, évitant le {@code @Autowired} explicite.</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/authors")
@RequiredArgsConstructor
public class AuthorController {

    private final AuthorService authorService;

    /**
     * Récupère tous les auteurs de la base de données.
     *
     * @return HTTP 200 avec la liste complète des auteurs (peut être vide).
     */
    @GetMapping
    public ResponseEntity<List<AuthorResponse>> getAll() {
        return ResponseEntity.ok(authorService.getAll());
    }

    /**
     * Récupère un auteur par son identifiant.
     *
     * @param id identifiant de l'auteur (extrait du chemin URL)
     * @return HTTP 200 avec les données de l'auteur
     * @throws com.library.libraryapi.exception.ResourceNotFoundException si l'auteur n'existe pas
     *         → intercepté par {@code GlobalExceptionHandler} qui renvoie un HTTP 404
     */
    @GetMapping("/{id}")
    public ResponseEntity<AuthorResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(authorService.getById(id));
    }

    /**
     * Recherche des auteurs par prénom ou nom de famille (insensible à la casse).
     *
     * <p>Délègue au repository qui génère automatiquement la requête JPQL via Spring Data.
     *
     * @param q terme de recherche (paramètre de requête URL, ex. : {@code ?q=hugo})
     * @return HTTP 200 avec la liste des auteurs correspondants (peut être vide)
     */
    @GetMapping("/search")
    public ResponseEntity<List<AuthorResponse>> search(@RequestParam String q) {
        return ResponseEntity.ok(authorService.search(q));
    }

    /**
     * Crée un nouvel auteur.
     *
     * <p>{@code @Valid} valide le corps de la requête avant d'appeler le service.
     *
     * @param request données de l'auteur à créer (prénom et nom obligatoires)
     * @return HTTP 201 Created avec le DTO de l'auteur créé (incluant son {@code id} généré)
     */
    @PostMapping
    public ResponseEntity<AuthorResponse> create(@Valid @RequestBody AuthorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authorService.create(request));
    }

    /**
     * Met à jour un auteur existant (remplacement complet — sémantique PUT).
     *
     * <p>Tous les champs envoyés dans le corps remplacent les valeurs existantes.
     *
     * @param id      identifiant de l'auteur à modifier
     * @param request nouvelles données de l'auteur
     * @return HTTP 200 avec le DTO de l'auteur mis à jour
     * @throws com.library.libraryapi.exception.ResourceNotFoundException si l'auteur n'existe pas
     */
    @PutMapping("/{id}")
    public ResponseEntity<AuthorResponse> update(@PathVariable Long id,
                                                  @Valid @RequestBody AuthorRequest request) {
        return ResponseEntity.ok(authorService.update(id, request));
    }

    /**
     * Supprime un auteur par son identifiant.
     *
     * <p>Le service retire d'abord l'auteur de tous ses livres (relation ManyToMany)
     * avant la suppression pour éviter les violations de contrainte de clé étrangère.
     *
     * @param id identifiant de l'auteur à supprimer
     * @return HTTP 204 No Content (pas de corps dans la réponse — convention REST)
     * @throws com.library.libraryapi.exception.ResourceNotFoundException si l'auteur n'existe pas
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        authorService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
