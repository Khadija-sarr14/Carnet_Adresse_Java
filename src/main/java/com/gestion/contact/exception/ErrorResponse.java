package com.gestion.contact.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Classe représentant une réponse d'erreur standardisée pour l'API REST
 * 
 * @author Personne 2 - Couche Service
 * @version 1.0
 */
public class ErrorResponse {
    
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    private Map<String, String> details;
    
    /**
     * Constructeur par défaut
     */
    public ErrorResponse() {
        this.timestamp = LocalDateTime.now();
        this.details = new HashMap<>();
    }
    
    /**
     * Constructeur avec paramètres principaux
     * 
     * @param status Le code HTTP
     * @param error Le type d'erreur
     * @param message Le message d'erreur
     * @param path Le chemin de la requête
     */
    public ErrorResponse(int status, String error, String message, String path) {
        this();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }
    
    // Getters et Setters
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public int getStatus() {
        return status;
    }
    
    public void setStatus(int status) {
        this.status = status;
    }
    
    public String getError() {
        return error;
    }
    
    public void setError(String error) {
        this.error = error;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getPath() {
        return path;
    }
    
    public void setPath(String path) {
        this.path = path;
    }
    
    public Map<String, String> getDetails() {
        return details;
    }
    
    public void setDetails(Map<String, String> details) {
        this.details = details;
    }
    
    /**
     * Ajouter un détail supplémentaire à la réponse d'erreur
     * 
     * @param key La clé du détail
     * @param value La valeur du détail
     */
    public void addDetail(String key, String value) {
        this.details.put(key, value);
    }
}
