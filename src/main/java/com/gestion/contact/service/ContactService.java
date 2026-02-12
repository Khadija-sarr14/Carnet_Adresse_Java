package com.gestion.contact.service;

import com.gestion.contact.entity.Contact;
import java.util.List;
import java.util.Optional;

/**
 * Interface définissant les opérations de gestion des contacts
 * 
 * @author Personne 2 - Couche Service
 * @version 1.0
 */
public interface ContactService {
    
    /**
     * Créer un nouveau contact
     * 
     * @param contact Le contact à créer
     * @return Le contact créé avec son ID généré
     * @throws DuplicateContactException si un contact avec le même email existe déjà
     * @throws InvalidContactException si les données du contact sont invalides
     */
    Contact createContact(Contact contact);
    
    /**
     * Récupérer tous les contacts
     * 
     * @return Liste de tous les contacts
     */
    List<Contact> getAllContacts();
    
    /**
     * Récupérer un contact par son ID
     * 
     * @param id L'identifiant du contact
     * @return Le contact trouvé
     * @throws ContactNotFoundException si le contact n'existe pas
     */
    Contact getContactById(Long id);
    
    /**
     * Récupérer un contact par son email
     * 
     * @param email L'email du contact
     * @return Optional contenant le contact si trouvé
     */
    Optional<Contact> getContactByEmail(String email);
    
    /**
     * Rechercher des contacts par nom
     * 
     * @param nom Le nom à rechercher (recherche partielle, insensible à la casse)
     * @return Liste des contacts correspondants
     */
    List<Contact> searchContactsByNom(String nom);
    
    /**
     * Rechercher des contacts par prénom
     * 
     * @param prenom Le prénom à rechercher (recherche partielle, insensible à la casse)
     * @return Liste des contacts correspondants
     */
    List<Contact> searchContactsByPrenom(String prenom);
    
    /**
     * Mettre à jour un contact existant
     * 
     * @param id L'identifiant du contact à mettre à jour
     * @param contactDetails Les nouvelles informations du contact
     * @return Le contact mis à jour
     * @throws ContactNotFoundException si le contact n'existe pas
     * @throws DuplicateContactException si l'email est déjà utilisé par un autre contact
     * @throws InvalidContactException si les données sont invalides
     */
    Contact updateContact(Long id, Contact contactDetails);
    
    /**
     * Supprimer un contact
     * 
     * @param id L'identifiant du contact à supprimer
     * @throws ContactNotFoundException si le contact n'existe pas
     */
    void deleteContact(Long id);
    
    /**
     * Vérifier si un email existe déjà
     * 
     * @param email L'email à vérifier
     * @return true si l'email existe, false sinon
     */
    boolean emailExists(String email);
    
    /**
     * Compter le nombre total de contacts
     * 
     * @return Le nombre de contacts
     */
    long countContacts();
    
    /**
     * Supprimer tous les contacts (à utiliser avec précaution)
     */
    void deleteAllContacts();
}
