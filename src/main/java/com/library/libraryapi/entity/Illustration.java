package com.library.libraryapi.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entité JPA représentant une illustration liée à un livre.
 *
 * Côté INVERSE de la relation OneToMany : chaque illustration appartient à un seul livre.
 * Le FetchType.LAZY est utilisé ici pour éviter de charger le livre complet à chaque
 * fois qu'on récupère une illustration. Le livre n'est chargé depuis la BDD que
 * lorsqu'on accède explicitement au champ "book" (chargement différé).
 * C'est une bonne pratique pour éviter les jointures inutiles et optimiser les performances.
 */
@Entity
@Table(name = "illustrations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Illustration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String imageUrl;

    /**
     * Relation ManyToOne vers Book.
     * FetchType.LAZY : le livre n'est PAS chargé automatiquement avec l'illustration.
     * Il n'est récupéré que si on appelle illustration.getBook().
     * Cela évite un SELECT supplémentaire quand on liste les illustrations.
     * @JoinColumn(name = "book_id") crée la colonne FK dans la table "illustrations".
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;
}
