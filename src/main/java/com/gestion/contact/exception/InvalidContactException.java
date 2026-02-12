package com.gestion.contact.exception;

/**
 * Exception levée lorsque les données d'un contact sont invalides
 * 
 * @author Personne 2 - Couche Service
 * @version 1.0
 */
public class InvalidContactException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Constructeur avec message personnalisé
     * 
     * @param message Le message d'erreur
     */
    public InvalidContactException(String message) {
        super(message);
    }
    
    /**
     * Constructeur pour un champ invalide spécifique
     * 
     * @param fieldName Le nom du champ invalide
     * @param reason La raison de l'invalidité
     * @return Une nouvelle instance de InvalidContactException
     */
    public static InvalidContactException forField(String fieldName, String reason) {
        return new InvalidContactException(
            "Le champ '" + fieldName + "' est invalide : " + reason
        );
    }
    
    /**
     * Constructeur avec message et cause
     * 
     * @param message Le message d'erreur
     * @param cause La cause de l'exception
     */
    public InvalidContactException(String message, Throwable cause) {
        super(message, cause);
    }
}
