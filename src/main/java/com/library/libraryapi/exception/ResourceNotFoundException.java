package com.library.libraryapi.exception;

/**
 * Exception levée quand une ressource (auteur, livre, illustration) n'est pas trouvée en BDD.
 * Elle est interceptée par GlobalExceptionHandler qui renvoie un HTTP 404 propre au client.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String entityName, Long id) {
        super(entityName + " avec l'id " + id + " introuvable");
    }
}
