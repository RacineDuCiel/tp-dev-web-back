package com.library.libraryapi.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Entité JPA représentant un livre.
 *
 * Book est le côté PROPRIÉTAIRE de la relation ManyToMany avec Author.
 * Il possède la @JoinTable qui définit la table de jointure "book_author".
 * Le côté propriétaire est celui qui a la @JoinTable, c'est lui qui contrôle
 * les insertions/suppressions dans la table de jointure.
 *
 * Book est aussi le côté propriétaire de la relation OneToMany avec Illustration :
 * cascade = ALL signifie que toutes les opérations (persist, merge, remove...)
 * sont propagées aux illustrations. orphanRemoval = true supprime automatiquement
 * les illustrations qui ne sont plus rattachées à aucun livre.
 */
@Entity
@Table(name = "books")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(unique = true)
    private String isbn;

    private LocalDate publicationDate;

    @Column(columnDefinition = "TEXT")
    private String summary;

    private Integer pageCount;

    private String genre;

    /**
     * Côté PROPRIÉTAIRE du ManyToMany avec Author.
     * La @JoinTable définit la table intermédiaire "book_author"
     * avec les colonnes de jointure (book_id → books.id, author_id → authors.id).
     * C'est Book qui gère les insertions dans cette table.
     */
    @ManyToMany
    @JoinTable(
        name = "book_author",
        joinColumns = @JoinColumn(name = "book_id"),
        inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    @Builder.Default
    private Set<Author> authors = new HashSet<>();

    /**
     * Relation OneToMany avec Illustration.
     * cascade = ALL : toute opération sur Book se propage aux illustrations
     *   (utile pour créer un livre avec ses illustrations en une fois).
     * orphanRemoval = true : si on retire une illustration de la liste,
     *   Hibernate la supprime automatiquement de la base.
     */
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Illustration> illustrations = new ArrayList<>();

    // ─── Méthodes utilitaires pour synchroniser les deux côtés de la relation ───

    /**
     * Ajoute un auteur et synchronise les deux côtés de la relation bidirectionnelle.
     * Sans cette synchronisation, le côté inverse (Author.books) ne serait pas
     * mis à jour en mémoire avant le prochain rechargement depuis la BDD.
     */
    public void addAuthor(Author author) {
        this.authors.add(author);
        author.getBooks().add(this);
    }

    /**
     * Retire un auteur et synchronise les deux côtés.
     */
    public void removeAuthor(Author author) {
        this.authors.remove(author);
        author.getBooks().remove(this);
    }

    /**
     * Ajoute une illustration et synchronise les deux côtés.
     */
    public void addIllustration(Illustration illustration) {
        this.illustrations.add(illustration);
        illustration.setBook(this);
    }

    /**
     * Retire une illustration et synchronise les deux côtés.
     */
    public void removeIllustration(Illustration illustration) {
        this.illustrations.remove(illustration);
        illustration.setBook(null);
    }
}
