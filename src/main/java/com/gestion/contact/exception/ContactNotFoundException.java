package com.gestion.contact.exception;

/**
 * Exception levée lorsqu'un contact n'est pas trouvé dans la base de données
 * 
 * @author Personne 2 - Couche Service
 * @version 1.0
 */
public class ContactNotFoundException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Constructeur avec message personnalisé
     * 
     * @param message Le message d'erreur
     */
    public ContactNotFoundException(String message) {
        super(message);
    }
    
    /**
     * Constructeur avec ID du contact non trouvé
     * 
     * @param id L'identifiant du contact
     */
    public ContactNotFoundException(Long id) {
        super("Contact non trouvé avec l'ID : " + id);
    }
    
    /**
     * Constructeur avec message et cause
     * 
     * @param message Le message d'erreur
     * @param cause La cause de l'exception
     */
    public ContactNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
