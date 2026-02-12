package com.gestion.contact.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Gestionnaire global des exceptions pour l'application
 * Capture et formate toutes les exceptions levées par les contrôleurs
 * 
 * @author Personne 2 - Couche Service
 * @version 1.0
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    /**
     * Gère les exceptions ContactNotFoundException
     * Retourne un code HTTP 404 (Not Found)
     * 
     * @param ex L'exception levée
     * @param request La requête web
     * @return ResponseEntity avec ErrorResponse et status 404
     */
    @ExceptionHandler(ContactNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleContactNotFoundException(
            ContactNotFoundException ex, WebRequest request) {
        
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            "Not Found",
            ex.getMessage(),
            extractPath(request)
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
    
    /**
     * Gère les exceptions DuplicateContactException
     * Retourne un code HTTP 409 (Conflict)
     * 
     * @param ex L'exception levée
     * @param request La requête web
     * @return ResponseEntity avec ErrorResponse et status 409
     */
    @ExceptionHandler(DuplicateContactException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateContactException(
            DuplicateContactException ex, WebRequest request) {
        
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.CONFLICT.value(),
            "Conflict",
            ex.getMessage(),
            extractPath(request)
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }
    
    /**
     * Gère les exceptions InvalidContactException
     * Retourne un code HTTP 400 (Bad Request)
     * 
     * @param ex L'exception levée
     * @param request La requête web
     * @return ResponseEntity avec ErrorResponse et status 400
     */
    @ExceptionHandler(InvalidContactException.class)
    public ResponseEntity<ErrorResponse> handleInvalidContactException(
            InvalidContactException ex, WebRequest request) {
        
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Bad Request",
            ex.getMessage(),
            extractPath(request)
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Gère les erreurs de validation (annotations @Valid)
     * Retourne un code HTTP 400 (Bad Request)
     * 
     * @param ex L'exception de validation
     * @param request La requête web
     * @return ResponseEntity avec ErrorResponse et détails de validation
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex, WebRequest request) {
        
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Validation Failed",
            "La validation des données a échoué. Veuillez vérifier les champs.",
            extractPath(request)
        );
        
        // Ajouter les détails de chaque erreur de validation
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        errorResponse.setDetails(errors);
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Gère les IllegalArgumentException
     * Retourne un code HTTP 400 (Bad Request)
     * 
     * @param ex L'exception levée
     * @param request La requête web
     * @return ResponseEntity avec ErrorResponse et status 400
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request) {
        
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Bad Request",
            ex.getMessage(),
            extractPath(request)
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Gère toutes les autres exceptions non gérées
     * Retourne un code HTTP 500 (Internal Server Error)
     * 
     * @param ex L'exception levée
     * @param request La requête web
     * @return ResponseEntity avec ErrorResponse et status 500
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex, WebRequest request) {
        
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Internal Server Error",
            "Une erreur interne est survenue. Veuillez contacter l'administrateur.",
            extractPath(request)
        );
        
        // En mode développement, on peut ajouter le message d'erreur complet
        errorResponse.addDetail("exceptionType", ex.getClass().getSimpleName());
        errorResponse.addDetail("exceptionMessage", ex.getMessage());
        
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    /**
     * Extrait le chemin de la requête
     * 
     * @param request La requête web
     * @return Le chemin nettoyé
     */
    private String extractPath(WebRequest request) {
        return request.getDescription(false).replace("uri=", "");
    }
}
