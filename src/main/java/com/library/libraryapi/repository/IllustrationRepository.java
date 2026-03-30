package com.library.libraryapi.repository;

import com.library.libraryapi.entity.Illustration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IllustrationRepository extends JpaRepository<Illustration, Long> {

    /**
     * Récupère toutes les illustrations appartenant à un livre donné par son ID.
     * Spring Data génère automatiquement la requête JPQL depuis le nom de la méthode
     * (jointure sur le champ "book.id").
     */
    List<Illustration> findByBookId(Long bookId);
}
