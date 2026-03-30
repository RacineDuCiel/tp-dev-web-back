package com.library.libraryapi.repository;

import com.library.libraryapi.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {

    /**
     * Recherche les auteurs dont le prénom OU le nom de famille contient la chaîne donnée
     * (insensible à la casse). Spring Data génère automatiquement la requête JPQL depuis
     * le nom de la méthode.
     */
    List<Author> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(String firstName, String lastName);
}
