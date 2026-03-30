package com.library.libraryapi.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Entité JPA représentant un auteur.
 *
 * Côté INVERSE de la relation ManyToMany avec Book.
 * On utilise mappedBy = "authors" car c'est Book qui possède la @JoinTable,
 * donc Book est le côté propriétaire. Author est le côté inverse (mappedBy).
 * mappedBy indique à Hibernate que la relation est déjà définie dans Book.authors,
 * et qu'il ne doit pas créer de deuxième table de jointure ici.
 */
@Entity
@Table(name = "authors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(columnDefinition = "TEXT")
    private String bio;

    private LocalDate birthDate;

    private String nationality;

    /**
     * Côté INVERSE du ManyToMany : Book est le propriétaire (il a @JoinTable).
     * mappedBy = "authors" signifie que c'est le champ "authors" dans Book qui gère la relation.
     * Hibernate ne crée pas de table de jointure ici, il la lit depuis Book.
     *
     * @Builder.Default initialise la collection pour éviter les NullPointerException
     * avec le pattern builder de Lombok.
     */
    @ManyToMany(mappedBy = "authors")
    @Builder.Default
    private Set<Book> books = new HashSet<>();
}
