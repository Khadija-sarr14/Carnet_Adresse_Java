package com.gestion.contact.exception;

/**
 * Exception levée lorsqu'on tente de créer un contact avec un email déjà existant
 * 
 * @author Personne 2 - Couche Service
 * @version 1.0
 */
public class DuplicateContactException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Constructeur avec message personnalisé
     * 
     * @param message Le message d'erreur
     */
    public DuplicateContactException(String message) {
        super(message);
    }
    
    /**
     * Constructeur statique pour email en double
     * 
     * @param email L'email déjà existant
     * @return Une nouvelle instance de DuplicateContactException
     */
    public static DuplicateContactException forEmail(String email) {
        return new DuplicateContactException(
            "Un contact avec l'email '" + email + "' existe déjà dans la base de données"
        );
    }
    
    /**
     * Constructeur avec message et cause
     * 
     * @param message Le message d'erreur
     * @param cause La cause de l'exception
     */
    public DuplicateContactException(String message, Throwable cause) {
        super(message, cause);
    }
}
