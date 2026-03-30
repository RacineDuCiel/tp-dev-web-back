package com.library.libraryapi.repository;

import com.library.libraryapi.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    /**
     * Recherche les livres dont le titre contient la chaîne donnée (insensible à la casse).
     * Spring Data génère automatiquement la requête JPQL depuis le nom de la méthode.
     */
    List<Book> findByTitleContainingIgnoreCase(String title);
}
