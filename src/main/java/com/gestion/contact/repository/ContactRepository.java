package com.gestion.contact.repository;

import com.gestion.contact.entity.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Repository JPA pour l'entité Contact
 * Fourni par Personne 1 - Configuration + Persistance
 * 
 * @author Personne 1
 * @version 1.0
 */
@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {
    
    /**
     * Rechercher un contact par email
     * 
     * @param email L'email du contact
     * @return Optional contenant le contact si trouvé
     */
    Optional<Contact> findByEmail(String email);
    
    /**
     * Vérifier si un email existe déjà
     * 
     * @param email L'email à vérifier
     * @return true si l'email existe, false sinon
     */
    boolean existsByEmail(String email);
    
    /**
     * Rechercher des contacts par nom (insensible à la casse)
     * 
     * @param nom Le nom à rechercher
     * @return Liste des contacts trouvés
     */
    List<Contact> findByNomContainingIgnoreCase(String nom);
    
    /**
     * Rechercher des contacts par prénom (insensible à la casse)
     * 
     * @param prenom Le prénom à rechercher
     * @return Liste des contacts trouvés
     */
    List<Contact> findByPrenomContainingIgnoreCase(String prenom);
    
    /**
     * Rechercher des contacts par nom ET prénom
     * 
     * @param nom Le nom
     * @param prenom Le prénom
     * @return Liste des contacts trouvés
     */
    List<Contact> findByNomAndPrenom(String nom, String prenom);
}
